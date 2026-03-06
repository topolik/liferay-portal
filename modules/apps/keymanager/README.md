# Liferay DXP Key Manager (KMS) Architecture

## 1. Executive Summary
The Key Manager is a centralized security foundation for Liferay DXP. It abstracts secret management, identity impersonation, and sensitive configuration resolution into a pluggable SPI architecture. Its primary goal is **"Zero Plaintext"**: ensuring that no secret (passwords, API keys, tokens) ever resides in plaintext on disk, in environment variables, or in the database.

---

## 2. Architect Perspective: The "Identity-First" Chain
The system is built on a dependency-driven initialization chain that allows the application to "bootstrap" its security from an infrastructure-provided identity.

### 2.1 The Bootstrap Chain
1.  **Phase 1 (Ambient Identity):** Infrastructure providers (GCP ADC, AWS IAM, K8s Service Account) initialize. They require **no secrets**; they fetch short-lived identity tokens from the environment's metadata server.
2.  **Phase 2 (Remote Vaults):** Cloud providers (GCP Secret Manager, HashiCorp Vault) initialize using the Phase 1 identity for authentication.
3.  **Phase 3 (Encrypted Local Storage):** The Java KeyStore (JKS) provider initializes. Its decryption password ("Secret Zero") is fetched dynamically from a Phase 2 provider via a key reference.
4.  **Phase 4 (Consumer Resolution):** DXP components (Elasticsearch, Mail, Databases) receive their configurations, with references resolved by the preceding phases.

### 2.2 Key Reference Syntax
All consumers use a uniform URI-like syntax: `${keyref:providerId/alias[:version]}`.
*   `providerId`: Matches the `getProviderId()` of a registered `KeyProvider`.
*   `alias`: The unique identifier within that provider (e.g., secret name or key alias).
*   `version`: (Optional) Specific version or tag (e.g., `latest`, `2`).

---

## 3. Developer Perspective: SPI & Implementation
Developers interacting with the KMS should focus on the `KeyProvider` SPI and the `KeyResolverService`.

### 3.1 The `KeyProvider` SPI
Every provider must implement `com.liferay.keymanager.KeyProvider`.
*   **Capabilities:** Providers should explicitly declare capabilities (READ, WRITE, DELETE, LIST, VERSIONING).
*   **Memory Safety:** Use `char[]` for all secret data. Implementations must avoid converting secrets to `String` objects which are immutable and persist in the heap.
*   **Token Impersonation:** Identity providers (like ADC) implement `resolveKey("access-token")` to return short-lived OAuth2/JWT tokens instead of static secrets.

### 3.2 The `SecureSecret` Wrapper
Resolved secrets are returned in a `SecureSecret` (AutoCloseable) wrapper.
```java
try (SecureSecret secret = keyResolverService.resolveSecure(reference)) {
    // Use secret.getChars()
} // secret.close() automatically zeroes out the underlying char[]
```

### 3.4 The "Secure Pull" Pattern (Place-of-Use Resolution)
To achieve maximum resistance against **Heap Dump Analysis**, developers should avoid "pushing" resolved secrets into long-lived `String` fields. Instead, use the **Secure Pull** pattern:

1.  **Store the Reference:** In your `@Activate` or `@Modified` method, store only the raw configuration value (the `${keyref:...}` token).
2.  **Resolve at Point of Use:** Only resolve the actual secret at the exact moment it is needed (e.g., just before opening a network connection).
3.  **Use `SecureSecret`:** Always use the `resolveSecure` method to obtain a `SecureSecret` and use a try-with-resources block to ensure immediate memory zeroing.

**Example of Heap-Safe Implementation:**
```java
public void connectToExternalService() {
    // _passwordRef holds "${keyref:gcp-sm/my-password}"
    try (SecureSecret secret = _keyResolverService.resolveSecure(_passwordRef)) {
        // Use the char[] directly in the client/driver
        _client.connect(_username, secret.getChars());
    } 
    // The plaintext secret is wiped from memory HERE.
}
```

---

## 4. DevOps & Security Architect: Protection Mandates
The system must be resilient against "Secret Leakage" via common OS vectors.

### 4.1 Anti-Leakage Constraints
1.  **No Environment Variables:** Do not use `System.getenv()` for secrets. It is visible to other processes via `/proc`.
2.  **No Plaintext Files:** Master passwords and service account keys should not exist in plaintext on the filesystem.
3.  **Memory Zeroing:** The system must use `Arrays.fill(buffer, '\0')` immediately after a secret is consumed or evicted from cache.
4.  **Process Isolation:** By using Cloud Identity (ADC), the "Secret Zero" is never written to a file that could be captured by a volume snapshot or container escape.

### 4.2 Interception Mechanics
The KMS is "transparent" to DXP via two primary hooks:
*   **OSGi ConfigurationPlugin:** Intercepts all `Dictionary<String, Object>` configurations at a high ranking. It resolves `${keyref:...}` strings before they reach any `@Component`.
*   **PortalPropertiesOverride:** Scans Liferay's core properties (`PropsUtil`) on startup, resolving references in `portal-ext.properties` and injecting them back into the runtime memory.

### 3.3 Caching & Performance
To prevent excessive network round-trips to remote KMS providers (e.g., GCP SM), the `KeyResolverService` utilizes a **Secure Cache**:
*   **TTL-based Eviction:** Secrets are cached for a short period (default 5 minutes).
*   **Memory Hygiene:** Cached values are stored as `SecureSecret` objects. Upon eviction or system shutdown, the cache must explicitly call `close()` on all entries to zero out the memory.

---

## 5. Security Auditor Requirements
The system must provide non-repudiable evidence of secret access.

### 5.1 Auditing & Visibility
Every successful or failed resolution attempt is captured by the `KeyAuditService` and routed via the **Liferay Audit Framework**:
*   **Metadata:** Logs the `ProviderID`, `Alias`, `Timestamp`, and `Success/Failure` status.
*   **Error Reporting:** For unsuccessful resolutions, the audit log must include a descriptive message explaining the failure (e.g., "Provider unavailable" or "Invalid key version").
*   **Centralized Storage:** All audit events are routed to `AuditRouterUtil`, allowing them to be viewed in the DXP Audit Report or sent to external SIEM systems.


## 6. Deployment & Configuration Guide

The Key Manager supports different security postures depending on the environment. The goal is always to move from "Static Secrets" to "Ambient Identity."

### 6.1 Running on Localhost (Development)
For developers with `gcloud` installed, this is the preferred and most secure way to run DXP locally.

#### Option A: Cloud-Connected (Preferred)
If you have access to a GCP project, you can use the same security chain as production.
1.  **Authorize your machine:** Run `gcloud auth application-default login`.
2.  **Configuration:** Enable `gcp-adc` and `gcp-sm`.
3.  **Bootstrap:** Use `${keyref:gcp-sm/dev-keystore-password}` for your local `JavaKeyStoreProvider`.
*   **Benefit:** Zero secrets are stored on your local machine. All development secrets (Local DB, OAuth clients) are fetched from the cloud vault using your own developer identity.

#### Option B: Fully Offline (Fallback)
If you have no GCP access or are working offline:
*   **Strategy:** Use the **Java KeyStore** with a local plaintext password.
*   **Configuration:** Set `keystorePassword` to a simple string in your local OSGi config.
*   **Usage:** Store local secrets in `keystore.jks` via the Key Manager Web UI.

### 6.2 Running On-Premise (No GCP Connectivity)
For servers with no internet/cloud access, security relies on hardened local encryption.
*   **Strategy:** Hardened **Java KeyStore**.
*   **Configuration:** 
    *   **Secret Zero:** Do NOT put the `keystorePassword` in a file or environment variable.
    *   **Recommended:** Use a custom "Bootstrap Provider" (e.g., one that reads from a specialized hardware security module or a highly restricted root-only file) to feed the password into the `keystorePassword` field of the `JavaKeyStoreProvider`.
    *   Ensure the `.jks` file has `600` permissions and is owned by the `liferay` user.

### 6.3 Running inside GCP (GKE, GCE, Cloud Run)
This is the **Gold Standard** for security. No static keys exist anywhere in the environment.
*   **Strategy:** **Identity-First Chain** (ADC -> GCP SM -> KeyStore).
*   **Configuration:**
    1.  **Enable Workload Identity:** Ensure your GKE/GCE instance has a Service Account assigned with `Secret Manager Secret Accessor` and `Service Account Token Creator` roles.
    2.  **Configure `gcp-adc`:** Enable the provider. It will automatically grab the ambient identity.
    3.  **Configure `gcp-sm`:** Set the `projectId`. It will use the ADC identity to connect.
    4.  **Configure `keystore`:** Set `keystorePassword=${keyref:gcp-sm/dxp-keystore-password}`.
*   **Result:** The DXP instance starts "cold," fetches its identity from the hypervisor, unlocks the cloud vault, and finally unlocks its local encrypted storage. No developer or admin ever sees the master password.

---

## 7. Implementation Reference (LLM Quick-Start)
To generate a new provider (e.g., for Azure Key Vault):
1.  **Define Component:** `@Component(service = KeyProvider.class)`.
2.  **Set Ranking:** If it's a Phase 1 provider, set a high ranking.
3.  **Implement Resolve:** Use the Azure SDK, but authenticate via the Ambient Identity (Managed Identity).
4.  **Memory Hygiene:** Ensure the SDK's response is converted to `char[]` and original buffers are cleared.
