/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Tomas Polesovsky
 */
public class KeyProviderException extends PortalException {

	public KeyProviderException() {
	}

	public KeyProviderException(String msg) {
		super(msg);
	}

	public KeyProviderException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public KeyProviderException(Throwable throwable) {
		super(throwable);
	}

}