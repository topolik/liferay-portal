package com.liferay.portal.service.osgi.cxf.externalpolicy;

import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.attachment.external.DomainExpression;

import javax.xml.namespace.QName;

/**
 * @author Tomas Polesovsky
 */
public class WSDLReferenceDomainExpression implements DomainExpression {

	@Override
	public boolean appliesTo(BindingFaultInfo bfi) {
		if (bfi == null) {
			return false;
		}

		return appliesTo(bfi.getFaultInfo().getOperation(), getBindingFaultReference());
	}

	@Override
	public boolean appliesTo(BindingMessageInfo bmi) {
		if (bmi == null) {
			return false;
		}

		return appliesTo(bmi.getMessageInfo().getOperation(), getBindingMessageReference());
	}

	@Override
	public boolean appliesTo(BindingOperationInfo boi) {
		if (boi == null) {
			return false;
		}

		return appliesTo(boi.getOperationInfo(), getBindingOperationReference());
	}

	@Override
	public boolean appliesTo(EndpointInfo ei) {
		if (ei == null) {
			return false;
		}

		return appliesTo(ei.getInterface(), getEndpointReference());
	}

	@Override
	public boolean appliesTo(ServiceInfo si) {
		if (si == null) {
			return false;
		}

		return appliesTo(si.getInterface(), getServiceReference());
	}

	public QName getBindingFaultReference() {
		return _bindingFaultReference;
	}

	public QName getBindingMessageReference() {
		return _bindingMessageReference;
	}

	public QName getBindingOperationReference() {
		return _bindingOperationReference;
	}

	public QName getEndpointReference() {
		return _endpointReference;
	}

	public QName getServiceReference() {
		return _serviceReference;
	}

	public void setBindingFaultReference(QName bindingFaultReference) {
		this._bindingFaultReference = bindingFaultReference;
	}

	public void setBindingMessageReference(QName bindingMessageReference) {
		this._bindingMessageReference = bindingMessageReference;
	}

	public void setBindingOperationReference(QName bindingOperationReference) {
		this._bindingOperationReference = bindingOperationReference;
	}

	public void setEndpointReference(QName endpointReference) {
		this._endpointReference = endpointReference;
	}

	public void setServiceReference(QName _serviceReference) {
		this._serviceReference = _serviceReference;
	}

	protected boolean appliesTo(OperationInfo operationInfo, QName localReference) {
		return appliesTo(operationInfo.getInterface(), localReference);
	}

	protected boolean appliesTo(InterfaceInfo interfaceInfo, QName localReference) {
		return interfaceInfo.getName().equals(localReference);
	}

	private QName _bindingOperationReference;
	private QName _bindingMessageReference;
	private QName _bindingFaultReference;
	private QName _endpointReference;
	private QName _serviceReference;
}
