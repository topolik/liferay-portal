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

package com.liferay.multi.factor.authentication.provider.totp.exception;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author arthurchan35
 */
@ProviderType
public class NoSuchTOTPException extends NoSuchModelException {

	public NoSuchTOTPException() {
	}

	public NoSuchTOTPException(String msg) {
		super(msg);
	}

	public NoSuchTOTPException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchTOTPException(Throwable cause) {
		super(cause);
	}

}