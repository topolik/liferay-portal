package com.bemis.portal.twofa.provider.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Params;
import com.authy.api.PhoneVerification;
import com.authy.api.User;
import com.authy.api.Users;
import com.authy.api.Verification;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Thuong Dinh
 */
@Component(immediate = true, service = AuthyClient.class)
public class AuthyClient {

	public void registerUser(String email, String phone, String countryCode)
		throws Exception {

		// Create user

		User user = getUsers().createUser(email, phone, countryCode);

		if (!user.isOk()) {
			throw new Exception(user.getError().getMessage());
		}
	}

	public void sendVerificationCode(String phone, String countryCode)
		throws Exception {

		PhoneVerification phoneVerification = getPhoneVerification();

		Params params = new Params();

		params.setAttribute("locale", Locale.getDefault().getCountry());

		Verification verification = phoneVerification.start(
			phone, countryCode, "sms", params);

		if (!verification.isOk()) {
			throw new Exception(verification.getMessage());
		}
	}

	public void verifyCode(
			String phone, String countryCode, String verificationCode)
		throws Exception {

		PhoneVerification phoneVerification = getPhoneVerification();

		Verification verification = phoneVerification.check(
			phone, countryCode, verificationCode);

		if (!verification.isOk()) {
			throw new Exception(verification.getMessage());
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_authyApiClient = new AuthyApiClient(_API_KEY);
	}

	protected PhoneVerification getPhoneVerification() {
		return _authyApiClient.getPhoneVerification();
	}

	protected Users getUsers() {
		return _authyApiClient.getUsers();
	}

	private static final String _API_KEY = "fC0X7j9irKPB2E5gmIblFbInM0E2tbAY";

	private AuthyApiClient _authyApiClient;

}