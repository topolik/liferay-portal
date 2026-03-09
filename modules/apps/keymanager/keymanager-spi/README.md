# Key Manager SPI

The `keymanager-spi` defines the extension points for adding new storage and cryptographic backends to Liferay.

## Provider Interfaces

### 1. SecretVaultProvider

Implement this to provide storage for opaque secret material.
*   **Responsibility**: Persistence and envelope encryption.
*   **Methodology**: Typically utilizes a Master Key (via `CryptoManager`) to wrap Data Encryption Keys (DEKs).

### 2. CryptoVaultProvider

Implement this to provide cryptographic functionality.
*   **Responsibility**: Executing `encrypt`, `decrypt`, `wrap`, and `unwrap` operations.
*   **Policy**: Providers are responsible for their own metadata (algorithms, IV lengths).
*   **FIPS Rule**: Symmetric keys and private keys should generally **not** be extractable from the provider if it is backed by hardware (Level 3).

## Registration Pattern

All providers must be OSGi components and include a `providerId` property.

```java
@Component(
    configurationPid = "...",
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    service = CryptoVaultProvider.class
)
public class MyProvider implements CryptoVaultProvider {
    // ...
}
```

Providers should also implement `ConfigurationPidMapping` to support company-scoped (multitenant) configurations.