package com.bemis.portal.twofa.provider.google.internal;

import static com.bemis.portal.twofa.provider.google.constants.GoogleTwoFactorAuthConstants.GOOGLE_PROVIDER_NOTIFICATION_TYPE;

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalService;
import com.bemis.portal.twofa.device.manager.util.WorkflowUtil;
import com.bemis.portal.twofa.provider.google.GoogleClient;
import com.bemis.portal.twofa.provider.internal.TwoFactorAuthProvider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = "com.bemis.portal.twofa.provider.internal.TwoFactorAuthProvider=" + GOOGLE_PROVIDER_NOTIFICATION_TYPE,
	service = TwoFactorAuthProvider.class
)
public class GoogleTwoFactorAuthProvider implements TwoFactorAuthProvider {

	@Override
	public void sendNotification(
			long userId, String deviceIP, String verificationBaseURL)
		throws PortalException {

		// Generate code to send

		GoogleAuthenticatorKey googleAuthKey = _googleClient.generateOTP();

		// Store code for future user-verification

		String secretKey = googleAuthKey.getKey();
		int validationCode = googleAuthKey.getVerificationCode();

		_deviceCodeLocalService.storeDeviceCodeAndSendNotification(
			userId, deviceIP, secretKey, validationCode, verificationBaseURL);
	}

	@Override
	public boolean verifyLoginCode(long userId, String verificationCode)
		throws PortalException {

		DeviceCode deviceCode =
			_deviceCodeLocalService.fetchDeviceCodeByPortalUserId(userId);

		if (deviceCode == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					">>> No device code Exists for User with ID : " + userId);
			}

			throw new NoSuchDeviceCodeException(
				"No device code Exists for User with ID : " + userId);
		}

		String storedDeviceCode = deviceCode.getDeviceCode();

		boolean verified = StringUtil.equalsIgnoreCase(
			verificationCode, storedDeviceCode);

		WorkflowUtil.completeLoginVerificationWorkflow(
			deviceCode.getCompanyId(), deviceCode.getGroupId(),
			deviceCode.getDeviceCodeId());

		return verified;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleTwoFactorAuthProvider.class);

	@Reference
	private DeviceCodeLocalService _deviceCodeLocalService;

	@Reference
	private GoogleClient _googleClient;

}