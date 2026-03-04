package com.liferay.keymanager.web;

import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Admin UI portlet for Key Manager. Provides a web interface
 * for managing keys, running migrations, and viewing reports.
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Key Manager",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=com_liferay_keymanager_web_KeyManagerPortlet",
		"javax.portlet.security-role-ref=administrator"
	},
	service = Portlet.class
)
public class KeyManagerPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			"availableProviders", _keyResolverService.getAvailableProviders());

		super.render(renderRequest, renderResponse);
	}

	public void deleteKey(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String providerId = ParamUtil.getString(actionRequest, "providerId");
		String alias = ParamUtil.getString(actionRequest, "alias");

		for (KeyProvider keyProvider : _keyResolverService.getAvailableProviders()) {
			if (keyProvider.getProviderId().equals(providerId)) {
				keyProvider.deleteKey(alias);

				_keyResolverService.invalidateCache(
					_keyResolverService.createReference(providerId, alias));

				SessionMessages.add(actionRequest, "keyDeleted");

				return;
			}
		}

		SessionErrors.add(actionRequest, "providerNotFound");
	}

	public void storeKey(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String providerId = ParamUtil.getString(actionRequest, "providerId");
		String alias = ParamUtil.getString(actionRequest, "alias");
		String value = ParamUtil.getString(actionRequest, "value");

		if (alias.isEmpty() || value.isEmpty()) {
			SessionErrors.add(actionRequest, "missingFields");

			return;
		}

		try {
			_keyResolverService.storeAndReference(
				providerId, alias, value.toCharArray());

			SessionMessages.add(actionRequest, "keyStored");
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	@Reference
	private KeyResolverService _keyResolverService;

}
