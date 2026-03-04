/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.KeyProviderRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Liferay
 */
@Component(immediate = true, service = KeyProviderRegistry.class)
public class KeyProviderRegistryImpl implements KeyProviderRegistry {

	@Override
	public List<KeyProvider> getAllProviders() {
		return List.copyOf(_providers.values());
	}

	@Override
	public List<KeyProvider> getAvailableProviders() {
		return _providers.values(
		).stream(
		).filter(
			KeyProvider::isAvailable
		).sorted(
			(a, b) -> Integer.compare(a.getPriority(), b.getPriority())
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public Optional<KeyProvider> getProvider(String providerId) {
		return Optional.ofNullable(_providers.get(providerId));
	}

	@Override
	public void registerProvider(KeyProvider keyProvider) {
		String providerId = keyProvider.getProviderId();

		_providers.put(providerId, keyProvider);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Registered key provider: " + providerId + " (" +
					keyProvider.getDisplayName() + ")");
		}
	}

	@Override
	public void unregisterProvider(String providerId) {
		KeyProvider keyProvider = _providers.remove(providerId);

		if ((keyProvider != null) && _log.isInfoEnabled()) {
			_log.info("Unregistered key provider: " + providerId);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY, unbind = "_removeProvider"
	)
	private void _addProvider(KeyProvider keyProvider) {
		registerProvider(keyProvider);
	}

	private void _removeProvider(KeyProvider keyProvider) {
		unregisterProvider(keyProvider.getProviderId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyProviderRegistryImpl.class);

	private final Map<String, KeyProvider> _providers =
		new ConcurrentHashMap<>();

}
