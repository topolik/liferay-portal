# Database Key & Secret Provider

This module provides Liferay-native storage for secrets and keys using **Envelope Encryption**.

## Schema Structure

### SecretEntry (Opaque Secrets)
Stores encrypted credentials (passwords, tokens).
*   **Isolation**: Strictly scoped by `companyId`.
*   **Security**: The payload is encrypted with a Data Encryption Key (DEK). The DEK is then wrapped by a system-wide Master Key (KEK).

### KeyEntry (Cryptographic Keys)
Stores wrapped cryptographic material (AES, RSA).
*   **Metadata**: Stores the `keyType` and `algorithm`.
*   **Policy**: Does not support `wrap`/`unwrap` to prevent infinite recursion. It serves as a data-level encryption provider.

## Multitenancy
This module supports **Liferay Virtual Instances**.
1.  **Configuration**: Each instance can have its own `providerId` and `masterKeyReference`.
2.  **Scaffolding**: Uses `ConfigurationPidMapping` to appear in the "Virtual Instance" settings.
3.  **Finders**: Uses a composite `C_A` (Company and Alias) finder for all database lookups.

## DLKeyStore
Includes a provider that stores a standard Java KeyStore (`.p12` or `.jks`) within the **Liferay Document Library**, providing a bridge between the filesystem and the DB.
