package com.liferay.keymanager.upgrade.migration.writer;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;

public interface ConfigurationWriter {

	DiscoveredSecret.Source getSourceType();

	boolean replaceValue(DiscoveredSecret secret, String keyReference) throws Exception;

}
