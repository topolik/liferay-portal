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

@Component(immediate = true, service = KeyProviderRegistry.class)
public class KeyProviderRegistryImpl implements KeyProviderRegistry {

	@Override
	public void registerProvider(KeyProvider provider) {
		String providerId = provider.getProviderId();

		_providers.put(providerId, provider);

		if (_log.isInfoEnabled()) {
			_log.info("Registered key provider: " + providerId + " (" + provider.getDisplayName() + ")");
		}
	}

	@Override
	public void unregisterProvider(String providerId) {
		KeyProvider removed = _providers.remove(providerId);

		if (removed != null && _log.isInfoEnabled()) {
			_log.info("Unregistered key provider: " + providerId);
		}
	}

	@Override
	public Optional<KeyProvider> getProvider(String providerId) {
		return Optional.ofNullable(_providers.get(providerId));
	}

	@Override
	public List<KeyProvider> getAllProviders() {
		return List.copyOf(_providers.values());
	}

	@Override
	public List<KeyProvider> getAvailableProviders() {
		return _providers.values().stream()
			.filter(KeyProvider::isAvailable)
			.sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
			.collect(Collectors.toList());
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "_removeProvider"
	)
	private void _addProvider(KeyProvider provider) {
		registerProvider(provider);
	}

	private void _removeProvider(KeyProvider provider) {
		unregisterProvider(provider.getProviderId());
	}

	private static final Log _log = LogFactoryUtil.getLog(KeyProviderRegistryImpl.class);

	private final Map<String, KeyProvider> _providers = new ConcurrentHashMap<>();

}
