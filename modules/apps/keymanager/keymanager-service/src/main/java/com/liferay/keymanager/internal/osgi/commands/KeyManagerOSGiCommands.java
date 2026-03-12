/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.osgi.commands;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKeyMetadata;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Base64;

import java.util.List;

import org.apache.felix.service.command.Descriptor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"osgi.command.function=decrypt", "osgi.command.function=deleteKey",
		"osgi.command.function=deleteSecret", "osgi.command.function=encrypt",
		"osgi.command.function=generateAsymmetricKeyPair",
		"osgi.command.function=generateSecretKey",
		"osgi.command.function=getCryptoProviders",
		"osgi.command.function=getKeyIdentifiers",
		"osgi.command.function=getKeyMetadata",
		"osgi.command.function=getSecret",
		"osgi.command.function=getSecretIdentifiers",
		"osgi.command.function=getSecretProviders",
		"osgi.command.function=importSecretKey",
		"osgi.command.function=putSecret", "osgi.command.scope=keymanager"
	},
	service = OSGiCommands.class
)
public class KeyManagerOSGiCommands implements OSGiCommands {

	@Descriptor("Decrypt data: decrypt <companyId> <keyReferenceString> <base64Ciphertext>")
	public String decrypt(
		long companyId, String keyReferenceString, String base64Ciphertext) {

		try {
			byte[] ciphertext = Base64.decode(base64Ciphertext);

			byte[] plaintext = _cryptoManager.decrypt(
				companyId, _parseKeyReference(keyReferenceString),
				ciphertext);

			return new String(plaintext);
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Delete key: deleteKey <companyId> <keyReferenceString>")
	public String deleteKey(long companyId, String keyReferenceString) {
		try {
			_cryptoManager.deleteKey(
				companyId, _parseKeyReference(keyReferenceString));

			return "Key deleted successfully";
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Delete secret: deleteSecret <companyId> <keyReferenceString>")
	public String deleteSecret(long companyId, String keyReferenceString) {
		try {
			_secretManager.deleteSecret(
				companyId, _parseKeyReference(keyReferenceString));

			return "Secret deleted successfully";
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Encrypt data: encrypt <companyId> <keyReferenceString> <plaintext>")
	public String encrypt(
		long companyId, String keyReferenceString, String plaintext) {

		try {
			byte[] ciphertext = _cryptoManager.encrypt(
				companyId, _parseKeyReference(keyReferenceString),
				plaintext.getBytes());

			return Base64.encode(ciphertext);
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Generate asymmetric key pair: generateAsymmetricKeyPair <companyId> <providerId> <identifier> <algorithmSpec>")
	public String generateAsymmetricKeyPair(
		long companyId, String providerId, String identifier,
		String algorithmSpec) {

		try {
			KeyReference keyReference =
				_cryptoManager.generateAsymmetricKeyPair(
					companyId, providerId, identifier, algorithmSpec);

			return "Generated Key Pair: " + keyReference.toString();
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Generate secret key: generateSecretKey <companyId> <providerId> <identifier> <algorithmSpec>")
	public String generateSecretKey(
		long companyId, String providerId, String identifier,
		String algorithmSpec) {

		try {
			KeyReference keyReference = _cryptoManager.generateSecretKey(
				companyId, providerId, identifier, algorithmSpec);

			return "Generated Key: " + keyReference.toString();
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("List crypto providers: getCryptoProviders <companyId>")
	public void getCryptoProviders(long companyId) {
		try {
			List<String> providers = _cryptoManager.getProviders(companyId);

			for (String provider : providers) {
				System.out.println(provider);
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Descriptor("List key identifiers: getKeyIdentifiers <companyId> <providerId>")
	public void getKeyIdentifiers(long companyId, String providerId) {
		try {
			List<KeyReference> keyReferences = _cryptoManager.getKeyIdentifiers(
				companyId, providerId);

			for (KeyReference keyReference : keyReferences) {
				System.out.println(keyReference);
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Descriptor("Get key metadata: getKeyMetadata <companyId> <keyReferenceString>")
	public String getKeyMetadata(long companyId, String keyReferenceString) {
		try {
			CryptoKeyMetadata cryptoKeyMetadata = _cryptoManager.getKeyMetadata(
				companyId, _parseKeyReference(keyReferenceString));

			return StringBundler.concat(
				"Algorithm: ", cryptoKeyMetadata.getAlgorithm(),
				", Cipher Spec: ", cryptoKeyMetadata.getCipherSpec(),
				", Created: ", cryptoKeyMetadata.getCreationDate());
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Get secret: getSecret <companyId> <keyReferenceString>")
	public String getSecret(long companyId, String keyReferenceString) {
		try (SecureSecret secureSecret = _secretManager.getSecret(
				companyId, _parseKeyReference(keyReferenceString))) {

			return new String(secureSecret.getBytes());
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("List secret identifiers: getSecretIdentifiers <companyId> <providerId>")
	public void getSecretIdentifiers(long companyId, String providerId) {
		try {
			List<KeyReference> keyReferences =
				_secretManager.getSecretIdentifiers(companyId, providerId);

			for (KeyReference keyReference : keyReferences) {
				System.out.println(keyReference);
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Descriptor("List secret providers: getSecretProviders <companyId>")
	public void getSecretProviders(long companyId) {
		try {
			List<String> providers = _secretManager.getProviders(companyId);

			for (String provider : providers) {
				System.out.println(provider);
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Descriptor("Import secret key: importSecretKey <companyId> <providerId> <identifier> <base64RawKey> <algorithmSpec>")
	public String importSecretKey(
		long companyId, String providerId, String identifier,
		String base64RawKey, String algorithmSpec) {

		try {
			KeyReference keyReference = _cryptoManager.importSecretKey(
				companyId, providerId, identifier, Base64.decode(base64RawKey),
				algorithmSpec);

			return "Imported Key: " + keyReference.toString();
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Descriptor("Put secret: putSecret <companyId> <providerId> <identifier> <secretValue>")
	public String putSecret(
		long companyId, String providerId, String identifier,
		String secretValue) {

		try {
			KeyReference keyReference = new KeyReference(
				KeyReference.Type.SECRET, providerId, identifier);

			SecureSecret secureSecret = new SecureSecret(
				keyReference, secretValue.getBytes());

			_secretManager.putSecret(companyId, secureSecret);

			return "Secret stored successfully";
		}
		catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	private KeyReference _parseKeyReference(String value) {
		if (value.startsWith("${") && value.endsWith("}")) {
			return KeyReference.fromString(value);
		}

		if (value.startsWith("keyRef:") || value.startsWith("secretRef:")) {
			return KeyReference.fromString("${" + value + "}");
		}

		return KeyReference.fromString(value);
	}

	@Reference
	private CryptoManager _cryptoManager;

	@Reference
	private SecretManager _secretManager;

}