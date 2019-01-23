package com.bemis.portal.twofa.provider.internal;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Prathima Shreenath
 */
public interface TwoFactorAuthProvider {

	//TODO : change to sendAuthToken ??
	public void sendNotification(
			long userId, String deviceIP, String verificationBaseURL)
		throws PortalException;

	//TODO : change to verifyAuthToken ??
	public boolean verifyLoginCode(long userId, String verificationCode)
		throws PortalException;

}