package com.bemis.portal.twofa.device.manager.util;

import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Thuong Dinh
 * @author Prathima Shreenath
 */
public class WorkflowUtil {

	public static void completeLoginVerificationWorkflow(
			long companyId, long groupId, long deviceCodeId)
		throws PortalException {

		WorkflowInstanceLink workflowInstanceLink =
			WorkflowInstanceLinkLocalServiceUtil.fetchWorkflowInstanceLink(
				companyId, groupId, DeviceCode.class.getName(), deviceCodeId);

		if (workflowInstanceLink == null) {
			return;
		}

		WorkflowInstanceLinkLocalServiceUtil.deleteWorkflowInstanceLink(
			workflowInstanceLink);
	}

	public static void startWorkflow(
			long companyId, long groupId, long userId, long classPK,
			String className, String workflowDefinition,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		WorkflowDefinition definition =
			WorkflowDefinitionManagerUtil.getLatestKaleoDefinition(
				companyId, workflowDefinition);

		String definitionName = definition.getName();

		ServiceContext serviceContext = new ServiceContext();

		workflowContext.put(
			WorkflowConstants.CONTEXT_COMPANY_ID, String.valueOf(companyId));
		workflowContext.put(
			WorkflowConstants.CONTEXT_GROUP_ID, String.valueOf(groupId));
		workflowContext.put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME, className);
		workflowContext.put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK, String.valueOf(classPK));
		workflowContext.put(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext);

		workflowContext.put(
			WorkflowConstants.CONTEXT_NOTIFICATION_SENDER_ADDRESS,
			"NoReply@bemis.com");
		workflowContext.put(
			WorkflowConstants.CONTEXT_NOTIFICATION_SUBJECT,
			"Verification Code: Activate your new device at The Hub™");
		workflowContext.put(
			WorkflowConstants.CONTEXT_NOTIFICATION_SENDER_NAME,
			"The Hub™, Bemis Customer Portal");

		//start workflow instance

		WorkflowInstance workflowInstance =
			WorkflowInstanceManagerUtil.startWorkflowInstance(
				companyId, groupId, userId, definitionName,
				definition.getVersion(), StringPool.BLANK, workflowContext);

		WorkflowInstanceLinkLocalServiceUtil.addWorkflowInstanceLink(
			userId, companyId, groupId, className, classPK,
			workflowInstance.getWorkflowInstanceId());

		if (_log.isDebugEnabled()) {
			_log.debug(">>> Workflow started for : " + definitionName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(WorkflowUtil.class);

}