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

package com.liferay.server.admin.web.internal.background.task;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.server.admin.web.internal.background.task.display.UpgradeLegacyPasswordsBackgroundTaskDisplay;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	immediate = true,
	property = "background.task.executor.class.name=com.liferay.server.admin.web.internal.background.task.UpgradeLegacyPasswordsBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class UpgradeLegacyPasswordsBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public UpgradeLegacyPasswordsBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new UpgradeLegacyBackgroundTaskStatusMessageTranslator());

		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		long backgroundTaskId = backgroundTask.getBackgroundTaskId();

		try {
			_sendStatusMessage(backgroundTaskId, "start");

			String currentAlgorithm = PropsUtil.get(
				PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);

			currentAlgorithm = _getAlgorithmName(currentAlgorithm);

			DynamicQuery dynamicQuery = _userLocalService.dynamicQuery();

			String patternToSkip = "{" + currentAlgorithm + "%";

			Criterion pwc = RestrictionsFactoryUtil.like(
				"password", patternToSkip);

			dynamicQuery.add(RestrictionsFactoryUtil.not(pwc));

			dynamicQuery.add(RestrictionsFactoryUtil.eq("defaultUser", false));

			ProjectionList projectionList =
				ProjectionFactoryUtil.projectionList();

			projectionList.add(ProjectionFactoryUtil.property("userId"));
			projectionList.add(ProjectionFactoryUtil.property("password"));

			dynamicQuery.setProjection(projectionList);

			List<Object[]> objects = _userLocalService.dynamicQuery(
				dynamicQuery);

			for (int i = 0; i < objects.size(); ++i) {
				Object[] object = objects.get(i);

				long userId = (Long)object[0];

				String password = (String)object[1];

				String doubleHashed = PasswordEncryptorUtil.encrypt(
					password, password);

				_userLocalService.updatePasswordManually(
					userId, doubleHashed, true, true, new Date());

				_sendStatusMessage(backgroundTaskId, i + 1, objects.size());
			}

			return BackgroundTaskResult.SUCCESS;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			_sendStatusMessage(backgroundTaskId, "end");
		}
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return new UpgradeLegacyPasswordsBackgroundTaskDisplay(backgroundTask);
	}

	private String _getAlgorithmName(String algorithm) {
		int index = algorithm.indexOf(CharPool.SLASH);

		if (index > 0) {
			algorithm = algorithm.substring(0, index);
		}

		return StringUtil.toUpperCase(algorithm);
	}

	private void _sendStatusMessage(
		long backgroundTaskId, long count, long total) {

		Message message = new Message();

		message.put("percentage", count * 100 / total);

		_sendStatusMessage(message, backgroundTaskId);
	}

	private void _sendStatusMessage(long backgroundTaskId, String phase) {
		Message message = new Message();

		message.put("phase", phase);

		_sendStatusMessage(message, backgroundTaskId);
	}

	private void _sendStatusMessage(Message message, long backgroundTaskId) {
		message.put(
			BackgroundTaskConstants.BACKGROUND_TASK_ID, backgroundTaskId);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

	@Reference
	private UserLocalService _userLocalService;

}