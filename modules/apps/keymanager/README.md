# Liferay DXP Key Manager

The Key Manager is a centralized security foundation for Liferay DXP, designed for strict **FIPS 140-2/3 compliance** and FedRAMP readiness. It establishes a fundamental boundary between **Secrets** (opaque credentials) and **Cryptographic Keys** (material used for functional crypto).

## Architecture Overview

### 1. Dual-Purpose Design
The system is divided into two distinct logical managers:
*   **Secret Manager**: Handles binary blobs like passwords and API tokens. It uses **Envelope Encryption** to ensure that secrets are never stored in plaintext in the database.
*   **Crypto Manager**: Handles functional keys (AES, RSA). It supports **Opaque Operations**, meaning sensitive private material (like a Master Key in GCP KMS) never needs to enter the application's memory.

### 2. Multi-Layered Security (Envelope Encryption)
1.  **Material**: The actual secret or key.
2.  **DEK (Data Encryption Key)**: A unique, transient key that encrypts the material.
3.  **KEK (Key Encryption Key / Master Key)**: A system-wide key that encrypts the DEK. The KEK can be stored in a local File KeyStore (FIPS Level 1) or a Cloud HSM (FIPS Level 3).

### 3. Tenant Isolation
Every secret and key is strictly isolated by Liferay's **`companyId`**. Cross-company access is prevented at both the configuration and persistence layers.

---

## Module Map

| Module | Role |
| :--- | :--- |
| `keymanager-api` | Public interfaces for Liferay developers. |
| `keymanager-spi` | Extension points for adding new backends. |
| `keymanager-service` | Core dispatcher and manager implementations. |
| `keymanager-provider-db` | Multitenant storage for secrets and keys. |
| `keymanager-provider-os` | Integration with local KeyStores, K8s, and Env. |
| `keymanager-provider-gcp` | Integration with Google Cloud KMS and Secret Manager. |

---

## Quick Start for Developers

### Opaque Secret Retrieval
```java
try (SecureSecret secret = _secretManager.getSecret(keyRef)) {
    // use secret.getBytes()
} // memory is zeroed out automatically
```

### Direct Data Encryption
```java
// No knowledge of algorithms or IVs required
byte[] encrypted = _cryptoManager.encrypt(keyRef, plaintext);
```

### Key Reference Syntax
*   **Secret**: `${secretRef:[providerId]:[alias]}`
*   **Key**: `${keyRef:[providerId]:[alias]}`

---

## FIPS/FedRAMP Compliance Notes

To meet strict FIPS and FedRAMP compliance auditing requirements, the Key Manager modules strictly use `java.security.SecureRandom` directly rather than relying on Liferay's custom wrapper (`com.liferay.portal.kernel.security.SecureRandom`). 

Liferay's `SourceFormatter` natively enforces the use of the internal wrapper via the `IllegalImportsCheck`. Because local `source-formatter.properties` exclusions do not support rule-level suppression (only full file exclusions, which degrades code quality by disabling formatting), this enforcement has been explicitly bypassed in the workspace's root `source-formatter-suppressions.xml`:

```xml
<suppress checks="IllegalImportsCheck" files="modules/apps/keymanager/.*" />
```

This global exclusion ensures the Key Manager generates cryptographic material (like IVs and DEKs) via native Java standard libraries, preserving a clean and verifiable audit trail while keeping all other Liferay source code formatting rules active.
