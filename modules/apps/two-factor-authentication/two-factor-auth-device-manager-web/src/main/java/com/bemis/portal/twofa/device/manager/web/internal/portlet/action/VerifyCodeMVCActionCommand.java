package com.bemis.portal.twofa.device.manager.web.internal.portlet.action;

import com.bemis.portal.twofa.device.manager.service.DeviceManagerLocalService;
import com.bemis.portal.twofa.device.manager.web.constants.DeviceManagerPortletKeys;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

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
		"mvc.command.name=verifyLoginCode"
	},
	service = MVCActionCommand.class
)
public class VerifyCodeMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String verificationCode = ParamUtil.getString(
			actionRequest, "loginVerificationCode");

		long portalUserId = ParamUtil.getLong(actionRequest, "portalUserId");

		boolean verificationSuccessful =
			_deviceManagerLocalService.verifyDeviceCode(
				portalUserId, verificationCode);

		actionRequest.setAttribute("portalUserId", portalUserId);

		if (verificationSuccessful) {
			postProcessAuthSuccess(actionRequest);
		}
		else {
			postProcessAuthFailure(actionRequest);
		}

		PortalUtil.copyRequestParameters(actionRequest, actionResponse);

		actionResponse.setRenderParameter("mvcPath", "/view.jsp");

		sendRedirect(actionRequest, actionResponse);
	}

	protected void postProcessAuthFailure(ActionRequest actionRequest)
		throws Exception {

		actionRequest.setAttribute("authVerified", false);

		SessionErrors.add(actionRequest, "error.verification-failed");
	}

	protected void postProcessAuthSuccess(ActionRequest actionRequest)
		throws IOException {

		actionRequest.setAttribute("authVerified", true);

		SessionMessages.add(
			actionRequest, "success.login-code-verified-successfully");
	}

	@Reference
	private DeviceManagerLocalService _deviceManagerLocalService;

}