package com.bemis.portal.twofa.device.manager.workflow.util;

import static com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants.WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = WorkflowDefinitionUploadUtil.class)
public class WorkflowDefinitionUploadUtil {

	public WorkflowDefinition uploadWorkflow(
			String filePath, ServiceContext serviceContext)
		throws PortalException {

		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(filePath)) {
//			byte[] bytes = FileUtil.getBytes(inputStream);

			return _workflowEngine.deployWorkflowDefinition(
				WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION,WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION,
				inputStream, serviceContext);
		}
		catch (Exception e) {
			if (_log.isInfoEnabled()) {
				_log.info(">>> Unable to Upload workflow :" + e.getMessage());
			}

			throw new PortalException("Unable to Upload workflow : ", e);
		}
	}

	public boolean workflowDefinitionExists(
			String definitionName, ServiceContext context)
		throws PortalException {

		KaleoDefinition definition =
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				definitionName, context);

		if (definition == null) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowDefinitionUploadUtil.class);

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private WorkflowEngine _workflowEngine;

}