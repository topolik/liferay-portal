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
boolean facebookConnectAuthEnabled = FacebookConnectUtil.isEnabled(company.getCompanyId());
boolean facebookConnectVerifiedAccountRequired = FacebookConnectUtil.isVerifiedAccountRequired(company.getCompanyId());
String facebookConnectAppId = FacebookConnectUtil.getAppId(company.getCompanyId());

String facebookConnectAppSecret = FacebookConnectUtil.getAppSecret(company.getCompanyId());

if (Validator.isNotNull(facebookConnectAppSecret)) {
	facebookConnectAppSecret = Portal.TEMP_OBFUSCATION_VALUE;
}

String facebookConnectGraphURL = FacebookConnectUtil.getGraphURL(company.getCompanyId());
String facebookConnectOauthAuthURL = FacebookConnectUtil.getAuthURL(company.getCompanyId());
String facebookConnectOauthTokenURL = FacebookConnectUtil.getAccessTokenURL(company.getCompanyId());
String facebookConnectRedirectURL = FacebookConnectUtil.getRedirectURL(company.getCompanyId());
%>

<liferay-ui:error key="facebookConnectGraphURLInvalid" message="the-facebook-connect-graph-url-is-invalid" />
<liferay-ui:error key="facebookConnectOauthAuthURLInvalid" message="the-facebook-connect-oauth-auth-url-is-invalid" />
<liferay-ui:error key="facebookConnectOauthRedirectURLInvalid" message="the-facebook-connect-oauth-redirect-url-is-invalid" />
<liferay-ui:error key="facebookConnectOauthTokenURLInvalid" message="the-facebook-connect-oauth-token-url-is-invalid" />

<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/portal_settings/edit_company_facebook_connect_configuration" />

<aui:fieldset>
	<aui:input label="enabled" name="facebook--enabled" type="checkbox" value="<%= facebookConnectAuthEnabled %>" />

	<aui:input label="require-verified-account" name="facebook--verifiedAccountRequired" type="checkbox" value="<%= facebookConnectVerifiedAccountRequired %>" />

	<aui:input cssClass="lfr-input-text-container" label="application-id" name="facebook--appId" type="text" value="<%= facebookConnectAppId %>" />

	<aui:input cssClass="lfr-input-text-container" label="application-secret" name="facebook--appSecret" type="password" value="<%= facebookConnectAppSecret %>" />

	<aui:input cssClass="lfr-input-text-container" label="graph-url" name="facebook--graphURL" type="text" value="<%= facebookConnectGraphURL %>" />

	<aui:input cssClass="lfr-input-text-container" label="oauth-authentication-url" name="facebook--oauthAuthURL" type="text" value="<%= facebookConnectOauthAuthURL %>" />

	<aui:input cssClass="lfr-input-text-container" label="oauth-token-url" name="facebook--oauthTokenURL" type="text" value="<%= facebookConnectOauthTokenURL %>" />

	<aui:input cssClass="lfr-input-text-container" label="oauth-redirect-url" name="facebook--oauthRedirectURL" type="text" value="<%= facebookConnectRedirectURL %>" />
</aui:fieldset>