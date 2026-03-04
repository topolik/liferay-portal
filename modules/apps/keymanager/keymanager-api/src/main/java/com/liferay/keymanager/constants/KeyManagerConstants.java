/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.constants;

/**
 * @author Tomas Polesovsky
 */
public class KeyManagerConstants {

	public static final String AUDIT_EVENT_DELETE = "KEY_DELETE";

	public static final String AUDIT_EVENT_RESOLVE = "KEY_RESOLVE";

	public static final String AUDIT_EVENT_ROTATE = "KEY_ROTATE";

	public static final String AUDIT_EVENT_STORE = "KEY_STORE";

	public static final int DEFAULT_CACHE_MAX_SIZE = 1000;

	public static final long DEFAULT_CACHE_TTL_SECONDS = 300;

	public static final String KEY_REFERENCE_PREFIX = "${keyref:";

	public static final String KEY_REFERENCE_SUFFIX = "}";

	public static final String PROVIDER_ALIAS_SEPARATOR = "/";

	public static final String PROVIDER_GCP_ADC = "gcp-adc";

	public static final String PROVIDER_GCP_KMS = "gcp-kms";

	public static final String PROVIDER_GCP_SECRET_MANAGER = "gcp-sm";

	public static final String PROVIDER_GCP_SERVICE_ACCOUNT = "gcp-sa";

	public static final String PROVIDER_KEYSTORE = "keystore";

	public static final String PROVIDER_VAULT = "vault";

	private KeyManagerConstants() {
	}

}