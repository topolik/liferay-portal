/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.db.internal.configuration.DBCryptoVaultProviderConfiguration;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.service.KeyEntryLocalService;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.nio.ByteBuffer;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.sql.Blob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DBCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = CryptoVaultProvider.class
)
public class DBCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public byte[] decrypt(String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				_getCompanyId(), identifier);

			Key key = _unwrapKey(keyEntry);

			String cipherSpec = keyEntry.getCipherSpec();

			String transformation = _parseAlgorithm(cipherSpec);

			Map<String, String> cipherSpecMap = _parseCipherSpec(cipherSpec);

			Cipher cipher = Cipher.getInstance(transformation);

			KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

			if (keyType == KeyType.SECRET) {
				int ivSize = GetterUtil.getInteger(cipherSpecMap.get("ivSize"));
				int gcmTag = GetterUtil.getInteger(cipherSpecMap.get("gcmTag"));

				ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertext);

				byte[] iv = new byte[ivSize];
				byteBuffer.get(iv);

				byte[] actualCiphertext = new byte[byteBuffer.remaining()];
				byteBuffer.get(actualCiphertext);

				cipher.init(
					Cipher.DECRYPT_MODE, key,
					_getParameterSpec(iv, transformation, gcmTag));

				return cipher.doFinal(actualCiphertext);
			}

			// Asymmetric (No IV handling in standard DBCryptoVaultProvider)

			cipher.init(Cipher.DECRYPT_MODE, key);

			return cipher.doFinal(ciphertext);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to decrypt with key: " + identifier, exception);
		}
	}

	@Override
	public void deleteKey(String identifier) throws CryptoManagerException {
		try {
			KeyEntry keyEntry = _keyEntryLocalService.fetchKeyEntry(
				_getCompanyId(), identifier);

			if (keyEntry != null) {
				_keyEntryLocalService.deleteKeyEntry(keyEntry);
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to delete key: " + identifier, exception);
		}
	}

	@Override
	public byte[] encrypt(String identifier, byte[] plaintext)
		throws CryptoManagerException {

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				_getCompanyId(), identifier);

			Key key = _unwrapKey(keyEntry);

			String cipherSpec = keyEntry.getCipherSpec();

			String transformation = _parseAlgorithm(cipherSpec);

			Map<String, String> configurationMap = _parseCipherSpec(
				cipherSpec);

			Cipher cipher = Cipher.getInstance(transformation);

			KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

			if (keyType == KeyType.SECRET) {
				int ivSize = GetterUtil.getInteger(
					configurationMap.get("ivSize"));
				int gcmTag = GetterUtil.getInteger(
					configurationMap.get("gcmTag"));

				byte[] iv = new byte[ivSize];
				_secureRandom.nextBytes(iv);

				cipher.init(
					Cipher.ENCRYPT_MODE, key,
					_getParameterSpec(iv, transformation, gcmTag));

				byte[] ciphertext = cipher.doFinal(plaintext);

				// Prepend IV to ciphertext

				ByteBuffer byteBuffer = ByteBuffer.allocate(
					iv.length + ciphertext.length);
				byteBuffer.put(iv);
				byteBuffer.put(ciphertext);

				return byteBuffer.array();
			}

			// Asymmetric (Public Key)

			cipher.init(Cipher.ENCRYPT_MODE, key);

			return cipher.doFinal(plaintext);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to encrypt with key: " + identifier, exception);
		}
	}

	public Certificate getCertificate(String identifier)
		throws CryptoManagerException {

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				_getCompanyId(), identifier);

			if (!Objects.equals(keyEntry.getKeyType(), KeyType.CERTIFICATE.name())) {
				return null;
			}

			byte[] encoded = _blobToBytes(keyEntry.getWrappedKeyBlob());

			CertificateFactory certificateFactory =
				CertificateFactory.getInstance("X.509");

			return certificateFactory.generateCertificate(
				new ByteArrayInputStream(encoded));
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get certificate: " + identifier, exception);
		}
	}

	@Override
	public List<String> getKeyIdentifiers() throws CryptoManagerException {
		try {
			return _keyEntryLocalService.getKeyIdentifiers(_getCompanyId());
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list key identifiers", exception);
		}
	}

	public PrivateKey getPrivateKey(String identifier)
		throws CryptoManagerException {

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				_getCompanyId(), identifier);

			if (!Objects.equals(keyEntry.getKeyType(), KeyType.PRIVATE.name())) {
				return null;
			}

			Key key = _unwrapKey(keyEntry);

			if (!(key instanceof PrivateKey)) {
				// Reconstitute from PKCS8 if unwrapped as generic Key
				KeyFactory keyFactory = KeyFactory.getInstance(
					keyEntry.getAlgorithm());

				return keyFactory.generatePrivate(
					new PKCS8EncodedKeySpec(key.getEncoded()));
			}

			return (PrivateKey)key;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get private key: " + identifier, exception);
		}
	}

	@Override
	public PublicKey getPublicKey(String identifier)
		throws CryptoManagerException {

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				_getCompanyId(), identifier);

			KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

			if (keyType == KeyType.PUBLIC) {
				byte[] encoded = _blobToBytes(keyEntry.getWrappedKeyBlob());

				KeyFactory keyFactory = KeyFactory.getInstance(
					keyEntry.getAlgorithm());

				return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
			}

			// If it's a private key, the public key is often available in the
			// certificate

			Certificate certificate = getCertificate(identifier);

			if (certificate != null) {
				return certificate.getPublicKey();
			}

			return null;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get public key: " + identifier, exception);
		}
	}

	@Override
	public void addCertificate(String identifier, Certificate certificate)
		throws CryptoManagerException {

		try {
			_saveKeyEntry(
				identifier, KeyType.CERTIFICATE, "X.509",
				certificate.getEncoded(), null, null);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to put certificate: " + identifier, exception);
		}
	}

	@Override
	public void addPrivateKey(String identifier, CryptoKey privateKey)
		throws CryptoManagerException {

		try {
			byte[] wrappedBytes = _cryptoManager.wrap(
				KeyReference.fromString(_masterKeyReference), privateKey.getKey());

			_saveKeyEntry(
				identifier, KeyType.PRIVATE, privateKey.getKey().getAlgorithm(),
				wrappedBytes, _masterKeyReference, privateKey.getCipherSpec());
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to put private key: " + identifier, exception);
		}
	}

	@Override
	public void addPublicKey(String identifier, CryptoKey publicKey)
		throws CryptoManagerException {

		try {
			_saveKeyEntry(
				identifier, KeyType.PUBLIC, publicKey.getKey().getAlgorithm(),
				publicKey.getKey().getEncoded(), null, publicKey.getCipherSpec());
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to put public key: " + identifier, exception);
		}
	}

	@Override
	public void addSecretKey(String identifier, CryptoKey secretKey)
		throws CryptoManagerException {

		try {
			byte[] wrappedKeyBytes = _cryptoManager.wrap(
				KeyReference.fromString(_masterKeyReference), secretKey.getKey());

			_saveKeyEntry(
				identifier, KeyType.SECRET, secretKey.getKey().getAlgorithm(),
				wrappedKeyBytes, _masterKeyReference, secretKey.getCipherSpec());
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to put secret key: " + identifier, exception);
		}
	}

	@Override
	public Key unwrap(
			String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] wrap(String identifier, Key keyToWrap)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		DBCryptoVaultProviderConfiguration dbCryptoVaultProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				DBCryptoVaultProviderConfiguration.class, properties);

		_masterKeyReference =
			dbCryptoVaultProviderConfiguration.masterKeyReference();
		_providerId = dbCryptoVaultProviderConfiguration.providerId();
	}

	private byte[] _blobToBytes(Blob blob) throws Exception {
		try (InputStream inputStream = blob.getBinaryStream()) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			StreamUtil.transfer(inputStream, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private long _getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}

	private AlgorithmParameterSpec _getParameterSpec(
		byte[] iv, String algorithm, int gcmTag) {

		if (algorithm.contains("/GCM/")) {
			return new GCMParameterSpec(gcmTag, iv);
		}

		return new IvParameterSpec(iv);
	}

	private String _parseAlgorithm(String configuration) {
		if (Validator.isNull(configuration)) {
			return null;
		}

		String[] parts = StringUtil.split(configuration, ";");

		if (parts.length > 0) {
			return parts[0].trim();
		}

		return null;
	}

	private Map<String, String> _parseCipherSpec(String configuration) {
		Map<String, String> map = new HashMap<>();

		if (Validator.isNull(configuration)) {
			return map;
		}

		String[] parts = StringUtil.split(configuration, ";");

		// Skip the first part (the algorithm)
		for (int i = 1; i < parts.length; i++) {
			String[] keyValue = StringUtil.split(parts[i], "=");

			if (keyValue.length == 2) {
				map.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}

		return map;
	}

	private void _saveKeyEntry(
			String alias, KeyType keyType, String algorithm, byte[] blobBytes,
			String kekReference, String cipherSpec)
		throws Exception {

		long companyId = _getCompanyId();

		KeyEntry keyEntry = _keyEntryLocalService.fetchKeyEntry(
			companyId, alias);

		if (keyEntry == null) {
			keyEntry = _keyEntryLocalService.createKeyEntry(0);

			keyEntry.setCompanyId(companyId);
			keyEntry.setAlias(alias);
		}

		keyEntry.setKeyType(keyType.name());
		keyEntry.setAlgorithm(algorithm);
		keyEntry.setWrappedKeyBlob(
			new OutputBlob(
				new ByteArrayInputStream(blobBytes), blobBytes.length));
		keyEntry.setKekReference(kekReference);
		keyEntry.setCipherSpec(cipherSpec);

		_keyEntryLocalService.updateKeyEntry(keyEntry);
	}

	private Key _unwrapKey(KeyEntry keyEntry) throws Exception {
		KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

		if (keyType == KeyType.CERTIFICATE) {
			Certificate certificate = getCertificate(keyEntry.getAlias());

			return certificate.getPublicKey();
		}

		if (keyType == KeyType.PUBLIC) {
			byte[] encoded = _blobToBytes(keyEntry.getWrappedKeyBlob());

			KeyFactory keyFactory = KeyFactory.getInstance(
				keyEntry.getAlgorithm());

			return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
		}

		// SECRET or PRIVATE (Wrapped)

		KeyReference masterKeyRef = KeyReference.fromString(
			keyEntry.getKekReference());

		byte[] encryptedKeyBytes = _blobToBytes(keyEntry.getWrappedKeyBlob());

		int wrappedType = Cipher.SECRET_KEY;

		if (keyType == KeyType.PRIVATE) {
			wrappedType = Cipher.PRIVATE_KEY;
		}

		return _cryptoManager.unwrap(
			masterKeyRef, encryptedKeyBytes, keyEntry.getAlgorithm(),
			wrappedType);
	}

	@Reference
	private CryptoManager _cryptoManager;

	@Reference
	private KeyEntryLocalService _keyEntryLocalService;

	private String _masterKeyReference;
	private String _providerId;
	private final SecureRandom _secureRandom = new SecureRandom();

	/**
	 * @author Tomas Polesovsky
	 */
	public enum KeyType {
	
		CERTIFICATE, PRIVATE, PUBLIC, SECRET;
	
	}
}
