<%@ page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %>
<%@ page
	import="com.liferay.multi.factor.authentication.spi.verifier.UserAccountSetupMFAVerifier" %>

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

<%@ include file="/init.jsp" %>

<%
	long userId = user.getUserId();

	String screenNavigationCategoryKey = ParamUtil.getString(request, "screenNavigationCategoryKey");
	String screenNavigationEntryKey = ParamUtil.getString(request, "screenNavigationEntryKey");

	UserAccountSetupMFAVerifier userAccountSetupMFAVerifier = (UserAccountSetupMFAVerifier)request.getAttribute(UserAccountSetupMFAVerifier.class.getName());
%>

<portlet:actionURL name="/my_account/setup_mfa" var="actionURL">
	<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_user" />
</portlet:actionURL>

<aui:form action="<%= actionURL %>" cssClass="portlet-users-admin-edit-user" data-senna-off="true" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="screenNavigationCategoryKey" type="hidden" value="<%= screenNavigationCategoryKey %>" />
	<aui:input name="screenNavigationEntryKey" type="hidden" value="<%= screenNavigationEntryKey %>" />
	<aui:input name="userAccountSetupMFAVerifierName" type="hidden" value="<%= userAccountSetupMFAVerifier.getName()%>" />

	<liferay-ui:error key="userAccountSetupFailed" message="user-account-setup-failed" />

	<div class="sheet sheet-lg">
			<div class="sheet-header">
				<h2 class="sheet-title"><liferay-ui:message key="<%= userAccountSetupMFAVerifier.getName() %>" escapeAttribute="<%= true %>"/></h2>
			</div>

		<div class="sheet-section">
			<%
				userAccountSetupMFAVerifier.includeUserAccountSetup(userId, request, response);
			%>
		</div>

	</div>

	<aui:button-row>
		<aui:button type="submit" value="submit" />
	</aui:button-row>
</aui:form>