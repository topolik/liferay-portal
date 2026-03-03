package com.liferay.keymanager.upgrade.discovery.scanner;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;

import java.util.List;

public interface SecretScanner {

	DiscoveredSecret.Source getSourceType();

	String getName();

	List<DiscoveredSecret> scan() throws Exception;

	default int getPriority() {
		return 100;
	}

}
