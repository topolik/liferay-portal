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

package com.liferay.portal.workflow.kaleo.runtime.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class WorkflowContextUtil {

	public static final String WORKFLOW_CONTEXT_NAME = "workflowContext";

	public static String convert(Map<String, Serializable> workflowContext) {
		if (workflowContext == null) {
			return StringPool.BLANK;
		}

		return JSONFactoryUtil.serialize(workflowContext);
	}

	public static Map<String, Serializable> convert(String json) {
		if (Validator.isNull(json)) {
			return new HashMap<>();
		}

		Map<String, Serializable> workflowContext =
			(Map<String, Serializable>)JSONFactoryUtil.deserialize(json);

		Serializable serviceContextSerializable = workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		if (serviceContextSerializable != null) {
			ServiceContext serviceContext = ServiceContextUtil.deserialize(
				JSONFactoryUtil.looseSerialize(serviceContextSerializable));

			workflowContext.put(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext);
		}

		return workflowContext;
	}

	public static void mergeWorkflowContexts(
		ExecutionContext executionContext,
		Map<String, Serializable> workflowContext) {

		if ((workflowContext != null) && !workflowContext.isEmpty()) {
			Map<String, Serializable> executionContextWorkflowContext =
				executionContext.getWorkflowContext();

			executionContextWorkflowContext.putAll(workflowContext);
		}
	}

}