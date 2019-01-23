package com.bemis.portal.twofa.provider.authy.internal;

import static com.bemis.portal.twofa.provider.authy.constants.AuthyTwoFactorAuthConstants.AUTHY_PROVIDER_NOTIFICATION_TYPE;

import static com.liferay.portal.kernel.util.StringPool.BLANK;

import com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalService;
import com.bemis.portal.twofa.provider.authy.AuthyClient;
import com.bemis.portal.twofa.provider.internal.TwoFactorAuthProvider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = "com.bemis.portal.twofa.provider.internal.TwoFactorAuthProvider=" + AUTHY_PROVIDER_NOTIFICATION_TYPE,
	service = TwoFactorAuthProvider.class
)
public class AuthyTwoFactorAuthProvider implements TwoFactorAuthProvider {

	@Override
	public void sendNotification(
			long userId, String deviceIP, String verificationBAseURL)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		String countryCode = getCountryCode(user);

		String phoneNumber = getPhoneNumber(user);

		registerPortalUserAsAuthyUser(
			user.getEmailAddress(), phoneNumber, countryCode);

		try {
			_authyClient.sendVerificationCode(phoneNumber, countryCode);

			//Adding a dummy device code with no verification code
			//This device code is later used to retrieve the ip address of the
			//device used by the user, which is later used for registering
			//device as perm/temp

			_deviceCodeLocalService.storeDeviceCodeAndSendNotification(
				userId, deviceIP, BLANK, 0, verificationBAseURL);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					">>> Unable to send verification code for : " + userId, e);
			}

			throw new PortalException(e.getMessage(), e);
		}
	}

	@Override
	public boolean verifyLoginCode(long userId, String verificationCode)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		String countryCode = getCountryCode(user);

		String phoneNumber = getPhoneNumber(user);

		try {
			_authyClient.verifyCode(phoneNumber, countryCode, verificationCode);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					" >>> Unable to verify the login code for user with ID : " +
						userId,
					e);
			}

			return false;
		}

		return true;
	}

	protected String getCountryCode(User user) throws PortalException {
		String countryName = user.getLocale().getCountry();

		Country country = _countryService.getCountryByA2(countryName);

		return country.getIdd();
	}

	protected String getPhoneNumber(User user) {
		String phoneNumber = "";
		List<Phone> phones = user.getPhones();

		for (Phone phone : phones) {
			if (phone.isPrimary()) {
				return phone.getNumber();
			}
		}

		return phoneNumber;
	}

	protected void registerPortalUserAsAuthyUser(
			String emailAddress, String phoneNumber, String countryCode)
		throws PortalException {

		try {
			_authyClient.registerUser(emailAddress, phoneNumber, countryCode);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					">>> Unable to register portal user as an authy User", e);
			}

			throw new PortalException(e.getMessage(), e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthyTwoFactorAuthProvider.class);

	@Reference
	private AuthyClient _authyClient;

	@Reference
	private CountryService _countryService;

	@Reference
	private DeviceCodeLocalService _deviceCodeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}