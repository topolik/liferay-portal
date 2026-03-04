/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Liferay
 */
public class KeyResolutionException extends PortalException {

	public KeyResolutionException() {
	}

	public KeyResolutionException(String msg) {
		super(msg);
	}

	public KeyResolutionException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public KeyResolutionException(Throwable throwable) {
		super(throwable);
	}

}