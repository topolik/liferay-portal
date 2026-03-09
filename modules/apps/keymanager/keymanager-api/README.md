# Key Manager API

The `keymanager-api` module provides the primary interfaces for interacting with Liferay's FIPS-compliant security foundation.

## Core Concepts

### 1. KeyReference

All material is addressed via a `KeyReference`. It is a structured token containing:
*   **Type**: `SECRET` (for credentials) or `CRYPTO` (for keys).
*   **Provider ID**: The backend housing the material (e.g., `db`, `gcp-kms`).
*   **Identifier**: The unique name or path of the material.

**Syntax**: `${[type]Ref:[providerId]:[identifier]}`

### 2. SecureSecret

An `AutoCloseable` wrapper for sensitive `byte[]` arrays. It ensures that material is wiped from the JVM heap (`Arrays.fill(bytes, (byte)0)`) as soon as the `close()` method is called.

---

## SecretManager API

Used for managing opaque credentials like passwords or API tokens.

```java
@Reference
private SecretManager _secretManager;

// Consumption
try (SecureSecret secret = _secretManager.getSecret(keyRef)) {
    String password = new String(secret.getBytes());
} // Zeroed here

// Listing
List<String> aliases = _secretManager.getSecretIdentifiers("db");
```

## CryptoManager API

Used for functional cryptographic operations. Unlike standard Java APIs, this interface is **Metadata-Driven** and **Opaque**, meaning it handles algorithms and IVs internally.

```java
@Reference
private CryptoManager _cryptoManager;

// High-level Opaque Encryption (Self-describing result)
byte[] encrypted = _cryptoManager.encrypt(keyRef, plaintext);

// Low-level Key Wrapping (KEK)
byte[] wrapped = _cryptoManager.wrap(masterKeyRef, keyToProtect);
```