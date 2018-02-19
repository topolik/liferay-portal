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
String redirect = ParamUtil.getString(request, "redirect");

long oAuth2ApplicationId = ParamUtil.getLong(request, "oAuth2ApplicationId", -1);

OAuth2Application oAuth2Application = null;

if (oAuth2ApplicationId > -1) {
		oAuth2Application = OAuth2ApplicationLocalServiceUtil.getOAuth2Application(oAuth2ApplicationId);
}

	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	String headerTitle = (oAuth2Application == null) ? LanguageUtil.get(request, "add-o-auth2-application") : LanguageUtil.format(request, "edit-x", oAuth2Application.getName(), false);

	renderResponse.setTitle(headerTitle);
%>

<div class="container-fluid-1280">
	<portlet:actionURL name='<%= oAuth2Application == null ? "updateOAuth2Application" : "updateOAuth2Application" %>' var="editOAuth2ApplicationURL" />

	<aui:form action="<%= editOAuth2ApplicationURL %>" name="fm">
		<liferay-ui:error exception="<%= DuplicateOAuth2ClientIdException.class %>" message="client-id-already-exists" />

		<aui:model-context bean="<%= oAuth2Application %>" model="<%= OAuth2Application.class %>" />

		<aui:fieldset-group markupView="lexicon">
			<aui:input type="hidden" name="oAuth2ApplicationId"
				value='<%= oAuth2Application == null ? "" : oAuth2Application.getOAuth2ApplicationId() %>'
			/>

			<aui:fieldset label="details">
				<aui:input name="name" />
				<aui:input name="description" type="textarea" />
				<aui:input name="webUrl" />
				<aui:input name="clientConfidential" type="checkbox"  value='<%= oAuth2Application == null ? "" : oAuth2Application.getClientConfidential() %>' />
				<aui:input name="clientId" value='<%= oAuth2Application == null ? "" : oAuth2Application.getClientId() %>' />
				<aui:input name="clientSecret" value='<%= oAuth2Application == null ? "" : oAuth2Application.getClientSecret() %>' />
				<aui:input name="redirectUri" value='<%= oAuth2Application == null ? "" : oAuth2Application.getRedirectUri() %>' />
			</aui:fieldset>
		</aui:fieldset-group>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" />
			<aui:button cssClass="btn-lg" onClick="<%= portletDisplay.getURLBack() %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</div>