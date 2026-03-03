package com.liferay.keymanager.upgrade.discovery;

import java.util.List;

public interface SecretDiscoveryService {

	List<DiscoveredSecret> discoverAll();

	List<DiscoveredSecret> discoverBySource(DiscoveredSecret.Source source);

	List<DiscoveredSecret> discoverBySensitivity(DiscoveredSecret.Sensitivity minSensitivity);

}
