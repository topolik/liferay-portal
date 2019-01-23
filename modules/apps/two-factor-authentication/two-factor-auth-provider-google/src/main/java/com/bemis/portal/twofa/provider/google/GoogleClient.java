package com.bemis.portal.twofa.provider.google;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = GoogleClient.class)
public class GoogleClient {

	public GoogleAuthenticatorKey generateOTP() {
		//create user credentials
		GoogleAuthenticatorKey authKey =
			_googleAuthenticator.createCredentials();

		return authKey;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_googleAuthenticator = new GoogleAuthenticator();
	}

	private GoogleAuthenticator _googleAuthenticator;

}