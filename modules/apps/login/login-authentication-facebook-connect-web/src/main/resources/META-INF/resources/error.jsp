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

<div class="sheet sheet-lg">
	<div class="sheet-header">
		<div class="autofit-padded-no-gutters-x autofit-row">
			<div class="autofit-col autofit-col-expand">
				<h2 class="sheet-title">
					<liferay-ui:message key="failed-to-sign-in-using-this-facebook-account" />
				</h2>
			</div>
		</div>
	</div>

	<div class="sheet-text">
		<liferay-ui:error exception="<%= MustVerifyEmailAddressException.class %>" message="you-need-to-verify-your-email-address-on-facebook-first" />
		<liferay-ui:error exception="<%= StrangersNotAllowedException.class %>" message="only-known-users-are-allowed-to-sign-in-using-facebook" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" message="this-facebook-account-cannot-be-used-to-register-a-new-user-because-its-email-domain-is-reserved" />
	</div>

	<div class="sheet-footer">
		<aui:button-row>
			<aui:button onClick="window.close();" value="close" />
		</aui:button-row>
	</div>
</div>