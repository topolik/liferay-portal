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

<%@ include file="/com.liferay.login.web/init.jsp" %>

<%
String openSSOSubjectScreenName = (String)request.getAttribute("open.sso.subject.screenName");

String redirect = ParamUtil.getString(request, "redirect");

portletDisplay.setURLBack(redirect);
portletDisplay.setShowBackIcon(true);
%>

<c:if test="<%= Validator.isNotNull(openSSOSubjectScreenName) %>">
	<p>
		<liferay-ui:message arguments='<%= "<strong>" + HtmlUtil.escapeAttribute(openSSOSubjectScreenName) + "</strong>" %>' key="your-user-x-could-not-be-logged-in" />

		<a href="<%= themeDisplay.getURLSignOut() %>"><liferay-ui:message arguments='<%= "<strong>" + HtmlUtil.escapeAttribute(openSSOSubjectScreenName) + "</strong>" %>' key="not-x" /></a>
	</p>
</c:if>

<liferay-ui:error key="MustNotUseCompanyMx" message="the-email-address-associated-with-your-opensso-account-cannot-be-used-to-register-a-new-user-because-its-email-domain-is-reserved" />
<liferay-ui:error key="StrangersNotAllowedException" message="only-known-users-are-allowed-to-sign-in-using-opensso" />
<liferay-ui:error key="MustBeAuthenticated" message="you-are-not-signed-into-the-opensso-service" />