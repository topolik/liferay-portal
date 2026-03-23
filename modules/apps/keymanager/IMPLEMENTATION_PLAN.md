# Key Manager Profile Architecture & FIPS Compliance Plan

## Architectural & Design Principles
The Key Manager architecture is governed by the following core principles:

1. **Topology-Driven Orchestration vs. Priority-Based Resolution:** 
   Moving away from implicit, priority-based competition among providers (where the highest priority "wins"). Instead, explicit **Profiles** act as strict blueprints/topologies that route cryptographic operations to specific providers based on their designated roles (KEK, DEK, Secret). This ensures determinism and auditability.
2. **Immutable Cryptographic Boundaries:** 
   Profiles are defined programmatically as OSGi components (`KeyManagerProfile` services). Configuration is only used to *select* the active profile, not to define its internal topology. This prevents runtime misconfigurations, downgrade attacks, and configuration drift.
3. **Fail-Fast & Zero-Trust Validation:** 
   FIPS compliance is enforced as a strict gatekeeper rather than a passive report. If a profile requires FIPS and the underlying JVM or provider topology is not compliant, the configuration change is actively rejected before saving, preventing the system from entering an insecure state.
4. **Envelope Encryption & Segregation of Duties:** 
   Strict adherence to separating Key Encryption Keys (KEK - Root of Trust) and Data Encryption Keys (DEK - Persistence). High-assurance boundaries (HSM/GCP KMS) handle the KEK, while database providers handle the high-throughput DEKs and Secrets.
5. **Multi-Tenancy Isolation:** 
   Profiles explicitly define dual topologies for the System level (`companyId=0`) and Instance level (`companyId>0`), ensuring tenant data boundaries are logically and physically segregated.
6. **Secure by Default & Automation (Bootstrapping):** 
   Local and cloud development environments must be seamless. The system automatically provisions keys, keystores, and dependent OSGi configurations upon profile activation, removing the risk of manual setup errors.

## Phase 0: Bootstrap Property Resolution (Infrastructure Injection)
**Goal:** Resolve critical bootstrap properties (like the database password) without modifying the portal kernel, by leveraging FIPS-compliant infrastructure injection.

1. **Infrastructure Injection Model**
   * This implementation plan treats "Secret Zero" (Pre-OSGi resolution) as an infrastructure-solved problem.
   * **Mechanism:** SRE/Ops must provision bootstrap secrets using a FIPS-compliant Kubernetes mechanism (e.g., External Secrets Operator or CSI Secret Store Driver backed by GCP Secret Manager/HSM).
   * **Injection:** Secrets must be injected into the Liferay container as standard Liferay-prefixed environment variables.
   * **Example:** `LIFERAY_JDBC_PERIOD_DEFAULT_PERIOD_PASSWORD` is injected by K8s, allowing the Liferay DataSource to initialize normally without any custom Key Manager code being active.

2. **Runtime Secret Reference Syntax (`${secretRef:[provider]:[alias]}`)**
   * While bootstrap relies on Env Vars, the Key Manager will still support the `secretRef` syntax for **post-OSGi** configurations (e.g., within System Settings or Third-Party modules).
   * This ensures a consistent developer experience for all non-bootstrap secrets.

3. **GCP Alias Normalization Logic**
   * To ensure consistency between Infrastructure (K8s/GCP) and Liferay, the Key Manager will use a standard normalization rule for all GCP-backed operations:
     * Dots (`.`) and Spaces (` `) are converted to Hyphens (`-`).
     * Any other character not allowed by GCP is converted to an Underscore (`_`).
   * **Example:** `jdbc.default.password` -> `jdbc-default-password`.
   * **Prerequisite:** SREs should use this same normalization when naming secrets in GCP Secret Manager to ensure they match the application's runtime expectations.

---

## Phase 1: Immutability & Cleanup (Removing Implicit Priorities)
**Goal:** Eliminate race conditions by removing configurable provider IDs and priority sorting. Providers will have static identities.

1. **Update Configuration Interfaces (Remove mutable properties)**
   * Remove `providerId` and `priority` methods from all `@Meta.OCD` configuration interfaces (`*Configuration.java`).

2. **Make Provider IDs Static**
   * Add the OSGi property `keymanager.provider.id` directly to the `@Component` annotation of every provider class.
   * Update the `activate` methods to no longer read `providerId` or `priority` from the configuration map.
   * *Target Identities:* `db-system-crypto`, `db-company-crypto`, `db-system-secret`, `db-company-secret`, `keystore-crypto`, `gcp-kms-system-crypto`, `gcp-kms-company-crypto`, `env-secret`, `k8s-secret`.

3. **Strip Priority from SPI and Managers**
   * Remove `int getPriority()` from `CryptoVaultProvider`, `SecretVaultProvider`, `SecretVaultReader`, and `SecretVaultWriter`.
   * In `CryptoManagerImpl` and `SecretManagerImpl`, remove `PropertyServiceReferenceComparator`. The `ServiceTrackerMap` will strictly be a flat lookup table keyed by `keymanager.provider.id`.

---

## Phase 2: The Profile SPI & Orchestrator (Explicit Routing)
**Goal:** Introduce the routing blueprint, enforce it, and manage its lifecycle. All profile-related classes will reside in `profile` packages.

1. **Create the Profile SPI (`keymanager-spi`)**
   * **Package:** `com.liferay.keymanager.spi.profile`
   * **File:** `KeyManagerProfile.java`
   * **Details:** Interface defining the explicit topology and lifecycle hooks.
     ```java
     public interface KeyManagerProfile {
         String getProfileId();
         
         String getSystemKekProviderId();
         String getSystemDekProviderId();
         String getSystemSecretProviderId();
         
         String getCompanyKekProviderId();
         String getCompanyDekProviderId();
         String getCompanySecretProviderId();
         
         boolean isStrictMode();
         boolean requireFips();
         
         // Called by the Orchestrator when this profile is activated
         void bootstrap() throws Exception; 
     }
     ```

2. **Create the Global Profile Configurations (`keymanager-service`)**
   * **Package:** `com.liferay.keymanager.internal.profile.configuration`
   * **File 1:** `KeyManagerGlobalConfiguration.java` (`@Meta.OCD`). Single property: `String activeProfileId() default "custom";` (The default in code is `"custom"`, to be safe if no profile is explicitly selected).
   * **File 2:** `KeyManagerCustomProfileConfiguration.java` (`@Meta.OCD`). Configures the custom mapping.
   * **Developer Override:** Add the following line to `portal-impl/src/portal-developer.properties` so that local developer machines automatically use the `local-dev` profile without affecting production environments:
     `configuration.override.com.liferay.keymanager.internal.profile.configuration.KeyManagerGlobalConfiguration_activeProfileId="local-dev"`

3. **Implement the Custom Profile (`keymanager-service`)**
   * **Package:** `com.liferay.keymanager.internal.profile`
   * **File:** `CustomKeyManagerProfile.java` implementing `KeyManagerProfile`.

4. **Create the Profile Orchestrator (`keymanager-service`)**
   * **Package:** `com.liferay.keymanager.internal.profile`
   * **File:** `ProfileOrchestrator.java` (`@Component`)
   * **Details:**
     * Tracks all registered `KeyManagerProfile` services.
     * Takes `@Reference` to `KeyManagerGlobalConfiguration`.
     * Exposes `getActiveProfile()`.
     * Upon detecting a change to the `activeProfileId` (via OSGi configuration updates), it explicitly references the configurations of the respective profile's providers and invokes the `bootstrap()` method, fully setting them up before fulfilling any KeyManager API requests, eliminating OSGi component activation race conditions.

5. **Refactor Managers to use the Orchestrator**
   * Modify `SecretManagerImpl` and `CryptoManagerImpl` to query `ProfileOrchestrator` when `ANY_PROVIDER` is requested, bifurcating by `companyId == 0` (System) vs `companyId > 0` (Company).
   * Implement strict mode checks for reads.

---

## Phase 3: FIPS Compliance Engine & Configuration Validation
**Goal:** Prevent non-compliant configurations from being saved and ensure the JVM has a certified boundary. This phase establishes the "Infrastructure-Led Mandate" where environment-level policy overrides UI-level configuration.

1. **The Infrastructure Mandate (`LIFERAY_KEYMANAGER_FIPS_ENFORCED`)**
   * Introduce a system environment variable `LIFERAY_KEYMANAGER_FIPS_ENFORCED`.
   * **Mandate Logic:** If set to `true`, the system enters "Strict FedRAMP Mode." This is the ultimate authority and cannot be overridden by any OSGi configuration or database setting.

2. **Define the FIPS Validator SPI (`keymanager-spi`)**
   * **Package:** `com.liferay.keymanager.spi.fips`
   * **File 1:** `FipsReport.java` (POJO for validation results).
     * `boolean isCompliant()`
     * `String getViolationMessage()` (Detailed feedback for the UI/Admin)
   * **File 2:** `FipsValidator.java`
     * `String getConfigurationPid()` (Links the validator to a specific OSGi PID)
     * `FipsReport validate(Map<String, ?> properties)` (Validates proposed config changes)

3. **Implement FIPS Validators in Providers**
   * Every provider (DB, GCP, OS) must implement and register a `FipsValidator` service for its specific PID.
   * **GCP Validator:** Rejects `SOFTWARE` protection levels or weak algorithms in Strict Mode.
   * **DB Validator:** Rejects non-AEAD encryption modes (e.g., enforces `AES/GCM`).
   * **OS/KeyStore Validator:** Returns `compliant=false` for any configuration attempt if FIPS is enforced.

4. **The Omni-Listener Guard (Mandate-Aware)**
   * **Strict Mode (`FIPS_ENFORCED=true`):** The listener acts as a **Firewall**. It intercepts `onBeforeSave` and `onBeforeDelete` for all `com.liferay.keymanager.*` PIDs. If a `FipsValidator` reports non-compliance (e.g., using `SOFTWARE` keys), the listener **REJECTS** the transaction with a `ConfigurationModelListenerException`.
   * **Standard Mode (`FIPS_ENFORCED=false`):** The listener acts as an **Advisor**. It allows configuration changes to proceed but triggers a **Diagnostic Warning** if best practices are violated, ensuring developers aren't blocked by production-level constraints.

5. **Create the FIPS Compliance Checker (`keymanager-service`)**
   * **Package:**  `com.liferay.keymanager.internal.fips`
   * **File:** `FipsComplianceChecker.java`
   * **Boot-Time Gatekeeper:** 
     * If `FIPS_ENFORCED=true`: The bundle **MUST** verify `BCFIPS` is the first provider. If missing, it **fails to start**, halting the portal.
     * If `FIPS_ENFORCED=false`: The bundle logs a "Standard Mode" initialization message and proceeds with available JVM providers.
   * **Continuous Runtime Verification (Heartbeat Check):** In Strict Mode, this check is mandatory before every crypto operation. In Standard Mode, it is disabled to reduce latency.
   * **"Dead is Better than Insecure" Philosophy:** In Strict Mode, there are no escape hatches. If the required FIPS infrastructure is unavailable, the system remains in a failed/blocked state.

6. **Memory Hygiene and Anti-Caching (The Network Blackout Rule)**
   * **The Mandate:** To comply with FIPS 140-2/3 zeroization requirements, the `CryptoManager` and all providers **MUST NOT** cache unwrapped, plaintext Data Encryption Keys (DEKs) or Master Keys in long-lived memory structures (e.g., ConcurrentHashMap, Ehcache).
   * **Zeroization:** Immediately after a cryptographic operation (wrap/unwrap), the byte arrays containing the plaintext key material must be explicitly overwritten using `java.util.Arrays.fill(bytes, (byte) 0)`.
   * **Availability Trade-off:** In the event of a network blackout to cloud KMS providers, the Key Manager will fail to unwrap keys, resulting in application errors. This is an intentional design choice; confidentiality is prioritized over availability in Strict Mode. Resiliency must be handled at the infrastructure/network layer, not via insecure application-level caching.

---

## Phase 4: Local Dev & GCP Auto-Bootstrapping
**Goal:** Seamless developer and cloud experiences through automated provisioning during profile activation, reactive to the environment mandate.

1. **Fix Local KEK Support (`keymanager-provider-os`)**
   * Implement `Cipher.WRAP_MODE` and `Cipher.UNWRAP_MODE` in `KeyStoreCryptoVaultProvider` to protect database-backed DEKs.

2. **Implement Local Dev Profile with Bootstrapping (`keymanager-provider-os`)**
   * **Package:** `com.liferay.keymanager.provider.os.internal.profile`
   * **File:** `LocalDevKeyManagerProfile.java` (`profile.id=local-dev`)
   * **Topology:** `keystore-crypto` -> `db-*-crypto` -> `db-*-secret`.
   * **`bootstrap()` implementation:**
     1. Derives a deterministic password based on a stable local machine identifier and local user identifier.
     2. Checks if `~/.liferay/keymanager/local-master.p12` exists.
     3. If it does not exist, automatically generates a 256-bit AES Master Key.
     4. Uses `ConfigurationAdmin` to update provider configurations.

3. **Implement GCP Profile with Bootstrapping (`keymanager-provider-gcp`)**
   * **Package:** `com.liferay.keymanager.provider.gcp.internal.profile`
   * **File:** `GcpKeyManagerProfile.java` (`profile.id=gcp`)
   * **Topology:** `gcp-kms-system-crypto` -> `db-*-crypto` -> `db-*-secret`.
   * **Mandate-Aware Bootstrapping:**
     1. Validates presence of necessary GCP credentials via Workload Identity.
     2. **Auto-Inference:** Automatically infers the current GCP Project ID.
     3. **Resource Provisioning:**
        * If `FIPS_ENFORCED=true`: Explicitly provisions/verifies KeyRing and CryptoKey with **`ProtectionLevel.HSM`**. Fails if quotas or permissions are missing.
        * If `FIPS_ENFORCED=false`: Provisions/verifies resources with **`ProtectionLevel.SOFTWARE`** (default) to minimize cost and infrastructure friction.
     4. Uses `ConfigurationAdmin` to automatically configure the GCP and DB providers.
     5. Enforces memory hygiene: In all modes, plaintext DEKs are zeroed out immediately after wrapping.

---

## Phase 5: Diagnostic Engine & Troubleshooting
**Goal:** Provide high-signal visibility into the Key Manager's state, enabling administrators to quickly pinpoint misconfigurations, permission gaps, or missing cloud resources without compromising the "Dead is Better" security posture.

1. **The Diagnostic SPI (`keymanager-api`)**
   * **Package:** `com.liferay.keymanager.diagnostic`
   * **Interface:** `KeyManagerDiagnosticTask`
   * **Purpose:** Allows providers and profiles to register "Self-Tests."
   * **Status Levels:** `OK`, `WARN`, `FAIL`.

2. **The Troubleshooting Command Suite (`keymanager-service`)**
   * **Implementation:** OSGi Gogo Shell commands under the `keymanager` scope.
   * **Command `keymanager:status`:** Performs a full sweep of mandates, topology, and connectivity.
   * **Command `keymanager:normalize [alias]`:** Displays the GCP normalization for a property key.

3. **Proactive Boot-Time Probing**
   * During the `bootstrap()` phase, providers perform "Metadata Pings" to verify IAM roles and resources.
   * **Log Hygiene:** Security rejections in Strict Mode include "Remediation Hints."

4. **Health Check Integration**
   * Register a `HealthCheck` that monitors the Key Manager's FIPS status.

---

## Phase 6: Documentation Updates (`README.md`)
**Goal:** Codify the prerequisites and operational requirements for FedRAMP readiness.

1. **Prerequisite Documentation**
   * Update the module's `README.md` to document the external infrastructure prerequisites:
     * **`local-dev` Profile:** Documentation on where the deterministic KeyStore is generated.
     * **`gcp` Profile:**
       * **Phase 0 Prerequisite:** Documentation on bootstrap secret injection via K8s.
       * **Environment Variables:** Document `LIFERAY_KEYMANAGER_FIPS_ENFORCED` and `LIFERAY_KEYMANAGER_ACTIVE_PROFILE`.
       * **Workload Identity:** Instructions on KSA-to-GSA mapping.
       * **IAM Roles:** Requirement for `roles/cloudkms.cryptoKeyEncrypterDecrypter`.
       * **FIPS JVM Setup:** Steps to install and configure Bouncy Castle FIPS (`BCFIPS`).

## Future Enhancements / TODOs

*   **Key Rotation Lifecycle**: Implement lifecycle management for cryptographic keys. This includes periodic rotation of Master Keys (KEKs) and the corresponding re-wrapping of Data Encryption Keys (DEKs) to comply with strict security policies that require key rotation after a certain number of days.
