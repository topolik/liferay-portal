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

<%@include file="../init.jsp"%>

<aui:nav-bar cssClass="navbar-no-collapse" markupView="lexicon">
	<aui:nav collapsible="<%= false %>" cssClass="navbar-nav">
		<aui:nav-item label="OAuth2 Applications" selected="<%= true %>" />
	</aui:nav>
</aui:nav-bar>

<div class="closed container-fluid-1280">

	<liferay-ui:search-container
	    total="<%= OAuth2ApplicationLocalServiceUtil.getOAuth2ApplicationsCount() %>">
	    <liferay-ui:search-container-results
	        results="<%= OAuth2ApplicationLocalServiceUtil.getOAuth2Applications( 
	            searchContainer.getStart(), searchContainer.getEnd()) %>" />
	
	    <liferay-ui:search-container-row
	        className="com.liferay.oauth2.provider.model.OAuth2Application" modelVar="oAuth2Application">
	
	        <liferay-ui:search-container-column-text property="name" />
	        
	        <liferay-ui:search-container-column-text property="description" />
	
	        <liferay-ui:search-container-column-jsp
	            align="right" 
	            path="/admin/oauth2application_actions.jsp" />
	
	    </liferay-ui:search-container-row>
	
	    <liferay-ui:search-iterator 
    			markupView="lexicon"
    			searchContainer="<%= searchContainer %>"/>
		</liferay-ui:search-container>

    <portlet:renderURL var="addOAuth2ApplicationURL">
        <portlet:param name="mvcPath"
            value="/admin/edit_oauth2application.jsp" />
        <portlet:param name="redirect" value="<%= currentURL %>" />
    </portlet:renderURL>
	
	<liferay-frontend:add-menu>
	    <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request,
	    "add-o-auth2-application") %>' url="<%= addOAuth2ApplicationURL.toString() %>" />
	</liferay-frontend:add-menu>	
	
</div>