package com.liferay.keymanager.internal.audit;

import com.liferay.keymanager.KeyReference;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = KeyAccessAuditService.class)
public class KeyAccessAuditService {

	public void auditKeyAccess(String eventType, KeyReference reference, boolean success) {
		try {
			long userId = PrincipalThreadLocal.getUserId();

			if (_log.isInfoEnabled()) {
				_log.info(
					"Key audit: " + eventType +
					" provider=" + reference.getProvider() +
					" alias=" + reference.getAlias() +
					" success=" + success +
					" user=" + userId);
			}
		}
		catch (Exception e) {
			_log.error("Failed to audit key access", e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(KeyAccessAuditService.class);

}
