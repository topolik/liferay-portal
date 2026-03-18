/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseGcpFactory<T> {

	protected void activate(
		Map<String, Object> properties, ComponentFactory<T> componentFactory,
		boolean systemScope) {

		_componentInstance = componentFactory.newInstance(
			HashMapDictionaryBuilder.<String, Object>putAll(
				properties
			).put(
				"systemScope", systemScope
			).remove(
				Constants.SERVICE_PID
			).build());
	}

	protected void deactivate() {
		if (_componentInstance != null) {
			_componentInstance.dispose();
		}
	}

	private ComponentInstance<T> _componentInstance;

}