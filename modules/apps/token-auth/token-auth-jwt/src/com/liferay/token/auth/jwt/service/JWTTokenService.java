/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.token.auth.jwt.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ac.AccessControlUtil;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.security.auth.AuthVerifierResult;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.impl.UserLocalServiceImpl;
import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.model.TokenSession;
import com.liferay.token.auth.service.TokenClientService;
import com.liferay.token.auth.service.TokenSessionService;
import com.liferay.token.auth.verifier.TokenVerificationException;
import com.liferay.util.PwdGenerator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"json.web.service.path=JWTTokenService"
	},
	service = JWTTokenService.class
)
@JSONWebService
@AccessControlled(guestAccessEnabled = true)
public class JWTTokenService {

	public String issue(String tokenClientId) throws PortalException {
		if (_tokenClientService == null || _tokenClientService == null) {
			return null;
		}

		TokenClient tokenClient = null;

		try {
			tokenClient = _tokenClientService.findById(tokenClientId);
		} catch (PortalException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to find requested client Id " + tokenClientId, e);
			}
			throw new PortalException("No clientId found for " + tokenClientId);
		}

		if(tokenClient == null ||
			tokenClient.getState().equals(TokenClient.State.REVOKED)) {

			throw new PrincipalException("The client has been revoked!");
		}

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		Map<String, Object> settings = authVerifierResult.getSettings();
		String authType = (String) settings.get(AuthVerifierPipeline.AUTH_TYPE);

		// TODO: make configurable
		List<String> allowedAuthTypes = Arrays.asList(new String[]{
			HttpServletRequest.BASIC_AUTH,
			HttpServletRequest.DIGEST_AUTH
		});

		if (!allowedAuthTypes.contains(authType)) {
			if(_log.isDebugEnabled()) {
				_log.debug(
					"Tokens cannot be issued with current authentication " +
						"scheme " + authType);
			}

			throw new PrincipalException(
				"Tokens cannot be issued with current authentication scheme "
					+ authType);
		}


		long userId = authVerifierResult.getUserId();

		TokenSession tokenSession = new TokenSession();
		tokenSession.setIssued(new Date());
		tokenSession.setTokenClientId(tokenClient.getClientId());
		tokenSession.setTokenType(_JWT);
		tokenSession.setUserId(userId);

		JWT jwtToken = createJTWToken(tokenClient, tokenSession);

		String token = jwtToken.serialize();

		tokenSession.setToken(token);

		return token;
	}

	public TokenSession verify(String token) throws TokenVerificationException {
		if (Validator.isNull(token)) {
			return null;
		}

		JWT jwtToken = parseToken(token);
		ReadOnlyJWTClaimsSet claimsSet = null;
		try {
			claimsSet = jwtToken.getJWTClaimsSet();
		} catch (ParseException e) {
			throw new TokenVerificationException("Unable to parse claims!", e);
		}

		List<String> audience = claimsSet.getAudience();

		if (audience == null || audience.size() != 1) {
			throw new TokenVerificationException(
				"Token audience " + audience + "should contain tokenClientId!");
		}

		String tokenClientId = audience.get(0);

		TokenSession tokenSession = new TokenSession();
		tokenSession.setIssued(claimsSet.getIssueTime());
		tokenSession.setToken(token);
		tokenSession.setTokenClientId(tokenClientId);
		tokenSession.setTokenType(_JWT);
		tokenSession.setUserId(GetterUtil.getLong(claimsSet.getSubject()));

		// TODO: check expiration?? Probably check expiration of TokenClient

		return tokenSession;
	}

	protected JWT createJTWToken(
			TokenClient tokenClient, TokenSession tokenSession)
		throws PortalException {

		//TODO: go with public key encryption

		JWTClaimsSet claimsSet = new JWTClaimsSet();
		claimsSet.setSubject(String.valueOf(tokenSession.getUserId()));
		claimsSet.setIssueTime(new Date());
		claimsSet.setAudience(tokenClient.getClientId());
		claimsSet.setJWTID(generateNonce());

		SignedJWT signedJWT = new SignedJWT(
			new JWSHeader(JWSAlgorithm.HS256), claimsSet);

		try {
			JWSSigner signer = new MACSigner(getKey());
			signedJWT.sign(signer);

		} catch (JOSEException e) {
			throw new PortalException("Unable to sign token", e);
		}

		return signedJWT;
	}

	protected String generateNonce() {
		return PwdGenerator.getPassword();
	}

	protected JWT parseToken(String token) throws TokenVerificationException {
		SignedJWT signedJWT = null;
		try {
			signedJWT = SignedJWT.parse(token);
		} catch (ParseException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e.getMessage(), e);
			}
			return null;
		}

		if (!signedJWT.getHeader().getAlgorithm().equals(JWSAlgorithm.HS256)) {
			return null;
		}

		JWSVerifier verifier = new MACVerifier(getKey());
		try {
			if (!signedJWT.verify(verifier)) {
				return null;
			}
		} catch (JOSEException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e.getMessage(), e);
			}

			throw new TokenVerificationException(
				"Incompatible header: " + signedJWT.getHeader().toJSONObject(),
				e);
		}

		return signedJWT;
	}

	private byte[] getKey() {
		return _key;
	}

	@Reference
	protected void setTokenClientService(TokenClientService tokenClientService) {
		this._tokenClientService = tokenClientService;
	}

	@Reference
	protected void setTokenSessionService(TokenSessionService tokenSessionService) {
		this._tokenSessionService = tokenSessionService;
	}

	public static final String _JWT = "JWT";
	private static final byte[] _key = new byte[32];

	static {
		SecureRandom random = new SecureRandom();
		random.nextBytes(_key);
	}

	private TokenClientService _tokenClientService;
	private TokenSessionService _tokenSessionService;

	private static Log _log = LogFactoryUtil.getLog(JWTTokenService.class);

}
