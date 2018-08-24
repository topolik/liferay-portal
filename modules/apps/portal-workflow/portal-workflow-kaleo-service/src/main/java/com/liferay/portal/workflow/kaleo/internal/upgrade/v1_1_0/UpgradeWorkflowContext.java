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

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_1_0;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v1_3_0.WorkflowContextUpgradeHelper;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

/**
 * @author Jang Kim
 */
public class UpgradeWorkflowContext extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		JSONFactory jsonFactory = JSONFactoryUtil.getJSONFactory();

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		if (jsonFactory == null) {
			jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
		}

		try {
			updateTable("KaleoInstance", "kaleoInstanceId");
			updateTable("KaleoLog", "kaleoLogId");
			updateTable("KaleoTaskInstanceToken", "kaleoTaskInstanceTokenId");
		}
		finally {
			jsonFactoryUtil.setJSONFactory(jsonFactory);
		}
	}

	protected void updateTable(String tableName, String fieldName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(tableName);
			PreparedStatement ps = connection.prepareStatement(
				StringBundler.concat(
					"select ", fieldName, ", workflowContext from ", tableName,
					" where workflowContext is not null and workflowContext ",
					"not like '%serializable%'"));
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				long fieldValue = rs.getLong(fieldName);
				String workflowContextJSON = rs.getString("workflowContext");

				if (Validator.isNull(workflowContextJSON)) {
					continue;
				}

				workflowContextJSON =
					_workflowContextUpgradeHelper.renamePortalClassNames(
						workflowContextJSON);

				Map<String, Serializable> workflowContext =
					WorkflowContextUtil.convert(workflowContextJSON);

				workflowContext =
					_workflowContextUpgradeHelper.renameEntryClassName(
						workflowContext);

				updateWorkflowContext(
					tableName, fieldName, fieldValue,
					WorkflowContextUtil.convert(workflowContext));
			}
		}
	}

	protected void updateWorkflowContext(
			String tableName, String primaryKeyName, long primaryKeyValue,
			String workflowContext)
		throws Exception {

		try (PreparedStatement ps = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set workflowContext = ? where ",
					primaryKeyName, " = ?"))) {

			ps.setString(1, workflowContext);
			ps.setLong(2, primaryKeyValue);

			ps.executeUpdate();
		}
	}

	private final WorkflowContextUpgradeHelper _workflowContextUpgradeHelper =
		new WorkflowContextUpgradeHelper();

}