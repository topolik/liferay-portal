package com.liferay.keymanager;

import com.liferay.keymanager.exception.KeyProviderException;

import java.util.List;

/**
 * SPI interface for key storage backends. Each provider (KeyStore, GCP KMS, etc.)
 * implements this interface and registers as an OSGi service.
 */
public interface KeyProvider {

	/**
	 * Returns the unique identifier for this provider.
	 * Must match the provider segment in key references.
	 */
	String getProviderId();

	/**
	 * Returns a human-readable name for this provider.
	 */
	String getDisplayName();

	/**
	 * Resolves a key alias to the actual secret value.
	 *
	 * @param alias the key alias within this provider
	 * @return the resolved secret as a char array (caller must zero after use)
	 * @throws KeyProviderException if resolution fails
	 */
	char[] resolveKey(String alias) throws KeyProviderException;

	/**
	 * Resolves a key alias to raw bytes (for binary keys/certificates).
	 *
	 * @param alias the key alias
	 * @return the resolved secret as bytes (caller must zero after use)
	 * @throws KeyProviderException if resolution fails
	 */
	byte[] resolveKeyBytes(String alias) throws KeyProviderException;

	/**
	 * Stores a new key in this provider.
	 *
	 * @param alias the alias to store under
	 * @param value the secret value
	 * @throws KeyProviderException if storage fails
	 */
	void storeKey(String alias, char[] value) throws KeyProviderException;

	/**
	 * Deletes a key from this provider.
	 *
	 * @param alias the alias to delete
	 * @throws KeyProviderException if deletion fails
	 */
	void deleteKey(String alias) throws KeyProviderException;

	/**
	 * Checks if a key alias exists in this provider.
	 */
	boolean containsKey(String alias) throws KeyProviderException;

	/**
	 * Lists all key aliases managed by this provider.
	 */
	List<String> listAliases() throws KeyProviderException;

	/**
	 * Returns metadata about a specific key.
	 */
	KeyMetadata getKeyMetadata(String alias) throws KeyProviderException;

	/**
	 * Returns the priority of this provider (lower = higher priority).
	 * Used when multiple providers could handle a reference.
	 */
	default int getPriority() {
		return 100;
	}

	/**
	 * Checks if this provider is currently available and configured.
	 */
	boolean isAvailable();

}