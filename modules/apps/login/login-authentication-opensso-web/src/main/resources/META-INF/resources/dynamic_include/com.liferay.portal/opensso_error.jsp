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

<%@ include file="/dynamic_include/init.jsp" %>

<%
String openSSOSubjectScreenName = (String)request.getAttribute("open.sso.subject.screenName");
%>

<script type="text/javascript">
	// <![CDATA[
	AUI().use(
		'liferay-notification',
		function(A) {
			new Liferay.Notification(
				{
					closeable: true,
					delay: {
						hide: 10000,
						show: 0
					},
					duration: 500,
					message: '<liferay-ui:message arguments='<%= "<strong>" + HtmlUtil.escapeAttribute(openSSOSubjectScreenName) + "</strong>" %>' key="your-user-x-could-not-be-logged-in" /> <a href="<%= request.getAttribute("open.sso.error.link") %>"><liferay-ui:message key="click-here-for-more-details" /></a>',
					render: true,
					title: '<liferay-ui:message key="warning" />',
					type: 'warning'
				}
			).render('body');
		}
	);
	// ]]>
</script>