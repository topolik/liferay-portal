/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.cxf;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Component
public class LiferaySubjectCreator implements SubjectCreator {

	@Override
	public UserSubject createUserSubject(
		MessageContext mc, MultivaluedMap<String, String> params)
		throws OAuthServiceException {
		SecurityContext securityContext = mc.getSecurityContext();

		Principal userPrincipal =
			securityContext.getUserPrincipal();

		try {
			User user = _userLocalService.getUser(
				Long.parseLong(userPrincipal.getName()));

			return new UserSubject(
				user.getLogin(), Long.toString(user.getUserId()));
		}
		catch (PortalException e) {
			throw new OAuthServiceException(e);
		}
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private UserLocalService _userLocalService;

}
