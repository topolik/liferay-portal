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

<%@ include file="../init.jsp" %>

<%
String mvcPath = ParamUtil.getString(request, "mvcPath");

ResultRow row = (ResultRow)request
				.getAttribute("SEARCH_CONTAINER_RESULT_ROW");

OAuth2Application oauth2application = (OAuth2Application)row.getObject();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<portlet:renderURL var="editURL">
		<portlet:param name="oAuth2ApplicationId"
			value="<%= String.valueOf(oauth2application.getOAuth2ApplicationId()) %>"
		/>

		<portlet:param name="mvcPath"
			value="/admin/edit_oauth2application.jsp"
		/>

		<portlet:param name="redirect"
			value="<%= currentURL %>"
		/>
	</portlet:renderURL>

	<liferay-ui:icon message="Edit"
		url="<%= editURL.toString() %>"
	/>

	<portlet:actionURL name="deleteOAuth2Application" var="deleteURL">
		<portlet:param name="oAuth2ApplicationId"
			value="<%= String.valueOf(oauth2application.getOAuth2ApplicationId()) %>"
		/>
	</portlet:actionURL>

	<liferay-ui:icon-delete message="delete" url="<%= deleteURL.toString() %>" />
</liferay-ui:icon-menu>