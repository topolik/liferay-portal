/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.multi.factor.authentication.checker.audit.util;

import com.liferay.multi.factor.authentication.checker.audit.constants.EventTypes;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;

/**
 * @author Arthur Chan
 */
public class MFACheckerAuditUtil {

	public static AuditMessage buildVerificationFailureMessage(
		User user, String checkerClassName, String reason) {

		JSONObject additionalInfo = JSONUtil.put("reason", reason);

		return new AuditMessage(
			EventTypes.VERIFICATION_FAILURE, user.getCompanyId(),
			user.getUserId(), user.getFullName(), checkerClassName,
			String.valueOf(user.getPrimaryKey()), null, additionalInfo);
	}

	public static AuditMessage buildVerificationSuccessMessage(
		User user, String checkerClassName) {

		return new AuditMessage(
			EventTypes.VERIFICATION_SUCCESS, user.getCompanyId(),
			user.getUserId(), user.getFullName(), checkerClassName,
			String.valueOf(user.getPrimaryKey()), null, null);
	}

	public static void routeAuditMessage(AuditMessage auditMessage) {
		try {
			AuditRouterUtil.route(auditMessage);
		}
		catch (AuditException ae) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", ae);
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFACheckerAuditUtil.class);

}