# Liferay DXP Key Manager

The Key Manager is a centralized security foundation for Liferay DXP, designed for strict **FIPS 140-2/3 compliance** and FedRAMP readiness. It establishes a fundamental boundary between **Secrets** (opaque values like API keys) and **Keys** (cryptographic primitives like AES or RSA).

## Core Architecture: Profile-Driven Routing

The Key Manager uses a **Profile-Driven Architecture** to manage cryptographic topologies. Instead of relying on implicit provider priorities, all routing is explicitly defined by the active **Key Manager Profile**.

### Active Profile
The active profile is controlled via the **Key Manager Global Configuration**.
- **custom**: Allows manual selection of providers for each role.
- **local-dev**: Automatically provisions a local PKCS12 keystore (~/.liferay/keymanager/local-master.p12) and configures the system to use it.
- **gcp**: Automatically infers GCP Project ID and configures GCP KMS and Secret Manager providers.

### Provider Roles
Each profile defines four primary roles for cryptographic operations:
1. **System KEK (Key Encryption Key)**: The root key used to wrap System Data Encryption Keys.
2. **System DEK (Data Encryption Key)**: The key used for general system-level encryption (e.g., database-backed secrets).
3. **Company KEK**: The root key used to wrap Data Encryption Keys for a specific virtual instance.
4. **Company DEK**: The key used for instance-specific encryption.

## FIPS 140-2/3 Compliance

When `LIFERAY_KEYMANAGER_FIPS_ENFORCED=true` is set in the environment:
- **Strict Mode**: The system mandates that `BCFIPS` is the first security provider.
- **Continuous Verification**: A heartbeat check is performed before every cryptographic operation.
- **Zeroization**: All intermediate plaintext key material is explicitly zeroed out in memory using `Arrays.fill(..., (byte)0)` immediately after use.
- **Configuration Guard**: An Omni-Listener intercepts all provider configurations and rejects non-compliant settings (e.g., non-AEAD modes or SOFTWARE protection in GCP KMS).

## Diagnostic Engine

The Key Manager includes a built-in diagnostic engine for troubleshooting.

### Gogo Shell Commands
- `keymanager:status`: Performs a full sweep of the Key Manager state, including FIPS status, active profile, and individual diagnostic tasks.

## Quick Start (Local Development)

1. Set the following environment variable:
   `export LIFERAY_KEYMANAGER_FIPS_ENFORCED=false` (or `true` if BCFIPS is configured).
2. Start Liferay DXP.
3. In System Settings, navigate to **Key Manager > Global Configuration** and set **Active Profile ID** to `local-dev`.
4. The system will automatically generate a master key and configure the providers.

## Key Reference Syntax

- **Crypto Key**: `${keyRef:[providerId]:[alias]}`
- **Secret**: `${secretRef:[providerId]:[alias]}`
- **Auto-Routing**: `${keyRef:*:my-key}` (routes to the provider defined in the active profile).
