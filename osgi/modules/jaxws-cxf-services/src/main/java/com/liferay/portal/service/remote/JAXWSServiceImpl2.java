package com.liferay.portal.service.remote;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tomas Polesovsky
 */

public class JAXWSServiceImpl2 implements JAXWSService2 {
	@Override
	public JAXWSServiceObject getObject() {
		JAXWSServiceObject result = new JaxWsServiceObjectImpl(
			new ArrayList<JAXWSServiceObject>());

		result.setDateParam(new Date());
		result.setIntParam(42);
		result.setStringParam("Hello service!");
		result.getChildren().add(result);

		return result;
	}

	@Override
	public void setObject(JAXWSServiceObject jaxwsServiceObject) {
		if (jaxwsServiceObject == null) {
			throw new RuntimeException("Object is null!");
		}

		System.out.println("================");
		System.out.println("Date param: " + jaxwsServiceObject.getDateParam());
		System.out.println("Int param: " + jaxwsServiceObject.getIntParam());
		System.out.println("String param: " + jaxwsServiceObject.getStringParam());

		System.out.println(jaxwsServiceObject.toString());
	}
}
