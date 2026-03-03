package com.liferay.keymanager.constants;

public class KeyManagerConstants {
	/**
	 * Key references follow the pattern:
	 * ${keyref:<provider>/<alias>}
	 *
	 * Examples:
	 *   ${keyref:keystore/smtp-password}
	 *   ${keyref:gcp-kms/projects/my-proj/locations/global/keyRings/my-ring/cryptoKeys/my-key/versions/1}
	 *   ${keyref:gcp-sa/my-service-account-key}
	 *   ${keyref:gcp-adc/default}
	 *   ${keyref:vault/secret/data/liferay/smtp}
	 */
	public static final String KEY_REFERENCE_PREFIX = "${keyref:";
	public static final String KEY_REFERENCE_SUFFIX = "}";
	public static final String PROVIDER_ALIAS_SEPARATOR = "/";

	public static final String PROVIDER_KEYSTORE = "keystore";
	public static final String PROVIDER_GCP_KMS = "gcp-kms";
	public static final String PROVIDER_GCP_SERVICE_ACCOUNT = "gcp-sa";
	public static final String PROVIDER_GCP_ADC = "gcp-adc";
	public static final String PROVIDER_VAULT = "vault";

	public static final long DEFAULT_CACHE_TTL_SECONDS = 300;
	public static final int DEFAULT_CACHE_MAX_SIZE = 1000;

	public static final String AUDIT_EVENT_RESOLVE = "KEY_RESOLVE";
	public static final String AUDIT_EVENT_STORE = "KEY_STORE";
	public static final String AUDIT_EVENT_ROTATE = "KEY_ROTATE";
	public static final String AUDIT_EVENT_DELETE = "KEY_DELETE";

	private KeyManagerConstants() {
	}

}
