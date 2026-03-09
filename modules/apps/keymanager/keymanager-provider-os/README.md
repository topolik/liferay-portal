# OS & Infrastructure Key Providers

This module bridges the Key Manager with the underlying host operating system and container orchestration layer.

## Providers

### 1. FileKeyStore (Primary KEK Provider)

Backed by a local filesystem KeyStore (`PKCS12`).
*   **Purpose**: Acts as the local "Root of Trust" for development and standard deployments.
*   **Engine**: BouncyCastle FIPS.
*   **Security**: Supports specialized **Key Wrap (RFC 5649)** modes.

### 2. File Provider (K8s Secrets)

Reads secrets directly from the filesystem.
*   **Standard Path**: Defaults to `/run/secrets`.
*   **Security**: Includes strict **Path Traversal** protection to prevent escaping the secrets directory.
*   **Read-Only**: Secrets are expected to be managed by Kubernetes/Docker.

### 3. Env Provider (Environment Variables)

Reads secrets from OS environment variables.
*   **Security**: Access is restricted to variables matching a mandatory prefix (default: `LIFERAY_SECRET_`).
*   **Read-Only**: Prevents the application from accidentally modifying the host environment.