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
int resendDuration = (Integer)request.getAttribute("resendDuration");
%>

<h1>
	<liferay-ui:message key="your-one-time-password-will-be-sent-to" />
</h1>

<aui:input id="setupEmail" name="setupEmail" showRequiredLabel="yes" />

<portlet:resourceURL id="/mfa_verify/sendotp" var="sendOTPURL" />

<aui:button id="sendButton" onclick='<%= liferayPortletResponse.getNamespace() + "send('" + sendOTPURL + "');" %>' value="send" />

<aui:script>
	function <portlet:namespace />send(sendOTPURL) {

		var button = document.getElementById('<portlet:namespace />sendButton');

		var buttonText = button.innerText;

		button.disabled = true;

		var resendDuration = '<%= resendDuration %>';

		window.setInterval(
			function() {
				if (sec === 0) {
					button.innerText = buttonText;
					button.disabled = false;
					window.clearInterval(resendDuration);
				}
				else {
					button.innerText = --resendDuration;
				}

			},
			1000
		);

		var email = document.getElementById('<portlet:namespace />setupEmail').value;

		AUI.$.ajax(
			sendOTPURL,
			{
				data: {
					<portlet:namespace />email:email
				}
			}
		)

	}
</aui:script>

<h1>
	<liferay-ui:message key="please-setup-your-2-factor-verification" />
</h1>

<aui:input name="otp" showRequiredLabel="yes" />