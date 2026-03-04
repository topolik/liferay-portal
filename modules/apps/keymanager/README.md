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

---

## 5. Security Auditor Requirements
The system must provide non-repudiable evidence of secret access.

### 5.1 Auditing & Visibility
*   **Access Logging:** Every `resolve` operation must log: `ProviderID`, `Alias`, `Timestamp`, and `CallingBundleSymbolicName`.
*   **Failure Monitoring:** Failed resolutions must be audited as potential "Unauthorized Access" attempts.
*   **Cache TTL:** Caching of secrets must be configurable and default to a short TTL (e.g., 5-10 minutes) to support secret rotation.

---

## 6. Implementation Reference (LLM Quick-Start)
To generate a new provider (e.g., for Azure Key Vault):
1.  **Define Component:** `@Component(service = KeyProvider.class)`.
2.  **Set Ranking:** If it's a Phase 1 provider, set a high ranking.
3.  **Implement Resolve:** Use the Azure SDK, but authenticate via the Ambient Identity (Managed Identity).
4.  **Memory Hygiene:** Ensure the SDK's response is converted to `char[]` and original buffers are cleared.
