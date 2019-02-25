
<%@ page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %>

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
	User selUser = PortalUtil.getSelectedUser(request);

	if (selUser != null) {
		PortalUtil.setPageSubtitle(selUser.getFullName(), request);
	}

	long selUserId = (selUser != null) ? selUser.getUserId() : 0;

	String screenNavigationCategoryKey = ParamUtil.getString(request, "screenNavigationCategoryKey");
	String screenNavigationEntryKey = ParamUtil.getString(request, "screenNavigationEntryKey");

	String label = ParamUtil.getString(request, "label");
%>

<portlet:renderURL var="redirect">
	<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_user" />
	<portlet:param name="p_u_i_d" value="<%= String.valueOf(selUserId) %>" />
	<portlet:param name="screenNavigationCategoryKey" value="<%= screenNavigationCategoryKey %>" />
	<portlet:param name="screenNavigationEntryKey" value="<%= screenNavigationEntryKey %>" />
</portlet:renderURL>

<aui:form action="" cssClass="portlet-users-admin-edit-user" data-senna-off="true" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect.toString() %>" />
	<aui:input name="p_u_i_d" type="hidden" value="<%= selUserId %>" />
	<aui:input name="screenNavigationCategoryKey" type="hidden" value="<%= screenNavigationCategoryKey %>" />
	<aui:input name="screenNavigationEntryKey" type="hidden" value="<%= screenNavigationEntryKey %>" />

	<div class="sheet sheet-lg">
			<div class="sheet-header">
				<h2 class="sheet-title"><%= label %></h2>
			</div>

		<div class="sheet-section">

		</div>

	</div>
</aui:form>