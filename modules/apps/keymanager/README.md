# Liferay Key Manager Module

A comprehensive cryptographic and API key management module for Liferay DXP.

## Modules

| Module | Description |
|--------|-------------|
| `com.liferay.keymanager.api` | API interfaces and constants |
| `com.liferay.keymanager.service` | Core implementation, interceptors, cache |
| `com.liferay.keymanager.provider.keystore` | Java KeyStore provider |
| `com.liferay.keymanager.provider.gcp` | Google Cloud KMS, Service Account, ADC providers |
| `com.liferay.keymanager.upgrade` | Migration/upgrade process for existing secrets |
| `com.liferay.keymanager.web` | Admin UI portlet |

## Quick Start

1. Build: `./gradlew build`
2. Deploy all JARs to `$LIFERAY_HOME/deploy/`
3. Configure via System Settings → Key Manager
4. Discover secrets: `g! keymigrate:discover`
5. Dry run: `g! keymigrate:dryrun keystore`
6. Migrate: `g! keymigrate:execute keystore HIGH_AND_MEDIUM_SENSITIVITY`

## Key Reference Format

```
${keyref:<provider>/<alias>}
```

Examples:
- `${keyref:keystore/smtp-password}`
- `${keyref:gcp-kms/projects/my-proj/locations/global/keyRings/ring/cryptoKeys/key}`
- `${keyref:gcp-sa/default}`
- `${keyref:gcp-adc/access-token}`
