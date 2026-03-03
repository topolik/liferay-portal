package com.liferay.keymanager.web;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * Admin UI portlet for Key Manager. Provides a web interface
 * for managing keys, running migrations, and viewing reports.
 *
 * TODO: Implement JSP views and action handlers.
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Key Manager",
		"javax.portlet.name=com_liferay_keymanager_web_KeyManagerPortlet",
		"javax.portlet.security-role-ref=administrator"
	},
	service = Portlet.class
)
public class KeyManagerPortlet extends MVCPortlet {
}
