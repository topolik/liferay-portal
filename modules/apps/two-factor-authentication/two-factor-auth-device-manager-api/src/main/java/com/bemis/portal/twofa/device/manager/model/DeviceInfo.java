package com.bemis.portal.twofa.device.manager.model;

import com.liferay.portal.kernel.model.User;

/**
 * @author Prathima Shreenath
 */
public class DeviceInfo {

	public DeviceInfo(
		long companyId, String userEmail, User user, String deviceIP,
		String verificationBaseURL) {

		_companyId = companyId;
		_userEmail = userEmail;
		_user = user;
		_deviceIP = deviceIP;
		_verificationBaseURL = verificationBaseURL;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public String getDeviceIP() {
		return _deviceIP;
	}

	public User getUser() {
		return _user;
	}

	public String getUserEmail() {
		return _userEmail;
	}

	public String getVerificationBaseURL() {
		return _verificationBaseURL;
	}

	private long _companyId;
	private String _deviceIP;
	private User _user;
	private String _userEmail;
	private String _verificationBaseURL;

}