package com.liferay.portal.service.remote;

import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
@XmlType
public interface JAXWSServiceObject {
	public String getStringParam();

	public void setStringParam(String stringParam);

	public Date getDateParam();
	public void setDateParam(Date dateParam);

	public int getIntParam();

	public void setIntParam(int intParam);

	public List<JAXWSServiceObject> getChildren();
}
