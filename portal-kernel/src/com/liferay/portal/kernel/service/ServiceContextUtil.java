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

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class ServiceContextUtil {

	public static Object deserialize(JSONObject jsonObject) {
		return deserialize(jsonObject.toString());
	}

	public static ServiceContext deserialize(String json) {
		Map serviceContextMap = JSONFactoryUtil.looseDeserialize(
			json, Map.class);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(
			GetterUtil.getBoolean(
				serviceContextMap.get("addGroupPermissions"),
				serviceContext.isAddGroupPermissions()));
		serviceContext.setAddGuestPermissions(
			GetterUtil.getBoolean(
				serviceContextMap.get("addGuestPermissions"),
				serviceContext.isAddGuestPermissions()));
		serviceContext.setAssetCategoryIds(
			GetterUtil.getLongValues(
				serviceContextMap.get("assetCategoryIds"),
				serviceContext.getAssetCategoryIds()));
		serviceContext.setAssetEntryVisible(
			GetterUtil.getBoolean(
				serviceContextMap.get("assetEntryVisible"),
				serviceContext.isAssetEntryVisible()));
		serviceContext.setAssetLinkEntryIds(
			GetterUtil.getLongValues(
				serviceContextMap.get("assetLinkEntryIds"),
				serviceContext.getAssetLinkEntryIds()));
		serviceContext.setAssetPriority(
			GetterUtil.getDouble(
				serviceContextMap.get("assetPriority"),
				serviceContext.getAssetPriority()));
		serviceContext.setAssetTagNames(
			GetterUtil.getStringValues(
				serviceContextMap.get("assetTagNames"),
				serviceContext.getAssetTagNames()));

		if (serviceContextMap.containsKey("attributes")) {
			serviceContext.setAttributes(
				(Map<String, Serializable>)serviceContextMap.get("attributes"));
		}

		serviceContext.setCommand(
			GetterUtil.getString(
				serviceContextMap.get("command"), serviceContext.getCommand()));
		serviceContext.setCompanyId(
			GetterUtil.getLong(
				serviceContextMap.get("companyId"),
				serviceContext.getCompanyId()));
		serviceContext.setCreateDate(
			GetterUtil.getDate(
				serviceContextMap.get("createDate"),
				DateFormatFactoryUtil.getDate(LocaleUtil.getDefault()),
				serviceContext.getCreateDate()));
		serviceContext.setCurrentURL(
			GetterUtil.getString(
				serviceContextMap.get("currentURL"),
				serviceContext.getCurrentURL()));
		serviceContext.setDeriveDefaultPermissions(
			GetterUtil.getBoolean(
				serviceContextMap.get("deriveDefaultPermissions"),
				serviceContext.isDeriveDefaultPermissions()));

		if (serviceContextMap.containsKey("expandoBridgeAttributes")) {
			serviceContext.setExpandoBridgeAttributes(
				(Map<String, Serializable>)serviceContextMap.get(
					"expandoBridgeAttributes"));
		}

		serviceContext.setFailOnPortalException(
			GetterUtil.getBoolean(
				serviceContextMap.get("failOnPortalException"),
				serviceContext.isFailOnPortalException()));
		serviceContext.setFormDate(
			GetterUtil.getDate(
				serviceContextMap.get("formDate"),
				DateFormatFactoryUtil.getDate(LocaleUtil.getDefault()),
				serviceContext.getFormDate()));
		serviceContext.setGroupPermissions(
			GetterUtil.getStringValues(
				serviceContextMap.get("groupPermissions"),
				serviceContext.getGroupPermissions()));
		serviceContext.setGuestPermissions(
			GetterUtil.getStringValues(
				serviceContextMap.get("groupPermissions"),
				serviceContext.getGuestPermissions()));
		serviceContext.setIndexingEnabled(
			GetterUtil.getBoolean(
				serviceContextMap.get("indexingEnabled"),
				serviceContext.isIndexingEnabled()));
		serviceContext.setLanguageId(
			GetterUtil.getString(
				serviceContextMap.get("languageId"),
				serviceContext.getLanguageId()));
		serviceContext.setLayoutFullURL(
			GetterUtil.getString(
				serviceContextMap.get("layoutFullURL"),
				serviceContext.getLayoutFullURL()));
		serviceContext.setLayoutURL(
			GetterUtil.getString(
				serviceContextMap.get("layoutURL"),
				serviceContext.getLayoutURL()));
		serviceContext.setModifiedDate(
			GetterUtil.getDate(
				serviceContextMap.get("modifiedDate"),
				DateFormatFactoryUtil.getDate(LocaleUtil.getDefault()),
				serviceContext.getModifiedDate()));
		serviceContext.setPathFriendlyURLPrivateGroup(
			GetterUtil.getString(
				serviceContextMap.get("pathFriendlyURLPrivateGroup"),
				serviceContext.getPathFriendlyURLPrivateGroup()));
		serviceContext.setPathFriendlyURLPrivateUser(
			GetterUtil.getString(
				serviceContextMap.get("pathFriendlyURLPrivateUser"),
				serviceContext.getPathFriendlyURLPrivateUser()));
		serviceContext.setPathFriendlyURLPublic(
			GetterUtil.getString(
				serviceContextMap.get("pathFriendlyURLPublic"),
				serviceContext.getPathFriendlyURLPublic()));
		serviceContext.setPathMain(
			GetterUtil.getString(
				serviceContextMap.get("pathMain"),
				serviceContext.getPathMain()));
		serviceContext.setPlid(
			GetterUtil.getLong(
				serviceContextMap.get("plid"), serviceContext.getPlid()));
		serviceContext.setPortalURL(
			GetterUtil.getString(
				serviceContextMap.get("portalURL"),
				serviceContext.getPortalURL()));
		serviceContext.setPortletId(
			GetterUtil.getString(
				serviceContextMap.get("portletId"),
				serviceContext.getPortletId()));

		Map<String, Serializable> portletPreferencesIdsMap =
			(Map<String, Serializable>)serviceContextMap.get(
				"portletPreferencesIds");

		if (portletPreferencesIdsMap != null) {
			serviceContext.setPortletPreferencesIds(
				new PortletPreferencesIds(
					GetterUtil.getLong(
						portletPreferencesIdsMap.get("companyId")),
					GetterUtil.getLong(portletPreferencesIdsMap.get("ownerId")),
					GetterUtil.getInteger(
						portletPreferencesIdsMap.get("ownerType")),
					GetterUtil.getLong(portletPreferencesIdsMap.get("plid")),
					GetterUtil.getString(
						portletPreferencesIdsMap.get("portletId"))));
		}

		serviceContext.setRemoteAddr(
			GetterUtil.getString(
				serviceContextMap.get("remoteAddr"),
				serviceContext.getRemoteAddr()));
		serviceContext.setRemoteHost(
			GetterUtil.getString(
				serviceContextMap.get("remoteHost"),
				serviceContext.getRemoteHost()));
		serviceContext.setScopeGroupId(
			GetterUtil.getLong(
				serviceContextMap.get("scopeGroupId"),
				serviceContext.getScopeGroupId()));
		serviceContext.setSignedIn(
			GetterUtil.getBoolean(
				serviceContextMap.get("signedIn"),
				serviceContext.isSignedIn()));
		serviceContext.setUserDisplayURL(
			GetterUtil.getString(
				serviceContextMap.get("userDisplayURL"),
				serviceContext.getUserDisplayURL()));
		serviceContext.setUserId(
			GetterUtil.getLong(
				serviceContextMap.get("userId"), serviceContext.getUserId()));
		serviceContext.setUuid(
			GetterUtil.getString(
				serviceContextMap.get("uuid"), serviceContext.getUuid()));
		serviceContext.setWorkflowAction(
			GetterUtil.getInteger(
				serviceContextMap.get("workflowAction"),
				serviceContext.getWorkflowAction()));

		return serviceContext;
	}

	public static PortletPreferences getPortletPreferences(
		ServiceContext serviceContext) {

		if (serviceContext == null) {
			return null;
		}

		PortletPreferencesIds portletPreferencesIds =
			serviceContext.getPortletPreferencesIds();

		if (portletPreferencesIds == null) {
			return null;
		}
		else {
			return PortletPreferencesLocalServiceUtil.getPreferences(
				portletPreferencesIds.getCompanyId(),
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(),
				portletPreferencesIds.getPlid(),
				portletPreferencesIds.getPortletId());
		}
	}

}