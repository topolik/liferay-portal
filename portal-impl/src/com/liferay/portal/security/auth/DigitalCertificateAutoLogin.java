/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsValues;

import java.nio.charset.Charset;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Áron Budea
 * @author Juan Fernández
 */
public class DigitalCertificateAutoLogin implements AutoLogin {

	public String[] login(
		HttpServletRequest request, HttpServletResponse response) {

			String[] credentials = null;

			if (!PropsValues.DIGITAL_CERTIFICATE_AUTH_ENABLED) {
				return credentials;
			}

			if (!request.isSecure()) {
				return credentials;
			}

			long companyId = PortalUtil.getCompanyId(request);

			try {
				String authType = PrefsPropsUtil.getString(
					companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
					PropsValues.COMPANY_SECURITY_AUTH_TYPE);

				String authAttribute = _getAuthAttribute(authType);

				X509Certificate[] certificates = (X509Certificate[])
					request.getAttribute(
						"javax.servlet.request.X509Certificate");

				if (certificates == null) {
					return credentials;
				}

				Collection alternativeNames;

				for (int i = 0; i < certificates.length && credentials == null;
					 i++) {

					alternativeNames = null;

					try {
						alternativeNames =
							certificates[i].getSubjectAlternativeNames();
					}
					catch (CertificateParsingException cpe) {
						_log.error(cpe, cpe);
					}

					credentials = _identify(
						authAttribute, authType, companyId,
						certificates[i].getSubjectX500Principal().getName(),
						alternativeNames);

					if (credentials != null) {
						break;
					}
				}
			}
			catch (Exception e) {
				_log.error(e, e);
			}

			return credentials;
	}

	private String _derIA5StringToString(byte[] derIA5String) {

		// DER encoded IA5String to String conversion

		String result = new String(derIA5String, Charset.forName("US-ASCII"));

		// 22 (0x16) as 1st byte defines IA5String

		if (derIA5String.length < 2 || derIA5String[0] != 22) {
			return null;
		}

		// 2nd byte is size, if 1st bit = 0, or
		// size is defined by the value in the following N bytes, where
		// N = 2nd byte's last 7 bits
		int size = 0;

		if (derIA5String[1] < 0) {
			// 1st bit = 1
			// Max length of e-mail address is 254 chars, size fits in 1 byte
			size = 1;
		}

		return result.substring(size + 2);
	}

	/*
	*	authType can be "emailAddress", "screenName" or "userId"
	*	The corresponding certificate attribute (authAttr) would be
	*	"emailaddress", "cn" or "uid" respectively
	*	however Java transforms emailaddress into OID + ASN.1 DER
	*/

	private String _getAuthAttribute(String authType) {

		String authAttribute = _ATTR_EMAIL;

		if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			authAttribute = _ATTR_UID;
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			authAttribute = _ATTR_CN;
		}

		return authAttribute;
	}

	private String[] _getCredentials(
		String authType, long companyId, String userIdentifier) {

		String[] credentials = null;

		if (Validator.isNull(userIdentifier)) {
			return credentials;
		}

		User user = null;

		try {
			if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				user = UserLocalServiceUtil.getUserByScreenName(
					companyId, userIdentifier);
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
				user = UserLocalServiceUtil.getUserById(
					Long.parseLong(userIdentifier));
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				user = UserLocalServiceUtil.getUserByEmailAddress(
					companyId, userIdentifier);
			}

			String password = user.getPassword();

			credentials = new String[3];

			credentials[0] = String.valueOf(user.getUserId());
			credentials[1] = password;
			credentials[2] = Boolean.TRUE.toString();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return credentials;
	}

	private String[] _identify(
		String authAttribute, String authType, long companyId,
		String distinguishedName, Collection alternativeNames) {

		String[] credentials = null;
		LdapName ldName = null;

		try {
			ldName = new LdapName(distinguishedName);
		}
		catch (InvalidNameException ine) {
			return credentials;
		}

		String userIdentifier = null;

		for (Rdn rdn : ldName.getRdns()) {

			String attr = rdn.getType();

			if (attr.toLowerCase().equals(authAttribute)) {
				if (authAttribute.equals(_ATTR_EMAIL)) {
					userIdentifier = _derIA5StringToString(
						(byte[])rdn.getValue());
				}
				else {
					userIdentifier = rdn.getValue().toString();
				}

				credentials = _getCredentials(
					authType, companyId, userIdentifier);

				if (credentials != null) {
					break;
				}
			}
		}

		// If no credentials found in DN, and auth is e-mail,
		// try Subject Alternative Name extension

		if ((credentials == null) &&
			authType.equals(CompanyConstants.AUTH_TYPE_EA) &&
			alternativeNames != null) {

			Iterator altNameIterator = alternativeNames.iterator();

			while (altNameIterator.hasNext()) {

				List altName = (List)altNameIterator.next();

				// 1 as list's 1st item means 2nd is rfc822Name (e-mail addr.)

				if (((Integer)altName.get(0)).intValue() == 1) {
					userIdentifier = (String)altName.get(1);
					credentials = _getCredentials(
						authType, companyId, userIdentifier);

					if (credentials != null) {
						break;
					}
				}
			}
		}

		return credentials;
	}

	private final static String _ATTR_CN = "cn";
	private final static String _ATTR_EMAIL = "1.2.840.113549.1.9.1";
	private final static String _ATTR_UID = "uid";

	private static Log _log = LogFactoryUtil.getLog(
		DigitalCertificateAutoLogin.class);

}