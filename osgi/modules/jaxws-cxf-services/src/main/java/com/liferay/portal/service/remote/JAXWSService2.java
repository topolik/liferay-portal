package com.liferay.portal.service.remote;

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

import javax.jws.Oneway;
import javax.jws.WebService;

/**
 * @author Tomas Polesovsky
 */

@JSONWebService
public interface JAXWSService2 {

	public JAXWSServiceObject getObject();

	public void setObject(JAXWSServiceObject jaxwsServiceObject);
}
