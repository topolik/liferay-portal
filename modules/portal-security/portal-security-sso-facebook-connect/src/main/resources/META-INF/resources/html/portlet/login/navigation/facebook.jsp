<%--
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
--%>

<%@ include file="/html/portlet/login/navigation/init.jsp" %>

<liferay-portlet:renderURL portletName="<%= LoginPortletKeys.LOGIN %>" varImpl="loginRedirectURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/login/login_redirect" />
</liferay-portlet:renderURL>

<liferay-portlet:resourceURL id="/login/facebook_connect_oauth" portletName="<%= LoginPortletKeys.LOGIN %>" varImpl="preAuthRedirect" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="redirect" value="${loginRedirectURL}" />
	<portlet:param name="state" value="<%= (String)request.getAttribute(FacebookConnectWebKeys.FACEBOOK_CSRF_TOKEN) %>" />
</liferay-portlet:resourceURL>

<%
String taglibOpenFacebookConnectLoginWindow = "javascript:var facebookConnectLoginWindow = window.open('" + HttpUtil.encodeURL(preAuthRedirect.toString()) + "', 'facebook', 'align=center,directories=no,height=560,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=1000'); void(''); facebookConnectLoginWindow.focus();";
%>

<liferay-ui:icon
	iconCssClass="icon-facebook"
	message="facebook"
	url="<%= taglibOpenFacebookConnectLoginWindow %>"
/>