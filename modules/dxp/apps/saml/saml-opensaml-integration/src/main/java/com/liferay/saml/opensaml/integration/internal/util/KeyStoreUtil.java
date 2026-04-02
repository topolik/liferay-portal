/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.saml.runtime.credential.KeyStoreManager;
import com.liferay.saml.runtime.exception.CredentialAuthException;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;

import org.opensaml.security.credential.UsageType;

import javax.security.auth.DestroyFailedException;

/**
 * @author João Victor Alves
 */
public class KeyStoreUtil {

	public static String getAlias(String entityId, UsageType usageType) {
		if (usageType.equals(UsageType.SIGNING)) {
			return entityId;
		}
		else if (usageType.equals(UsageType.ENCRYPTION)) {
			return entityId + "-encryption";
		}

		return entityId;
	}

	public static KeyStore.Entry getKeyStoreEntry(
			String alias, String certificateKeyPassword,
			KeyStoreManager keyStoreManager, SecretManager secretManager)
		throws CredentialAuthException {

		long companyId = CompanyThreadLocal.getCompanyId();

		KeyStore.PasswordProtection keyStorePasswordProtection = null;

		if (certificateKeyPassword != null) {
			if (!KeyReference.isKeyReference(certificateKeyPassword)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"FIPS compliance warning: SAML keystore password " +
							"is not saved in secure storage!");
				}

				keyStorePasswordProtection = new KeyStore.PasswordProtection(
					certificateKeyPassword.toCharArray());
			} 
			else {
				try (SecureSecret secureSecret =
					 secretManager.getSecret(
						 companyId, 
						 KeyReference.fromString(certificateKeyPassword))) {

					if (secureSecret == null) {
						Class<? extends KeyStoreManager> clazz = keyStoreManager.getClass();

						throw new CredentialAuthException.GeneralCredentialAuthException(
							String.format(
								"SecretManager could not resolve keystore entry password reference %s for company %s using %s",
								certificateKeyPassword, companyId, clazz.getSimpleName()),
							new GeneralSecurityException());
					}

					keyStorePasswordProtection =
						new KeyStore.PasswordProtection(
							secureSecret.getChars());
				}
				catch (SecretManagerException secretManagerException) {
					Class<? extends KeyStoreManager> clazz = keyStoreManager.getClass();

					throw new CredentialAuthException.GeneralCredentialAuthException(
						String.format(
							"SecretManager could not resolve keystore password reference for company %s using %s",
							companyId, clazz.getSimpleName()),
						new GeneralSecurityException(secretManagerException));
				}
			}
		}

		try {
			KeyStore keyStore = keyStoreManager.getKeyStore();

			return keyStore.getEntry(alias, keyStorePasswordProtection);
		}
		catch (GeneralSecurityException generalSecurityException) {
			Class<? extends KeyStoreManager> clazz = keyStoreManager.getClass();

			if (generalSecurityException instanceof KeyStoreException) {
				UnrecoverableKeyException unrecoverableKeyException =
					_getCauseThrowable(
						generalSecurityException,
						UnrecoverableKeyException.class);

				if (unrecoverableKeyException != null) {
					throw new CredentialAuthException.InvalidKeyStorePassword(
						String.format(
							"Company %s used an incorrect password to access " +
								"the key store provided by %s",
							companyId, clazz.getSimpleName()),
						unrecoverableKeyException);
				}

				throw new CredentialAuthException.InvalidKeyStore(
					String.format(
						"Company %s could not load the SAML key store " +
							"provided by %s",
						companyId, clazz.getSimpleName()),
					generalSecurityException);
			}

			if (generalSecurityException instanceof UnrecoverableKeyException) {
				throw new CredentialAuthException.InvalidCredentialPassword(
					String.format(
						"Company %s used an incorrect key credential " +
							"password to an entry in the SAML key store " +
								"provided by %s",
						companyId, clazz.getSimpleName()),
					(UnrecoverableKeyException)generalSecurityException);
			}

			throw new CredentialAuthException.GeneralCredentialAuthException(
				String.format(
					"Unknown exception thrown for company %s using %s",
					companyId, clazz.getSimpleName()),
				generalSecurityException);
		}
		finally {
			if (keyStorePasswordProtection != null && 
				!keyStorePasswordProtection.isDestroyed()) {
				
				try {
					keyStorePasswordProtection.destroy();
				}
				catch (DestroyFailedException e) {
					_log.warn(
						"FIPS Compliance warning: Unable to destroy password " +
							"material: " + e.getMessage(), e);
				}
			}
		}
	}

	private static <T> T _getCauseThrowable(
		Throwable throwable, Class<T> exceptionClass) {

		if (throwable == null) {
			return null;
		}

		Throwable causeThrowable = throwable.getCause();

		while (causeThrowable != null) {
			if (exceptionClass.isInstance(causeThrowable)) {
				return (T)causeThrowable;
			}

			causeThrowable = causeThrowable.getCause();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(KeyStoreUtil.class);

}