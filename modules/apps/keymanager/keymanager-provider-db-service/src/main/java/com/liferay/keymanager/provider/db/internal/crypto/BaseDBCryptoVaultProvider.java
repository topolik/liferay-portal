/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.db.internal.configuration.DBCompanyCryptoVaultProviderConfiguration;
import com.liferay.keymanager.provider.db.internal.configuration.DBSystemCryptoVaultProviderConfiguration;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.service.KeyEntryLocalService;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import java.sql.Blob;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseDBCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public byte[] decrypt(long companyId, String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				companyId, identifier);

			Key key = _unwrapKey(companyId, keyEntry);

			String cipherSpec = keyEntry.getCipherSpec();

			String transformation = _parseAlgorithm(cipherSpec);

			Cipher cipher = Cipher.getInstance(transformation);

			KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

			if (keyType == KeyType.SECRET) {
				Map<String, String> cipherSpecMap = _parseCipherSpec(
					cipherSpec);

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
	public void deleteKey(long companyId, String identifier)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyEntry keyEntry = _keyEntryLocalService.fetchKeyEntry(
				companyId, identifier);

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
	public byte[] encrypt(long companyId, String identifier, byte[] plaintext)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				companyId, identifier);

			Key key = _unwrapKey(companyId, keyEntry);

			String cipherSpec = keyEntry.getCipherSpec();

			String transformation = _parseAlgorithm(cipherSpec);

			Cipher cipher = Cipher.getInstance(transformation);

			KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

			if (keyType == KeyType.SECRET) {
				Map<String, String> configurationMap = _parseCipherSpec(
					cipherSpec);

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

	@Override
	public String generateAsymmetricKeyPair(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
				_parseKeyAlgorithm(algorithmSpec));

			Map<String, String> specMap = _parseCipherSpec(algorithmSpec);

			int keySize = GetterUtil.getInteger(specMap.get("keySize"), 2048);

			keyPairGenerator.initialize(keySize);

			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			byte[] wrappedPrivateKeyBytes = _cryptoManager.wrap(
				companyId, KeyReference.fromString(_masterKeyReference),
				keyPair.getPrivate());

			_saveKeyEntry(
				companyId, identifier, KeyType.PRIVATE,
				keyPair.getPrivate(
				).getAlgorithm(),
				wrappedPrivateKeyBytes, _masterKeyReference, algorithmSpec);

			_saveKeyEntry(
				companyId, identifier + ".pub", KeyType.PUBLIC,
				keyPair.getPublic(
				).getAlgorithm(),
				keyPair.getPublic(
				).getEncoded(),
				null, algorithmSpec);

			return identifier;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to generate asymmetric key pair: " + identifier,
				exception);
		}
	}

	@Override
	public String generateSecretKey(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(
				_parseKeyAlgorithm(algorithmSpec));

			Map<String, String> specMap = _parseCipherSpec(algorithmSpec);

			int keySize = GetterUtil.getInteger(specMap.get("keySize"), 256);

			keyGenerator.init(keySize);

			SecretKey secretKey = keyGenerator.generateKey();

			byte[] wrappedKeyBytes = _cryptoManager.wrap(
				companyId, KeyReference.fromString(_masterKeyReference),
				secretKey);

			_saveKeyEntry(
				companyId, identifier, KeyType.SECRET,
				secretKey.getAlgorithm(), wrappedKeyBytes, _masterKeyReference,
				algorithmSpec);

			return identifier;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to generate secret key: " + identifier, exception);
		}
	}

	@Override
	public List<String> getKeyIdentifiers(long companyId)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			return _keyEntryLocalService.getKeyIdentifiers(companyId);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list key identifiers", exception);
		}
	}

	@Override
	public CryptoKey getKeyMetadata(long companyId, String identifier)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			KeyEntry keyEntry = _keyEntryLocalService.getKeyEntry(
				companyId, identifier);

			return new CryptoKey(
				KeyReference.fromString(
					StringBundler.concat(
						"${keyRef:", _providerId, ":", identifier, "}")),
				keyEntry.getAlgorithm(), keyEntry.getCipherSpec(),
				keyEntry.getCreateDate(
				).getTime());
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get key metadata for: " + identifier, exception);
		}
	}

	@Override
	public String importSecretKey(
			long companyId, String identifier, byte[] rawKeyMaterial,
			String algorithmSpec)
		throws CryptoManagerException {

		_checkPermission(companyId);

		try {
			String algorithm = _parseKeyAlgorithm(algorithmSpec);

			SecretKey secretKey = new SecretKeySpec(rawKeyMaterial, algorithm);

			byte[] wrappedKeyBytes = _cryptoManager.wrap(
				companyId, KeyReference.fromString(_masterKeyReference),
				secretKey);

			_saveKeyEntry(
				companyId, identifier, KeyType.SECRET,
				secretKey.getAlgorithm(), wrappedKeyBytes, _masterKeyReference,
				algorithmSpec);

			return identifier;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to import secret key: " + identifier, exception);
		}
		finally {
			if (rawKeyMaterial != null) {
				Arrays.fill(rawKeyMaterial, (byte)0);
			}
		}
	}

	@Override
	public int getPriority() {
		return _priority;
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (!_enabled) {
			return false;
		}

		if ((companyId == 0) || (_companyId == companyId)) {
			return true;
		}

		return false;
	}

	@Override
	public Key unwrap(
			long companyId, String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] wrap(long companyId, String identifier, Key keyToWrap)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	/**
	 * @author Tomas Polesovsky
	 */
	public enum KeyType {

		PRIVATE, PUBLIC, SECRET

	}

	protected void activate(Map<String, Object> properties) {
		if (GetterUtil.getBoolean(properties.get("systemScope"))) {
			DBSystemCryptoVaultProviderConfiguration configuration =
				ConfigurableUtil.createConfigurable(
					DBSystemCryptoVaultProviderConfiguration.class, properties);

			_enabled = configuration.enabled();
			_priority = configuration.priority();
			_companyId = PortalInstancePool.getDefaultCompanyId();
			_masterKeyReference = configuration.masterKeyReference();
			_providerId = configuration.providerId();
		}
		else {
			DBCompanyCryptoVaultProviderConfiguration configuration =
				ConfigurableUtil.createConfigurable(
					DBCompanyCryptoVaultProviderConfiguration.class,
					properties);

			_enabled = true;
			_priority = configuration.priority();
			_companyId = ConfigurationFactoryUtil.getCompanyId(
				_companyLocalService, properties);
			_masterKeyReference = configuration.masterKeyReference();
			_providerId = configuration.providerId();
		}
	}

	@Deactivate
	protected void deactivate() {
	}

	private byte[] _blobToBytes(Blob blob) throws Exception {
		try (InputStream inputStream = blob.getBinaryStream()) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			StreamUtil.transfer(inputStream, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private void _checkPermission(long companyId)
		throws CryptoManagerException {

		if (!isAllowedCompany(companyId)) {
			throw new CryptoManagerException(
				"Company " + companyId + " is not allowed to use this provider");
		}
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

		return parts[0].trim();
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

	private String _parseKeyAlgorithm(String algorithmSpec) {
		String algorithm = _parseAlgorithm(algorithmSpec);

		if (algorithm == null) {
			return null;
		}

		int pos = algorithm.indexOf('/');

		if (pos > 0) {
			return algorithm.substring(0, pos);
		}

		return algorithm;
	}

	private void _saveKeyEntry(
			long companyId, String alias, KeyType keyType, String algorithm,
			byte[] blobBytes, String kekReference, String cipherSpec)
		throws Exception {

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

	private Key _unwrapKey(long companyId, KeyEntry keyEntry) throws Exception {
		KeyType keyType = KeyType.valueOf(keyEntry.getKeyType());

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
			companyId, masterKeyRef, encryptedKeyBytes, keyEntry.getAlgorithm(),
			wrappedType);
	}

	private volatile long _companyId;
	private volatile boolean _enabled;
	private volatile int _priority;
		
	@Reference
	protected CompanyLocalService _companyLocalService;

	@Reference
	protected CryptoManager _cryptoManager;

	@Reference
	protected KeyEntryLocalService _keyEntryLocalService;

	private volatile String _masterKeyReference;
	private volatile String _providerId;
	private final SecureRandom _secureRandom = new SecureRandom();

}