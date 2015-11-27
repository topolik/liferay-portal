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

package com.liferay.portal.security.sso.facebook.connect.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.auth.PrincipalException.MustBeEnabled;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectWebKeys;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;

/**
 * @author Stian Sigvartsen
 */
@Component(
	immediate = true,
	property = {
		"mvc.command.name=/login/facebook_connect_oauth",
		"javax.portlet.name=" + PortletKeys.LOGIN,
		"javax.portlet.name=" + PortletKeys.FAST_LOGIN,
	},
	service = MVCResourceCommand.class
)
public class FacebookConnectControllerMVCResourceCommand extends BaseMVCResourceCommand implements MVCResourceCommand { 

	@Override
	protected void doServeResource(ResourceRequest request, ResourceResponse response)
			throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		
		if (!_facebookConnect.isEnabled(themeDisplay.getCompanyId())) {
			
			throw new PortletException(
					new PrincipalException.MustBeEnabled(themeDisplay.getCompanyId(), FacebookConnect.class.getName()));
		}
		
		if (handleAlreadySignedIn(request, response, themeDisplay)) return;
		
		if (handleIncompleteUser(request, response)) return;
		
		if (handleFacebookAuth(request, response)) return;
		
		handleError(request, response, themeDisplay);
	}

	private boolean handleFacebookAuth(ResourceRequest request,
			ResourceResponse response) throws MustBeEnabled {
		
		HttpServletRequest httpServletRequest = getOriginalServletRequest(request);
		HttpSession session = httpServletRequest.getSession(true);
		
		String sessionCSRFToken = (String)session.getAttribute(FacebookConnectWebKeys.FACEBOOK_CSRF_TOKEN);
		String requestCSRFToken = request.getParameter("state");
		
		if (!Validator.isNull(requestCSRFToken) && requestCSRFToken.equals(sessionCSRFToken)) {

			String facebookAuthURL = getFacebookAuthURL(request, response);
			
			response.addProperty("Location", facebookAuthURL);	
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "302");
			
			return true;
		}
		
		return false;
	}
	
	private boolean handleIncompleteUser(ResourceRequest request, ResourceResponse response) throws PortletException {
		
		HttpServletRequest httpServletRequest = getOriginalServletRequest(request);
		HttpSession session = httpServletRequest.getSession(true);
		Long facebookIncompleteUserId = (Long)session.getAttribute(WebKeys.FACEBOOK_INCOMPLETE_USER_ID);
		if (!Validator.isNull(facebookIncompleteUserId)) {
			
			User user = _userLocalService.fetchUser((Long)session.getAttribute(FacebookConnectWebKeys.FACEBOOK_INCOMPLETE_MATCHED_USER_ID));		
			
			if (user != null) {
				response.addProperty("Location", getUpdateAccountURL(request, response));
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "302");
				return true;
			} else {
				session.removeAttribute(WebKeys.FACEBOOK_INCOMPLETE_USER_ID);
			}
		}
		
		return false;
	}

	private HttpServletRequest getOriginalServletRequest(ResourceRequest request) {
		return PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
	}

	private boolean handleAlreadySignedIn(ResourceRequest request, ResourceResponse response, ThemeDisplay themeDisplay) {
		
		if (themeDisplay.isSignedIn()) {
		
			String redirect = PortalUtil.escapeRedirect(request.getParameter("redirect"));
			
			response.addProperty("Location", redirect);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "302");
			
			return true;
		}
		
		return false;
	}
	
	private boolean handleError(ResourceRequest request, ResourceResponse response, ThemeDisplay themeDisplay) {
		
		String redirect = PortalUtil.escapeRedirect(request.getParameter("redirect"));
		
		response.addProperty("Location", redirect);
		response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "302");
		
		return true;
	}

	protected String getUpdateAccountURL(ResourceRequest request, ResourceResponse response)
			throws PortletException {
		
		PortletURL updateAccountURL = response.createRenderURL();
		updateAccountURL.setParameter("mvcRenderCommandName", "/login/associate_facebook_user");
		updateAccountURL.setParameter("redirect", request.getParameter("redirect"));
				
		return updateAccountURL.toString();
	}
	
	private String getFacebookAuthURL(ResourceRequest request, ResourceResponse response) throws MustBeEnabled {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_facebookConnect.isEnabled(themeDisplay.getCompanyId())) {
			throw new PrincipalException.MustBeEnabled(
				themeDisplay.getCompanyId(), FacebookConnect.class.getName());
		}

		String facebookAuthURL = _facebookConnect.getAuthURL(themeDisplay.getCompanyId());
		String facebookAppId = _facebookConnect.getAppId(themeDisplay.getCompanyId());

		String loginRedirectURL = getRedirectURI(request, response, themeDisplay);
		
		facebookAuthURL = HttpUtil.addParameter(facebookAuthURL, "client_id", facebookAppId);
		facebookAuthURL = HttpUtil.addParameter(facebookAuthURL, "redirect_uri", loginRedirectURL.toString());
		facebookAuthURL = HttpUtil.addParameter(facebookAuthURL, "scope", "email");
		facebookAuthURL = HttpUtil.addParameter(facebookAuthURL, "state", getCurrentCSRFToken(request));

		return facebookAuthURL;
	}
	
	public String getRedirectURI(ResourceRequest request, ResourceResponse response, ThemeDisplay themeDisplay) {
		
		String facebookAuthRedirectURL = _facebookConnect.getRedirectURL(themeDisplay.getCompanyId());		
		
		ResourceURL selfResourceURL = createSelfResourceURL(response);
		selfResourceURL.setParameter("redirect", request.getParameter("redirect"));
		
		String redirect_uri = HttpUtil.addParameter(facebookAuthRedirectURL, "postAuthRedirect", selfResourceURL.toString());
		
		return redirect_uri;
	}

	private ResourceURL createSelfResourceURL(ResourceResponse response) {
		ResourceURL controllerURL = response.createResourceURL();
		controllerURL.setResourceID("/login/facebook_connect_oauth");
		return controllerURL;
	}
	
	public String getCurrentCSRFToken(ResourceRequest request) {
		return (String)getOriginalServletRequest(request).getSession().getAttribute(FacebookConnectWebKeys.FACEBOOK_CSRF_TOKEN);
	}

	@Reference
	protected void setFacebookConnect(FacebookConnect facebookConnect) {
		_facebookConnect = facebookConnect;
	}
	
	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	private UserLocalService _userLocalService;
	private FacebookConnect _facebookConnect;	
}