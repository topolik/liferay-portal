package com.bemis.portal.twofa.device.manager.workflow.handler;

import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = {"model.class.name=com.bemis.portal.device.manager.model.DeviceCode"},
	service = WorkflowHandler.class
)
public class DeviceCodeWorkflowHandler extends BaseWorkflowHandler<DeviceCode> {

	@Override
	public String getClassName() {
		return DeviceCode.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public DeviceCode updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		return null;
	}

}