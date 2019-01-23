package com.bemis.portal.twofa.device.manager.web.internal.portlet;

import com.bemis.portal.twofa.device.manager.web.constants.DeviceManagerPortletKeys;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=Bemis",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Two Factor Auth Device Manager Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + DeviceManagerPortletKeys.DEVICE_MANAGER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class TwoFactorAuthDeviceManagerPortlet extends MVCPortlet {
}