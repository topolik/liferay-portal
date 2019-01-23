package com.bemis.portal.twofa.login.web.executor;

import com.bemis.portal.commons.service.BemisPortalService;
import com.bemis.portal.twofa.device.manager.model.DeviceInfo;
import com.bemis.portal.twofa.device.manager.service.DeviceLocalService;
import com.bemis.portal.twofa.device.manager.service.DeviceManagerLocalService;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.portlet.ActionRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = TwoFactorAuthExecutor.class)
public class TwoFactorAuthExecutor {

	public boolean enabled() {
		boolean twoFaEnabledPropValue = GetterUtil.getBoolean(
			PropsUtil.get(_TWO_FA_ENABLED));

		boolean twoFaEnabledConfigValue =
			_deviceManagerLocalService.twoFactorAuthenticationEnabled();

		if (twoFaEnabledPropValue && twoFaEnabledConfigValue) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean execute(DeviceInfo deviceInfo) throws PortalException {

		// this checks if the device is verified for this user
		// and also, if the device is a (one-time)temp device for this user

		boolean deviceVerified =
			_deviceManagerLocalService.isDeviceVerifiedForUser(deviceInfo);

		User portalUser = deviceInfo.getUser();

		if (deviceVerified || _bemisPortalService.isStaff(portalUser)) {
			return true;
		}

		String deviceIP = deviceInfo.getDeviceIP();
		String verificationBaseURL = deviceInfo.getVerificationBaseURL();

		_deviceManagerLocalService.createAndSendVerificationCode(
			portalUser, deviceIP, verificationBaseURL);

		return false;
	}

	public DeviceInfo extractDeviceInfo(ActionRequest actionRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		String email = ParamUtil.getString(actionRequest, _LOGIN_PARAM);

		User user = _userLocalService.fetchUserByEmailAddress(companyId, email);

		String verificationBaseURL = themeDisplay.getPortalURL();

		HttpServletRequest request = PortalUtil.getOriginalServletRequest(
			PortalUtil.getHttpServletRequest(actionRequest));

		String ipAddress = request.getHeader(_HTTP_HEADER_ORIGINATING_IP);

		if (_log.isDebugEnabled()) {
			_log.debug(
				">>> IP Address from request header(X-FORWARDED-FOR) : " +
					ipAddress);
		}

		String deviceIP = null;

		if ((ipAddress != null) && !(StringUtil.trim(ipAddress)).isEmpty()) {
			if (ipAddress.contains(StringPool.COMMA)) {
				ipAddress = StringUtil.extractFirst(
					ipAddress, StringPool.COMMA);
			}

			if (ipAddress.contains(StringPool.COLON)) {
				ipAddress = StringUtil.extractFirst(
					ipAddress, StringPool.COLON);
			}

			deviceIP = ipAddress;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(">>> Device IP : " + deviceIP);
		}

		if (deviceIP == null) {
			try {
				InetAddress address = InetAddress.getLocalHost();

				deviceIP = address.getHostAddress();

				if (_log.isDebugEnabled()) {
					_log.debug(
						">>> Could not fetch IP from request header, " +
							"fetching host address from localhost : " +
								deviceIP);
				}
			}
			catch (UnknownHostException uhe) {
				_log.error(">>> Unable to fetch the local host address", uhe);
			}
		}

		return new DeviceInfo(
			companyId, email, user, deviceIP, verificationBaseURL);
	}

	private static final String _HTTP_HEADER_ORIGINATING_IP = "X-FORWARDED-FOR";

	private static final String _LOGIN_PARAM = "login";

	private static final String _TWO_FA_ENABLED =
		"two.factor.authentication.enabled";

	private static final Log _log = LogFactoryUtil.getLog(
		TwoFactorAuthExecutor.class);

	@Reference
	private BemisPortalService _bemisPortalService;

	@Reference
	private DeviceLocalService _deviceLocalService;

	@Reference
	private DeviceManagerLocalService _deviceManagerLocalService;

	@Reference
	private UserLocalService _userLocalService;

}