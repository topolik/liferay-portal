# Google Cloud Platform Key Providers

This module provides **FIPS 140-2 Level 3** compliant key and secret management via Google Cloud.

## Providers

### 1. Cloud KMS (FIPS Level 3 KEK Provider)
Integrates with Google Cloud KMS.
*   **Security**: Master Key material **never enters Liferay memory**. Operations are performed inside GCP's Level 3 HSM hardware.
*   **Usage**: Primary Master Key (KEK) source for high-security FedRAMP environments.
*   **Support**: Asymmetric public key retrieval, symmetric data encryption, and remote key wrapping.

### 2. Secret Manager
Integrates with Google Cloud Secret Manager.
*   **Versioning**: Supports the `[alias]:[version]` syntax. Defaults to `latest`.
*   **Data Residency**: Supports **User-Managed Replication** to specific GCP regions.
*   **CMEK**: Supports wrapping cloud secrets with a KMS key for an added chain of trust.

## Configuration
Requires a valid GCP Service Account with the following roles:
*   `roles/cloudkms.cryptoKeyDecrypter`
*   `roles/cloudkms.publicKeyViewer`
*   `roles/secretmanager.secretAccessor`
*   `roles/secretmanager.secretVersionAdder` (if using `putSecret`)
