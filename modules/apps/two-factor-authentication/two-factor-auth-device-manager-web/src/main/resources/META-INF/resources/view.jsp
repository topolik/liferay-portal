<%@ include file="/init.jsp" %>

<%
HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
long portalUserId = ParamUtil.getLong(httpServletRequest, "portalUserId");
boolean authVerified = ParamUtil.getBoolean(httpServletRequest, "authVerified");

if (portalUserId == 0) {
	try {
		portalUserId = (long)request.getAttribute("portalUserId");
		authVerified = (boolean)request.getAttribute("authVerified");
	}
	catch (NullPointerException npe) {

	}
}
%>

<liferay-ui:error key="error.verification-failed" message="error.verification-failed" />

<liferay-ui:success key="success.login-code-verified-successfully" message="success.login-code-verified-successfully" />

<portlet:actionURL name="verifyLoginCode" var="verifyLoginCodeURL" />

<portlet:actionURL name="registerVerifiedDevice" var="registerVerifiedDeviceURL" />

<aui:container cssClass="device-manager">
	<h2>
		<liferay-ui:message key="login-from-a-new-device-detected" />
	</h2>

	<br>

	<c:choose>
		<c:when test="<%= !authVerified %>">
			<p>
				<liferay-ui:message key="we-have-detected-that-you-are-signing-into-your-account-using-a-new-device-a-notification-with-a-verification-code-has-been-sent-to-the-email-address-associated-with-this-account" />
			</p>

			<p>
				<liferay-ui:message key="please-authorize-your-new-device-by-entering-the-verification-code-below" />
			</p>

			<aui:form action="${verifyLoginCodeURL}" method="POST">
				<aui:input name="portalUserId" type="hidden" value="<%= portalUserId %>" />

				<aui:input name="authVerified" type="hidden" value="<%= authVerified %>" />

				<aui:input helpMessage="please-enter-verification-code-from-email" label="verification-code" name="loginVerificationCode" required="true" />

				<aui:button cssClass="btn-not-rounded btn-primary-red" name="saveButton" primary="false" type="submit" value="verify" />
			</aui:form>
		</c:when>
		<c:otherwise>
			<p>
				<liferay-ui:message key="login-code-verification-successful" />
			</p>

			<p>
				<liferay-ui:message key="do-you-want-to-register-this-device-as-one-of-your-personal-devices-if-you-choose-otherwise-you-will-still-be-able-to-log-in-for-just-this-one-time" />
			</p>

			<aui:form action="${registerVerifiedDeviceURL}" method="POST">
				<aui:input name="portalUserId" type="hidden" value="<%= portalUserId %>" />

				<aui:input name="authVerified" type="hidden" value="<%= authVerified %>" />

				<aui:field-wrapper helpMessage="do-you-want-to-register-device" name="registerDevice" required="true">
					<aui:input checked="true" inlineLabel="right" label="yes" name="registerDevice" type="radio" value="1" />

					<aui:input inlineLabel="right" label="no" name="registerDevice" type="radio" value="0" />
				</aui:field-wrapper>

				<aui:button name="saveButton" primary="false" type="submit" value="verify-continue-login" />
			</aui:form>
		</c:otherwise>
	</c:choose>
</aui:container>