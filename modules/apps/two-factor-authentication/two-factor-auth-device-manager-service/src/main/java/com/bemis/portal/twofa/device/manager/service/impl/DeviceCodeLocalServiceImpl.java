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

package com.bemis.portal.twofa.device.manager.service.impl;

import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.DEVICE_CODE;
import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.LOGIN_VERIFICATION_URL;
import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.PORTAL_USER_ID;
import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.TWO_FA_ENABLED;
import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION;

import static com.liferay.asset.kernel.model.AssetLinkConstants.TYPE_RELATED;

import aQute.bnd.annotation.ProviderType;

import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.service.base.DeviceCodeLocalServiceBaseImpl;
import com.bemis.portal.twofa.device.manager.util.WorkflowUtil;
import com.bemis.portal.twofa.provider.notification.configuration.TwoFactorAuthNotificationConfigurator;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of the device code local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceCodeLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalServiceUtil
 */
@ProviderType
public class DeviceCodeLocalServiceImpl extends DeviceCodeLocalServiceBaseImpl {

	@Override
	public DeviceCode fetchDeviceCodeByPortalUserId(long userId) {
		return deviceCodePersistence.fetchByPortalUserId(userId);
	}

	@Override
	public String getVerificationURL(
		long portalUserId, String verificationBaseURL) {

		StringBundler verificationURL = new StringBundler();

		verificationURL.append(verificationBaseURL);
		verificationURL.append(LOGIN_VERIFICATION_URL);
		verificationURL.append(StringPool.QUESTION);
		verificationURL.append(PORTAL_USER_ID);
		verificationURL.append(StringPool.EQUAL);
		verificationURL.append(portalUserId);
		verificationURL.append(StringPool.AMPERSAND);
		verificationURL.append(TWO_FA_ENABLED);
		verificationURL.append(StringPool.EQUAL);
		verificationURL.append(true);

		return verificationURL.toString();
	}

	@Override
	public void removeDeviceCode(long userId) {
		DeviceCode deviceCode = fetchDeviceCodeByPortalUserId(userId);

		deviceCodeLocalService.deleteDeviceCode(deviceCode);
	}

	@Override
	public void storeDeviceCodeAndSendNotification(
			long portalUserId, String deviceIP, String secretKey,
			int validationCode, String verificationBaseURL)
		throws PortalException {

		User portalUser = userLocalService.getUser(portalUserId);

		long companyId = portalUser.getCompanyId();

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

		long groupId = companyGroup.getGroupId();

		User defaultUser = userLocalService.getDefaultUser(companyId);

		DeviceCode deviceCode = deviceCodePersistence.fetchByPortalUserId(
			portalUserId);

		// delete any existing device code, previously generated for user

		if (deviceCode != null) {
			WorkflowUtil.completeLoginVerificationWorkflow(
				deviceCode.getCompanyId(), deviceCode.getGroupId(),
				deviceCode.getDeviceCodeId());

			deviceCodeLocalService.deleteDeviceCode(deviceCode);
		}

		String className = DeviceCode.class.getName();

		deviceCode = deviceCodePersistence.create(
			counterLocalService.increment(className));

		deviceCode.setCompanyId(companyId);
		deviceCode.setGroupId(groupId);
		deviceCode.setUserId(defaultUser.getUserId());
		deviceCode.setUserName(defaultUser.getScreenName());
		deviceCode.setCreateDate(new Date());
		deviceCode.setModifiedDate(new Date());

		deviceCode.setPortalUserId(portalUserId);
		deviceCode.setPortalUserName(portalUser.getScreenName());
		deviceCode.setEmailAddress(portalUser.getEmailAddress());
		deviceCode.setDeviceCode(secretKey);
		deviceCode.setDeviceIP(deviceIP);
		deviceCode.setValidationCode(validationCode);

		deviceCodePersistence.update(deviceCode);

		// If 2FA notification type is configured to be sms
		// no need to start workflow to send email notification

		String providerNotificationType =
			_providerNotificationConfigurator.get2FANotificationType();

		if (StringUtil.equalsIgnoreCase(providerNotificationType, "sms")) {
			return;
		}

		// Convert device-code into an asset entry

		long assetCreatorId = portalUserId;
		long deviceCodeId = deviceCode.getDeviceCodeId();

		convertToAsset(groupId, className, assetCreatorId, deviceCodeId);

		startDeviceCodeWorkflow(
			portalUserId, secretKey, verificationBaseURL, companyId, portalUser,
			groupId, className, assetCreatorId, deviceCodeId);
	}

	protected void convertToAsset(
			long groupId, String className, long assetCreatorId,
			long deviceCodeId)
		throws PortalException {

		_assetEntryLocalService.updateEntry(
			assetCreatorId, groupId, null, null, className, deviceCodeId, null,
			0, null, null, true, false, null, null, null, null, null,
			DEVICE_CODE, null, null, null, null, 0, 0, (Double)null);

		_assetLinkLocalService.updateLinks(
			assetCreatorId, deviceCodeId, null, TYPE_RELATED);
	}

	protected void startDeviceCodeWorkflow(
			long portalUserId, String secretKey, String verificationBaseURL,
			long companyId, User portalUser, long groupId, String className,
			long assetCreatorId, long deviceCodeId)
		throws PortalException {

		// create workflow context

		Map<String, Serializable> workflowContext = new HashMap<>();

		String verificationURL = getVerificationURL(
			portalUserId, verificationBaseURL);

		workflowContext.put("verificationBaseURL", verificationBaseURL);

		workflowContext.put("verificationURL", verificationURL);

		workflowContext.put("verificationCode", secretKey);
		workflowContext.put("portalUserName", portalUser.getFullName());

		//start workflow
		WorkflowUtil.startWorkflow(
			companyId, groupId, assetCreatorId, deviceCodeId, className,
			WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION, workflowContext);
	}

	@ServiceReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@ServiceReference(type = AssetLinkLocalService.class)
	private AssetLinkLocalService _assetLinkLocalService;

	@ServiceReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@ServiceReference(type = TwoFactorAuthNotificationConfigurator.class)
	private TwoFactorAuthNotificationConfigurator
		_providerNotificationConfigurator;

}