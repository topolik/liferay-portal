/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.application.list.permissions;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Portlet;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true)
public class PanelAppPermissionsStartupListener
	implements PortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT);

		List<Portlet> portlets = new ArrayList<>(panelApps.size());

		for (PanelApp panelApp : panelApps) {
			portlets.add(panelApp.getPortlet());
		}

		_userPersonalSitePermissions.initPermissions(
			company.getCompanyId(), portlets);
	}

	@Reference(unbind = "-")
	protected void setPanelAppRegistry(PanelAppRegistry panelAppRegistry) {
		_panelAppRegistry = panelAppRegistry;
	}

	@Reference(unbind = "-")
	protected void setUserPersonalSitePermissions(
		UserPersonalSitePermissions userPersonalSitePermissions) {

		_userPersonalSitePermissions = userPersonalSitePermissions;
	}

	private PanelAppRegistry _panelAppRegistry;
	private UserPersonalSitePermissions _userPersonalSitePermissions;

}