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
String sendToEmail = (String)request.getAttribute("sendToEmail");
%>

<portlet:resourceURL id="/mfa_verify/sendotp" var="sendOTPURL" />

<h1>
	<liferay-ui:message key="your-one-time-password-will-be-sent-to" />
</h1>

<aui:input disabled="<%= true %>" id="sendToEmail" name="sendToEmail" showRequiredLabel="yes" value="<%= sendToEmail %>" />

<aui:button onclick='<%= liferayPortletResponse.getNamespace() + "send('" + sendOTPURL + "');" %>' value="send" />

<aui:script>
	function <portlet:namespace />send(sendOTPURL) {

		AUI.$.ajax(
			sendOTPURL
		)

	}
</aui:script>

<h1>
	<liferay-ui:message key="please-enter-your-2-factor-verification-code" />
</h1>

<aui:input name="otp" showRequiredLabel="yes" />