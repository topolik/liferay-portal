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

<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<liferay-ui:error-marker key="errorSection" value="authentication" />

<h3><liferay-ui:message key="authentication" /></h3>

<%
String key = "portal.settings.authentication";

List<DynamicInclude> dynamicIncludes = new ArrayList<DynamicInclude>();

StringBundler tabNamesStringBundler = new StringBundler(2);

tabNamesStringBundler.append(StringUtil.merge(
	PropsValues.COMPANY_SETTINGS_FORM_AUTHENTICATION));

ServiceTracker<DynamicInclude, DynamicInclude> serviceTracker = null;

try {
	Registry registry = RegistryUtil.getRegistry();

	com.liferay.registry.Filter filter = registry.getFilter(
		"(&(&(objectClass=" + DynamicInclude.class.getName() +
			")(key=" + key + "))(label=*))");

	serviceTracker = registry.trackServices(filter);

	serviceTracker.open();

	ServiceReference<DynamicInclude>[] serviceReferences =
		serviceTracker.getServiceReferences();

	StringBundler dynamicIncludesTabNames = new StringBundler(
		serviceReferences.length * 2);

	for (ServiceReference<DynamicInclude> serviceReference :
		serviceReferences) {

		Object label = serviceReference.getProperty("label");
		if (label != null) {
			dynamicIncludesTabNames.append(StringPool.COMMA);
			dynamicIncludesTabNames.append(label);
		}

		dynamicIncludes.add(serviceTracker.getService(serviceReference));
	}

	tabNamesStringBundler.append(dynamicIncludesTabNames);
}
finally {
	if (serviceTracker != null) {
		serviceTracker.close();
	}
}

String tabNames = tabNamesStringBundler.toString();
if (tabNames.startsWith(StringPool.COMMA)) {
	tabNames = tabNames.substring(1);
}
%>

<liferay-ui:tabs
	names="<%= tabNames %>"
	refresh="<%= false %>"
>

	<%
	for (String section : PropsValues.COMPANY_SETTINGS_FORM_AUTHENTICATION) {
	%>

		<liferay-ui:section>
			<liferay-util:include page='<%= "/html/portlet/portal_settings/authentication/" + _getSectionJsp(section) + ".jsp" %>' portletId="<%= portletDisplay.getRootPortletId() %>" />
		</liferay-ui:section>

	<%
	}

	for (DynamicInclude dynamicInclude : dynamicIncludes) {
	%>

		<liferay-ui:section>

			<%
				dynamicInclude.include(request, response, key);
			%>

		</liferay-ui:section>

	<%
	}
	%>

</liferay-ui:tabs>

<%!
private String _getSectionJsp(String name) {
	return TextFormatter.format(name, TextFormatter.N);
}
%>