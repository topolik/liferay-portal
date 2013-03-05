/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.service.impl;

import com.liferay.portal.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.concurrent.LockRegistry;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.spring.aop.Skip;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.model.PortletPreferencesIds;
import com.liferay.portal.service.base.PortletPreferencesLocalServiceBaseImpl;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.PortletPreferencesImpl;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class PortletPreferencesLocalServiceImpl
	extends PortletPreferencesLocalServiceBaseImpl {

	public PortletPreferences addPortletPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, Portlet portlet, String defaultPreferences)
		throws SystemException {

		long portletPreferencesId = counterLocalService.increment();

		PortletPreferences portletPreferences =
			portletPreferencesPersistence.create(portletPreferencesId);

		portletPreferences.setOwnerId(ownerId);
		portletPreferences.setOwnerType(ownerType);
		portletPreferences.setPlid(plid);
		portletPreferences.setPortletId(portletId);

		if (Validator.isNull(defaultPreferences)) {
			if (portlet == null) {
				defaultPreferences = PortletConstants.DEFAULT_PREFERENCES;
			}
			else {
				defaultPreferences = portlet.getDefaultPreferences();
			}
		}

		portletPreferences.setPreferences(defaultPreferences);

		try {
			portletPreferencesPersistence.update(portletPreferences);
		}
		catch (SystemException se) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Add failed, fetch {ownerId=" + ownerId + ", ownerType=" +
						ownerType + ", plid=" + plid + ", portletId=" +
							portletId + "}");
			}

			portletPreferences = portletPreferencesPersistence.fetchByO_O_P_P(
				ownerId, ownerType, plid, portletId, false);

			if (portletPreferences == null) {
				throw se;
			}
		}

		return portletPreferences;
	}

	public javax.portlet.PortletPreferences addPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId)
		throws SystemException {

		return addPreferences(
			companyId, ownerId, ownerType, plid, portletId, null);
	}

	public javax.portlet.PortletPreferences addPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String defaultPreferences)
		throws SystemException {

		Portlet portlet = portletLocalService.getPortletById(
			companyId, portletId);

		PortletPreferences portletPreferences = addPortletPreferences(
			companyId, ownerId, ownerType, plid, portletId, portlet,
			defaultPreferences);

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId,
				portletPreferences.getPreferences());

		return portletPreferencesImpl;
	}

	public javax.portlet.PortletPreferences addPreferences(
			PortletPreferencesIds portletPreferencesIds)
		throws SystemException {

		return addPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId());
	}

	public void deletePortletPreferences(long ownerId, int ownerType, long plid)
		throws SystemException {

		portletPreferencesPersistence.removeByO_O_P(ownerId, ownerType, plid);
	}

	public void deletePortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException, SystemException {

		portletPreferencesPersistence.removeByO_O_P_P(
			ownerId, ownerType, plid, portletId);
	}

	public void deletePortletPreferencesByPlid(long plid)
		throws SystemException {

		portletPreferencesPersistence.removeByPlid(plid);
	}

	public javax.portlet.PortletPreferences fetchPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId)
		throws SystemException {

		PortletPreferences portletPreferences =
			portletPreferencesPersistence.fetchByO_O_P_P(
				ownerId, ownerType, plid, portletId);

		if (portletPreferences == null) {
			return null;
		}

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId,
				portletPreferences.getPreferences());

		return portletPreferencesImpl;
	}

	public javax.portlet.PortletPreferences fetchPreferences(
			PortletPreferencesIds portletPreferencesIds)
		throws SystemException {

		return fetchPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId());
	}

	@Skip
	public javax.portlet.PortletPreferences getDefaultPreferences(
			long companyId, String portletId)
		throws SystemException {

		Portlet portlet = portletLocalService.getPortletById(
			companyId, portletId);

		return PortletPreferencesFactoryUtil.fromDefaultXML(
			portlet.getDefaultPreferences());
	}

	public List<PortletPreferences> getPortletPreferences()
		throws SystemException {

		return portletPreferencesPersistence.findAll();
	}

	public List<PortletPreferences> getPortletPreferences(
			int ownerType, long plid, String portletId)
		throws SystemException {

		return portletPreferencesPersistence.findByO_P_P(
			ownerType, plid, portletId);
	}

	public List<PortletPreferences> getPortletPreferences(
			long ownerId, int ownerType, long plid)
		throws SystemException {

		return portletPreferencesPersistence.findByO_O_P(
			ownerId, ownerType, plid);
	}

	public PortletPreferences getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException, SystemException {

		return portletPreferencesPersistence.findByO_O_P_P(
			ownerId, ownerType, plid, portletId);
	}

	public List<PortletPreferences> getPortletPreferences(
			long companyId, long groupId, long ownerId, int ownerType,
			String portletId, boolean privateLayout)
		throws SystemException {

		return portletPreferencesFinder.findByC_G_O_O_P_P(
			companyId, groupId, ownerId, ownerType, portletId, privateLayout);
	}

	public List<PortletPreferences> getPortletPreferences(
			long plid, String portletId)
		throws SystemException {

		return portletPreferencesPersistence.findByP_P(plid, portletId);
	}

	public List<PortletPreferences> getPortletPreferencesByPlid(long plid)
		throws SystemException {

		return portletPreferencesPersistence.findByPlid(plid);
	}

	public long getPortletPreferencesCount(
			int ownerType, long plid, String portletId)
		throws SystemException {

		return portletPreferencesPersistence.countByO_P_P(
			ownerType, plid, portletId);
	}

	public javax.portlet.PortletPreferences getPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId)
		throws PortalException, SystemException {

		return getPreferences(
			companyId, ownerId, ownerType, plid, portletId, null);
	}

	public javax.portlet.PortletPreferences getPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String defaultPreferences)
		throws PortalException, SystemException {

		DB db = DBFactoryUtil.getDB();

		String dbType = db.getType();

		if (!dbType.equals(DB.TYPE_HYPERSONIC)) {
			return doGetPreferences(
				companyId, ownerId, ownerType, plid, portletId,
				defaultPreferences);
		}

		StringBundler sb = new StringBundler(7);

		sb.append(ownerId);
		sb.append(StringPool.POUND);
		sb.append(ownerType);
		sb.append(StringPool.POUND);
		sb.append(plid);
		sb.append(StringPool.POUND);
		sb.append(portletId);

		String groupName = getClass().getName();
		String key = sb.toString();

		Lock lock = LockRegistry.allocateLock(groupName, key);

		lock.lock();

		try {
			return doGetPreferences(
				companyId, ownerId, ownerType, plid, portletId,
				defaultPreferences);
		}
		finally {
			lock.unlock();

			LockRegistry.freeLock(groupName, key);
		}
	}

	public javax.portlet.PortletPreferences getPreferences(
			PortletPreferencesIds portletPreferencesIds)
		throws PortalException, SystemException {

		return getPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId());
	}

	public PortletPreferences updatePreferences(
			long ownerId, int ownerType, long plid, String portletId,
			javax.portlet.PortletPreferences portletPreferences)
		throws PortalException, SystemException {

		String xml = PortletPreferencesFactoryUtil.toXML(portletPreferences);

		return updatePreferences(ownerId, ownerType, plid, portletId, xml);
	}

	public PortletPreferences updatePreferences(
			long ownerId, int ownerType, long plid, String portletId,
			String xml)
		throws PortalException, SystemException {

		PortletPreferences portletPreferences =
			portletPreferencesPersistence.findByO_O_P_P(
				ownerId, ownerType, plid, portletId);

		portletPreferences.setPreferences(xml);

		portletPreferencesPersistence.update(portletPreferences);

		return portletPreferences;
	}

	protected javax.portlet.PortletPreferences doGetPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String defaultPreferences)
		throws PortalException, SystemException {

		PortletPreferences portletPreferences =
			portletPreferencesPersistence.fetchByO_O_P_P(
				ownerId, ownerType, plid, portletId);

		if (portletPreferences == null) {
			Portlet portlet = portletLocalService.getPortletById(
				companyId, portletId);

			if ((portlet != null) && portlet.isUndeployedPortlet()) {
				return new PortletPreferencesImpl();
			}

			throw new NoSuchPortletPreferencesException();
		}

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId,
				portletPreferences.getPreferences());

		return portletPreferencesImpl;
	}

	private static Log _log = LogFactoryUtil.getLog(
		PortletPreferencesLocalServiceImpl.class);

}