package com.liferay.keymanager.upgrade;

import com.liferay.keymanager.upgrade.migration.MigrationPlan;
import com.liferay.keymanager.upgrade.migration.MigrationResult;
import com.liferay.keymanager.upgrade.migration.MigrationStrategy;
import com.liferay.keymanager.upgrade.migration.SecretMigrationService;
import com.liferay.keymanager.upgrade.report.MigrationReport;
import com.liferay.keymanager.upgrade.report.MigrationReportService;
import com.liferay.keymanager.upgrade.verification.MigrationVerificationService;
import com.liferay.keymanager.upgrade.verification.VerificationResult;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

public class KeyManagerUpgradeProcess extends UpgradeProcess {

	private final SecretMigrationService _migrationService;
	private final MigrationVerificationService _verificationService;
	private final MigrationReportService _reportService;
	private final MigrationStrategy _strategy;
	private final String _targetProviderId;

	public KeyManagerUpgradeProcess(
		SecretMigrationService migrationService, MigrationVerificationService verificationService,
		MigrationReportService reportService, MigrationStrategy strategy, String targetProviderId) {

		_migrationService = migrationService;
		_verificationService = verificationService;
		_reportService = reportService;
		_strategy = strategy;
		_targetProviderId = targetProviderId;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_log.info("Starting Key Manager Secret Migration...");

		MigrationPlan plan = _migrationService.createPlan(_strategy, _targetProviderId);

		if (plan.getTotalCount() == 0) {
			_log.info("No secrets found to migrate.");

			return;
		}

		MigrationResult result = _migrationService.executePlan(plan);

		VerificationResult verification = null;

		if (result.getStatus() != MigrationResult.Status.FAILED && result.getStatus() != MigrationResult.Status.DRY_RUN) {
			verification = _verificationService.verify(result);
		}

		MigrationReport report = _reportService.generateReport(plan, result, verification);

		_reportService.saveReport(report);

		_log.info("\n" + report.getContent());

		if (result.getStatus() == MigrationResult.Status.FAILED) {
			throw new Exception("Key migration failed. Backup at: " + result.getBackupLocation());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(KeyManagerUpgradeProcess.class);

}
