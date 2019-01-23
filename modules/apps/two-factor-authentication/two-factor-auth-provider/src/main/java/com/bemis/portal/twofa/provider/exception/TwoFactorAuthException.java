package com.bemis.portal.twofa.provider.exception;

import com.liferay.portal.kernel.security.auth.AuthException;

/**
 * @author Prathima Shreenath
 */
public class TwoFactorAuthException extends AuthException {

	public TwoFactorAuthException() {
	}

	public TwoFactorAuthException(String msg) {
		super(msg);
	}

	public TwoFactorAuthException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TwoFactorAuthException(Throwable cause) {
		super(cause);
	}

}