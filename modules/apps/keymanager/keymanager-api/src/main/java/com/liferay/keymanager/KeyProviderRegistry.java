package com.liferay.keymanager;

import java.util.List;
import java.util.Optional;

public interface KeyProviderRegistry {

	void registerProvider(KeyProvider provider);

	void unregisterProvider(String providerId);

	Optional<KeyProvider> getProvider(String providerId);

	List<KeyProvider> getAllProviders();

	List<KeyProvider> getAvailableProviders();

}
