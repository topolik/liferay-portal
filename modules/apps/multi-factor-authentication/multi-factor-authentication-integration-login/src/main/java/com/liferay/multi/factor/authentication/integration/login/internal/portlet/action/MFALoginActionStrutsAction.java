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

package com.liferay.multi.factor.authentication.integration.login.internal.portlet.action;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.integration.login.internal.spi.integration.LoginMFAIntegration;
import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.action.LoginAction;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tomas Polesovsky
 */
@Component(
	enabled = false, //TODO
	immediate = true, property = "path=/portal/login",
	service = StrutsAction.class
)
public class MFALoginActionStrutsAction implements StrutsAction {

	@Override
	public String execute(
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		MFAVerifier mfaVerifier =
			_mfaRegistry.getMFAVerifier(LoginMFAIntegration.NAME);

		if (mfaVerifier == null) {
			ActionForward actionForward =
				_loginAction.execute(new ActionMapping(null, null, null, null),
					request, response);

			return actionForward.getPath();
		}

		throw new UnsupportedOperationException("Not Implemented Yet");
	}

	@Reference
	private MFARegistry _mfaRegistry;

	@Reference
	private MFAPortletURLFactory _mfaPortletURLFactory;

	@Reference
	private Portal _portal;

	private LoginAction _loginAction = new LoginAction();
}
