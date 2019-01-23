package com.bemis.portal.twofa.login.web.portlet.action;

import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.model.DeviceInfo;
import com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalService;
import com.bemis.portal.twofa.login.web.executor.TwoFactorAuthExecutor;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login", "service.ranking:Integer=100"
	},
	service = MVCActionCommand.class
)
public class CustomLoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		if (_twoFactorAuthExecutor.enabled() &&
			_isAuthenticatedUser(actionRequest)) {

			boolean twoFactAuthSuccessful = executeTwoFactorAuthentication(
				actionRequest);

			if (!twoFactAuthSuccessful) {
				postProcessAuthFailure(actionRequest, actionResponse);

				return;
			}
		}

		_mvcActionCommand.processAction(actionRequest, actionResponse);
	}

	protected boolean executeTwoFactorAuthentication(
			ActionRequest actionRequest)
		throws PortalException {

		DeviceInfo deviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			actionRequest);

		boolean authenticationComplete = _twoFactorAuthExecutor.execute(
			deviceInfo);

		if (!authenticationComplete) {
			User user = deviceInfo.getUser();

			actionRequest.setAttribute(
				DeviceManagerConstants.PORTAL_USER_ID, user.getUserId());

			actionRequest.setAttribute(
				DeviceManagerConstants.TWO_FA_ENABLED, true);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Login from Unknown device detected for user with id : " +
						user.getUserId());
			}
		}

		return authenticationComplete;
	}

	protected void postProcessAuthFailure(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		long portalUserId = (long)actionRequest.getAttribute("portalUserId");

		String redirectURL = _deviceCodeLocalService.getVerificationURL(
			portalUserId, StringPool.BLANK);

		// Redirect to the Login verification Layout

		actionRequest.setAttribute(WebKeys.REDIRECT, redirectURL);

		actionResponse.sendRedirect(redirectURL);
	}

	private boolean _isAuthenticatedUser(ActionRequest actionRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					themeDisplay.getLayout(),
					PortalUtil.getPortletId(actionRequest));

			String login = ParamUtil.getString(actionRequest, _LOGIN);
			String password = actionRequest.getParameter(_PASSWORD);
			String authType = portletPreferences.getValue(_AUTH_TYPE, null);

			HttpServletRequest request = PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(actionRequest));

			long userId =
				AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
					request, login, password, authType);

			if (userId != UserConstants.USER_ID_DEFAULT) {
				return true;
			}
		}

		return false;
	}

	private static final String _AUTH_TYPE = "authType";

	private static final String _LOGIN = "login";

	private static final String _PASSWORD = "password";

	private static final Log _log = LogFactoryUtil.getLog(
		CustomLoginMVCActionCommand.class);

	@Reference
	private DeviceCodeLocalService _deviceCodeLocalService;

	@Reference (
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _mvcActionCommand;

	@Reference
	private TwoFactorAuthExecutor _twoFactorAuthExecutor;

	@Reference
	private UserLocalService _userLocalService;

}