package com.bemis.portal.twofa.device.manager.web.internal.instance.lifecycle;

import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.web.constants.DeviceManagerPortletKeys;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class AddLoginVerificationLayoutPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_clusterMasterExecutor.isMaster()) {
			return;
		}

		long previousValue = CompanyThreadLocal.getCompanyId();
		long companyId = company.getCompanyId();

		CompanyThreadLocal.setCompanyId(companyId);

		try {
			addLoginVerificationLayout(companyId);
		}
		finally {
			CompanyThreadLocal.setCompanyId(previousValue);
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
	}

	protected void addLoginVerificationLayout(long companyId)
		throws PortalException {

		User defaultUser = _userLocalService.getDefaultUser(companyId);

		long defaultUserId = defaultUser.getUserId();

		// Adding layout to Guest site
		// Since layout needs to be accessed by guests(users before login)

		Group group = _groupLocalService.getGroup(
			companyId, DeviceManagerConstants.GUEST_PORTAL);

		long groupId = group.getGroupId();

		boolean privateLayout = false;
		String type = LayoutConstants.TYPE_PORTLET;
		boolean hidden = true;

		ServiceContext serviceContext = new ServiceContext();

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, privateLayout,
			DeviceManagerConstants.LOGIN_VERIFICATION_FRIENDLY_URL);

		if (Validator.isNotNull(layout)) {
			if (_log.isInfoEnabled()) {
				_log.info(
					">>> Layout exists with URL : " + layout.getFriendlyURL());
			}

			return;
		}

		String loginVerificationLayoutName =
			DeviceManagerConstants.LOGIN_VERIFICATION_LAYOUT;

		try {
			layout = _layoutLocalService.addLayout(
				defaultUserId, groupId, privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				loginVerificationLayoutName, loginVerificationLayoutName,
				StringPool.BLANK, type, hidden,
				DeviceManagerConstants.LOGIN_VERIFICATION_FRIENDLY_URL,
				serviceContext);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			updateLayoutTemplate(defaultUserId, layout, layoutTypePortlet);

			if (_log.isInfoEnabled()) {
				_log.info(
					">>> Layout added with URL : " + layout.getFriendlyURL());
			}
		}
		catch (PortalException pe) {
			_log.error(
				">>> Error adding layout : " + loginVerificationLayoutName, pe);
		}
	}

	protected void updateLayoutTemplate(
			long defaultUserId, Layout layout,
			LayoutTypePortlet layoutTypePortlet)
		throws PortalException {

		layoutTypePortlet.setLayoutTemplateId(defaultUserId, "1_column");

		layoutTypePortlet.addPortletId(
			defaultUserId, DeviceManagerPortletKeys.DEVICE_MANAGER, false);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.getPrivateLayout(),
			layout.getLayoutId(), layout.getTypeSettings());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddLoginVerificationLayoutPortalInstanceLifecycleListener.class);

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

}