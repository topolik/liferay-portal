package com.liferay.keymanager.upgrade.migration;

public interface SecretMigrationService {

	MigrationPlan createPlan(MigrationStrategy strategy, String targetProviderId);

	MigrationResult executePlan(MigrationPlan plan);

}
