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
EmailOTPConfiguration emailOTPConfiguration = (EmailOTPConfiguration)request.getAttribute("emailOTPConfiguration");
%>
<h1>
	<liferay-ui:message key="<%= HtmlUtil.escape(emailOTPConfiguration.name()) %>"/>
</h1>

<div id="<portlet:namespace/>phaseOne">
	<c:choose>
		<c:when test="<%= emailOTPConfiguration.allowCustomEmail() %>">
			<h2>
				<liferay-ui:message key="your-one-time-password-will-be-sent-to" />
			</h2>

			<aui:input id="setupEmail" name="setupEmail" showRequiredLabel="yes" />
		</c:when>
		<c:otherwise>
			<h2>
				<liferay-ui:message key="your-one-time-password-will-be-sent-to-your-email-address" />
			</h2>

		</c:otherwise>
	</c:choose>

	<aui:button id="sendEmailButton" value="send" />
</div>

<div id="<portlet:namespace/>messageContainer"></div>

<div id="<portlet:namespace/>phaseTwo">
	<h2>
		<liferay-ui:message key="please-enter-the-one-time-password-from-the-email" />
	</h2>

	<aui:input name="otp" showRequiredLabel="yes" />

	<aui:button id="sendButton" onclick='<%= liferayPortletResponse.getNamespace() + "send();" %>' value="send" />
</div>


<aui:script use="aui-base,aui-io-request">
	<liferay-portlet:resourceURL id="/mfa_verify/sendemailotp" var="sendOTPURL" portletName="<%= MFAPortletKeys.MFA_VERIFY %>">
		<portlet:param name="mfaVerifierName" value="<%= emailOTPConfiguration.name() %>" />
	</liferay-portlet:resourceURL>

	A.one('#<portlet:namespace />sendEmailButton').on(
		'click',
		function(event) {
			var sendEmailButton = A.one('#<portlet:namespace />sendEmailButton');

			sendEmailButton.disabled = true;

			var buttonText = sendEmailButton.innerText;

			var resendDuration = <%= emailOTPConfiguration.resendEmailTimeout() %>;

			var interval = setInterval(
				function() {
					if (resendDuration === 0) {
						sendEmailButton.innerText = buttonText;
						sendEmailButton.disabled = false;

						clearInterval(interval);
					}
					else {
						sendEmailButton.innerText = --resendDuration;
					}

				},
				1000
			);

			var data = {
				p_auth: Liferay.authToken
			};

			var setupEmail = A.one('#<portlet:namespace />setupEmail');

			if (setupEmail) {
				data["email"] = setupEmail.value;
			}

			var sendOTPURL = '<%= HtmlUtil.escapeJS(sendOTPURL) %>';

			A.io.request(
				sendOTPURL,
				{
					dataType: 'JSON',
					data: data,
					method: 'POST',
					on: {
						failure: function(event, id, obj) {
							var messageContainer = A.one('#<portlet:namespace />messageContainer');
							messageContainer.html('<span class="alert alert-danger"><liferay-ui:message key="unable-to-send-email" /></span>');

							sendEmailButton.innerText = buttonText;
							sendEmailButton.disabled = false;

							clearInterval(interval);
						},
						success: function(event, id, obj) {
							var messageContainer = A.one('#<portlet:namespace />messageContainer');
							messageContainer.html('<span class="alert alert-success"><liferay-ui:message key="email-sent-please-enter-the-received-code" /></span>');

							var phaseTwo = A.one('#<portlet:namespace />phaseTwo');
							phaseTwo.disabled = false;
						}
					}
				}
			);
		}
	);
</aui:script>