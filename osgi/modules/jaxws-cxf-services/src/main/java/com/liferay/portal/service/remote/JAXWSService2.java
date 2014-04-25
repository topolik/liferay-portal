package com.liferay.portal.service.remote;

import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

import javax.jws.Oneway;
import javax.jws.WebService;

/**
 * @author Tomas Polesovsky
 */

@WebService
public interface JAXWSService2 {

	public JAXWSServiceObject getObject();

	@Oneway
	public void setObject(JAXWSServiceObject jaxwsServiceObject);
}
