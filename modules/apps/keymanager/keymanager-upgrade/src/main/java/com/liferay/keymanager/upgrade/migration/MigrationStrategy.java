package com.liferay.keymanager.upgrade.migration;

public enum MigrationStrategy {
	HIGH_SENSITIVITY_ONLY,
	HIGH_AND_MEDIUM_SENSITIVITY,
	ALL_SECRETS,
	DRY_RUN
}
