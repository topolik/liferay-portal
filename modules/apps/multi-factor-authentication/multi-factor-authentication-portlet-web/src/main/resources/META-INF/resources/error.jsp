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

<liferay-ui:error-header />

<c:if test='<%= SessionErrors.contains(request, "noVerifierConfigured") %>'>
	<liferay-ui:error key="noVerifierConfigured" message="<%= LanguageUtil.format(request, "no-multi-factor-authentication-verifier-configured-for-x", HtmlUtil.escape(GetterUtil.getString(SessionErrors.get(request, "noVerifierConfigured")))) %>" />
</c:if>

<liferay-ui:error key="sessionExpired" message="your-session-expired" />

<c:if test='<%= SessionErrors.contains(request, "unknownMFAIntegrationName") %>'>
	<liferay-ui:error key="unknownMFAIntegrationName" message="<%= LanguageUtil.format(request, "unknown-multi-factor-authentication-integration-x", HtmlUtil.escape(GetterUtil.getString(SessionErrors.get(request, "unknownMFAIntegrationName")))) %>" />
</c:if>

<liferay-ui:error key="unsupportedIntegrationVerifier" message="internal-error-please-see-log-messages" />

<liferay-ui:error-principal />