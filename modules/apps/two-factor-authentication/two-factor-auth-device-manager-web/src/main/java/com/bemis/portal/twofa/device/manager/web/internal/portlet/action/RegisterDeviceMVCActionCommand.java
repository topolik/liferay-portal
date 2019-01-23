package com.bemis.portal.twofa.device.manager.web.internal.portlet.action;

import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.service.DeviceManagerLocalService;
import com.bemis.portal.twofa.device.manager.web.constants.DeviceManagerPortletKeys;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DeviceManagerPortletKeys.DEVICE_MANAGER,
		"mvc.command.name=registerVerifiedDevice"
	},
	service = MVCActionCommand.class
)
public class RegisterDeviceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long portalUserId = ParamUtil.getLong(actionRequest, "portalUserId");

		boolean registerAsVerifiedDevice = ParamUtil.getBoolean(
			actionRequest, "registerDevice");

		_deviceManagerLocalService.registerDeviceAsVerifiedOrTemp(
			portalUserId, registerAsVerifiedDevice);

		if (_log.isDebugEnabled()) {
			_log.debug(">>> Device Registered Successfully");
		}

		postProcessRegisterDevice(actionRequest, actionResponse);
	}

	protected void postProcessRegisterDevice(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		actionRequest.setAttribute(
			WebKeys.REDIRECT, DeviceManagerConstants.LOGIN_URL);

		actionResponse.sendRedirect(DeviceManagerConstants.LOGIN_URL);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RegisterDeviceMVCActionCommand.class);

	@Reference
	private DeviceManagerLocalService _deviceManagerLocalService;

}