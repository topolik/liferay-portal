/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.audit;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = {})
public class KeyAuditService {

	public void auditAccess(
		String providerId, String alias, boolean success, String message) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"alias", alias
			).put(
				"providerId", providerId
			).put(
				"success", success
			);

			if (message != null) {
				jsonObject.put("message", message);
			}

			AuditMessage auditMessage = new AuditMessage(
				"com.liferay.keymanager", 0, 0, null, null, null,
				jsonObject.toString());

			AuditRouterUtil.route(auditMessage);
		}
		catch (Exception exception) {
			_log.error("Failed to route audit message", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyAuditService.class);

	@Reference
	private JSONFactory _jsonFactory;

}