/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.audit;

import com.liferay.keymanager.KeyReference;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;

import org.osgi.service.component.annotations.Component;

/**
 * @author Liferay
 */
@Component(immediate = true, service = KeyAccessAuditService.class)
public class KeyAccessAuditService {

	public void auditKeyAccess(
		String eventType, KeyReference keyReference, boolean success) {

		try {
			long userId = PrincipalThreadLocal.getUserId();

			if (_log.isInfoEnabled()) {
				_log.info(
					"Key audit: " + eventType + " provider=" +
						keyReference.getProvider() + " alias=" +
						keyReference.getAlias() + " success=" + success +
						" user=" + userId);
			}
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Failed to audit key access", e);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyAccessAuditService.class);

}
