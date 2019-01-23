package com.bemis.portal.twofa.device.manager.lifecycle;

import com.bemis.portal.commons.service.BemisPortalService;
import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.workflow.util.WorkflowDefinitionUploadUtil;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class AddDeviceManagerContentLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_clusterMasterExecutor.isMaster()) {
			return;
		}

		long previousValue = CompanyThreadLocal.getCompanyId();
		long companyId = company.getCompanyId();

		CompanyThreadLocal.setCompanyId(companyId);

		try {
			uploadWorkflow(companyId);
		}
		finally {
			CompanyThreadLocal.setCompanyId(previousValue);
		}
	}

	protected ServiceContext getServiceContext(
		long companyId, User defaultUser) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(defaultUser.getUserId());

		return serviceContext;
	}

	protected void uploadWorkflow(long companyId) throws PortalException {
		User defaultUser = _bemisPortalService.getDefaultUser();

		ServiceContext serviceContext = getServiceContext(
			companyId, defaultUser);

		String definitionName =
			DeviceManagerConstants.WORKFLOW_DEF_TWO_FA_EMAIL_NOTIFICATION;

		boolean workflowExists =
			_workflowDefinitionUploadUtil.workflowDefinitionExists(
				definitionName, serviceContext);

		if (workflowExists) {
			if (_log.isInfoEnabled()) {
				_log.info(
					">>> Workflow Definition already exists for : " +
						definitionName);
			}

			return;
		}

		String workflowPath =
			"/workflow/two-factor-authentication-definition.xml";

		final String twoFaNotificationWorkflow =
			DeviceManagerConstants.RESOURCES_DIRECTORY + workflowPath;

		try {
			_workflowDefinitionUploadUtil.uploadWorkflow(
				twoFaNotificationWorkflow, serviceContext);

			if (_log.isInfoEnabled()) {
				_log.info(
					">>> Uploaded Workflow Definition for Custom Login Action");
			}
		}
		catch (PortalException pe) {
			_log.error(
				">>> Unable to upload workflow definition : " +
					definitionName,
				pe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddDeviceManagerContentLifecycleListener.class);

	@Reference
	private BemisPortalService _bemisPortalService;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private WorkflowDefinitionUploadUtil _workflowDefinitionUploadUtil;

	@Reference
	private WorkflowEngine _workflowEngine;

}