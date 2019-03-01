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
BrowserMFAVerifier browserMFAVerifier = (BrowserMFAVerifier)request.getAttribute(BrowserMFAVerifier.class.getName());

List<BrowserMFAVerifier> setupMFAVerifiers = (List<BrowserMFAVerifier>)request.getAttribute("setupMFAVerifiers");

int mfaVerifierIndex = ParamUtil.getInteger(request, "mfaVerifierIndex", 0);

if ((mfaVerifierIndex > -1) && (mfaVerifierIndex < setupMFAVerifiers.size())) {
	browserMFAVerifier = setupMFAVerifiers.get(mfaVerifierIndex);
}

%>
<portlet:actionURL name="/mfa_verify/setup" var="setupMFAActionURL">
	<portlet:param name="mvcRenderCommandName" value="/mfa_verify/setup" />
</portlet:actionURL>

<aui:form action="<%= setupMFAActionURL %>" cssClass="container-fluid-1280 sign-in-form" method="post" name="fm">
	<aui:input name="integrationName" type="hidden" value='<%= ParamUtil.getString(request, "integrationName") %>' />
	<aui:input name="mfaVerifierIndex" type="hidden" value='<%= String.valueOf(mfaVerifierIndex) %>' />
	<aui:input name="redirect" type="hidden" value='<%= ParamUtil.getString(request, "redirect") %>' />
	<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />

	<liferay-ui:error key="mfaFailed" message="multi-factor-authentication-setup-failed" />

	<%
		browserMFAVerifier.includeSetup(themeDisplay.getUserId(), request, response);
	%>

	<c:if test="<%= setupMFAVerifiers.size() > 1 %>">
		<portlet:renderURL var="setupAnotherMFAVerifier" copyCurrentRenderParameters="<%= true %>">
			<portlet:param name="integrationName" value='<%= ParamUtil.getString(request, "integrationName") %>' />
			<portlet:param name="mfaVerifierIndex" value="<%= mfaVerifierIndex + 1 < setupMFAVerifiers.size() ?  String.valueOf(mfaVerifierIndex + 1) : "0" %>"/>
			<portlet:param name="mvcRenderCommandName" value="/mfa_verify/setup" />
			<portlet:param name="redirect" value='<%= ParamUtil.getString(request, "redirect") %>' />
			<portlet:param name="saveLastPath" value="<%= Boolean.FALSE.toString() %>" />
		</portlet:renderURL>

		<a href="<%= HtmlUtil.escapeAttribute(setupAnotherMFAVerifier) %>"><liferay-ui:message key="setup-another-verifier" /></a>
	</c:if>

	<aui:button-row>
		<aui:button type="submit" value="submit" />
	</aui:button-row>
</aui:form>