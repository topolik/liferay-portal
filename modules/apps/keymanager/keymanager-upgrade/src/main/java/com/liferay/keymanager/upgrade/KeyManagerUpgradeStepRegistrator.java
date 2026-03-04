package com.liferay.keymanager.upgrade;

import com.liferay.keymanager.upgrade.internal.configuration.KeyManagerUpgradeStepRegistratorConfiguration;
import com.liferay.keymanager.upgrade.migration.MigrationStrategy;
import com.liferay.keymanager.upgrade.migration.SecretMigrationService;
import com.liferay.keymanager.upgrade.report.MigrationReportService;
import com.liferay.keymanager.upgrade.verification.MigrationVerificationService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(immediate = true, service = UpgradeStepRegistrator.class)
@Designate(ocd = KeyManagerUpgradeStepRegistratorConfiguration.class)
public class KeyManagerUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (!_autoMigrate) {
			_log.info("Key Manager auto-migration is disabled. Use Gogo shell commands to migrate manually.");

			return;
		}

		MigrationStrategy strategy;

		try { strategy = MigrationStrategy.valueOf(_migrationStrategy); }
		catch (IllegalArgumentException e) {
			strategy = MigrationStrategy.DRY_RUN;
		}

		registry.registerInitialUpgradeSteps(
			new KeyManagerUpgradeProcess(_migrationService, _verificationService, _reportService, strategy, _targetProviderId));
	}

	@Activate
	protected void activate(KeyManagerUpgradeStepRegistratorConfiguration configuration) {
		_migrationStrategy = configuration.migrationStrategy();
		_targetProviderId = configuration.targetProviderId();
		_autoMigrate = configuration.autoMigrate();
	}

	@Reference
	private SecretMigrationService _migrationService;

	@Reference
	private MigrationVerificationService _verificationService;

	@Reference
	private MigrationReportService _reportService;

	private String _migrationStrategy;
	private String _targetProviderId;
	private boolean _autoMigrate;

	private static final Log _log = LogFactoryUtil.getLog(KeyManagerUpgradeStepRegistrator.class);

}
