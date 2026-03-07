# Liferay DXP Key Manager (FIPS-Compliant Architecture)

## 1. Executive Summary
The Key Manager is a centralized security foundation for Liferay DXP, designed with strict **FIPS 140-2/3 compliance** in mind. It establishes a fundamental boundary between **Secrets** (opaque credentials) and **Cryptographic Keys** (material used for cryptographic operations).

## 2. Dual-Purpose Architecture
The module is divided into two distinct subsystems to enforce this boundary.

### 2.1 Secret Manager (Purpose 1)
*   **Domain**: Passwords, API Tokens, OAuth Secrets, Webhook Keys.
*   **Behavior**: Secrets are retrieved as opaque `byte[]` arrays wrapped in a memory-safe `SecureSecret` (AutoCloseable) object, ensuring they are zeroed out immediately after use.
*   **Internal Storage Mechanism (Envelope Encryption)**:
    *   Secrets stored internally are never saved in plaintext.
    *   The system generates a unique **Data Encryption Key (DEK)** and **IV** for each secret.
    *   The secret is encrypted with the DEK.
    *   The DEK is encrypted using a system-wide **Master Key (KEK)** retrieved from the Crypto Manager.

### 2.2 Crypto Manager (Purpose 2)
*   **Domain**: `javax.crypto.SecretKey`, `java.security.PrivateKey`, `java.security.PublicKey`, `java.security.cert.Certificate`.
*   **Behavior**: Cryptographic keys are returned as opaque Java objects. **They cannot be extracted as raw bytes.** This ensures that when backed by a Hardware Security Module (HSM) or a cloud KMS provider, the private material never crosses the cryptographic boundary.

## 3. Developer Perspective (API)
Developers consume the Key Manager via the `keymanager-api` module.

### Secret Consumption
```java
@Reference
private SecretManager _secretManager;

public void connectToDatabase() {
	KeyReference keyReference = KeyReference.fromString(
		"${secretRef:db:jdbc-password}");

	try (SecureSecret dbPassword = _secretManager.getSecret(keyReference)) {
		driver.connect(username, dbPassword.getBytes());
	} // Memory is securely zeroed here
}
```

### Cryptographic Operations
```java
@Reference
private CryptoManager _cryptoManager;

public byte[] encryptData(byte[] plaintext) throws Exception {
	KeyReference keyReference = KeyReference.fromString(
		"${keyRef:dl-keystore:master-key}");

	SecretKey key = _cryptoManager.getSecretKey(keyReference);

	Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

	byte[] iv = new byte[12];
	
	SecureRandom.getInstanceStrong().nextBytes(iv);
	
	cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));

	byte[] ciphertext = cipher.doFinal(plaintext);

	// Always prepend or store the IV alongside the ciphertext

	ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
	byteBuffer.put(iv);
	byteBuffer.put(ciphertext);

	return byteBuffer.array();
}
```

## 4. Implementer Perspective (SPI)
Custom storage backends (e.g., GCP Secret Manager, HashiCorp Vault) are implemented by extending the `keymanager-spi`.

*   **`SecretVaultProvider`**: Implement this to provide a new backend for opaque secrets (e.g., Google Secret Manager).
*   **`CryptoVaultProvider`**: Implement this to provide a new backend for cryptographic keys (e.g., Google Cloud KMS, AWS KMS).

## 5. Deployment Scenarios
*   **Localhost / Testing**: Uses local `PKCS12` keystores for Crypto, and local DB (with local master key) for Secrets.
*   **GCP / Cloud Native**: Uses GCP KMS via `CryptoVaultProvider` for master keys, and GCP Secret Manager via `SecretVaultProvider` for tokens.

## 6. Key Reference Syntax
All consumers use a uniform token syntax based on the type of material required:

*   **Secret References**: `${secretRef:[providerId]:[identifier]}`
    *   Used for opaque credentials (passwords, tokens).
    *   Resolves via `SecretManager`.
    *   *Example*: `${secretRef:db:jdbc-password}`
*   **Crypto References**: `${keyRef:[providerId]:[identifier]}`
    *   Used for cryptographic material (`SecretKey`, `PrivateKey`).
    *   Resolves via `CryptoManager`.
    *   *Example*: `${keyRef:dl-keystore:document-library-master-key}`
