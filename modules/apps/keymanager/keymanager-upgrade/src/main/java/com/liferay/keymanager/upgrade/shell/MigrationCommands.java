package com.liferay.keymanager.upgrade.shell;

import com.liferay.keymanager.upgrade.backup.BackupManifest;
import com.liferay.keymanager.upgrade.backup.BackupService;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.keymanager.upgrade.discovery.SecretDiscoveryService;
import com.liferay.keymanager.upgrade.migration.MigrationPlan;
import com.liferay.keymanager.upgrade.migration.MigrationResult;
import com.liferay.keymanager.upgrade.migration.MigrationStrategy;
import com.liferay.keymanager.upgrade.migration.SecretMigrationService;
import com.liferay.keymanager.upgrade.report.MigrationReport;
import com.liferay.keymanager.upgrade.report.MigrationReportService;
import com.liferay.keymanager.upgrade.verification.MigrationVerificationService;
import com.liferay.keymanager.upgrade.verification.VerificationResult;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"osgi.command.scope=keymigrate",
		"osgi.command.function=discover",
		"osgi.command.function=plan",
		"osgi.command.function=dryrun",
		"osgi.command.function=execute",
		"osgi.command.function=verify",
		"osgi.command.function=backups",
		"osgi.command.function=restore"
	},
	service = MigrationCommands.class
)
public class MigrationCommands {

	public void discover() {
		System.out.println("Scanning for plaintext secrets...\n");

		List<DiscoveredSecret> secrets = _discoveryService.discoverAll();

		System.out.printf("%-55s %-12s %-20s %-10s%n", "Property Key", "Sensitivity", "Source", "Already Ref?");
		System.out.println("-".repeat(100));

		for (DiscoveredSecret secret : secrets) {
			System.out.printf("%-55s %-12s %-20s %-10s%n",
				_truncate(secret.getPropertyKey(), 55), secret.getSensitivity(),
				secret.getSource(), secret.isAlreadyReference() ? "YES" : "no");
		}

		System.out.println("\nTotal: " + secrets.size() + " potential secrets found.");
	}

	public void plan(String targetProviderId, String strategyName) {
		MigrationStrategy strategy;

		try { strategy = MigrationStrategy.valueOf(strategyName); }
		catch (IllegalArgumentException e) {
			System.err.println("Invalid strategy: " + strategyName);

			return;
		}

		MigrationPlan migrationPlan = _migrationService.createPlan(strategy, targetProviderId);

		System.out.println("Plan: " + migrationPlan.getTotalCount() + " to migrate, " + migrationPlan.getSkippedCount() + " skipped");

		for (MigrationPlan.MigrationEntry entry : migrationPlan.getEntries()) {
			System.out.printf("  %s -> %s%n", entry.getSecret().getPropertyKey(), entry.getTargetReference());
		}
	}

	public void dryrun(String targetProviderId) {
		plan(targetProviderId, "DRY_RUN");
	}

	public void execute(String targetProviderId, String strategyName) {
		MigrationStrategy strategy;

		try { strategy = MigrationStrategy.valueOf(strategyName); }
		catch (IllegalArgumentException e) {
			System.err.println("Invalid strategy: " + strategyName);

			return;
		}

		System.out.println("Creating migration plan...");

		MigrationPlan migrationPlan = _migrationService.createPlan(strategy, targetProviderId);

		System.out.println("Executing migration (" + migrationPlan.getTotalCount() + " secrets)...");

		MigrationResult result = _migrationService.executePlan(migrationPlan);

		System.out.println("Status: " + result.getStatus() + " (success=" + result.getSuccessCount() + ", failed=" + result.getFailureCount() + ")");

		VerificationResult verification = null;

		if (result.getSuccessCount() > 0) {
			System.out.println("Verifying...");

			verification = _verificationService.verify(result);

			System.out.println("Verification: " + verification.getStatus());
		}

		MigrationReport report = _reportService.generateReport(migrationPlan, result, verification);

		try { _reportService.saveReport(report); } catch (Exception e) { System.err.println("Could not save report"); }

		System.out.println("\n" + report.getContent());
	}

	public void verify() {
		System.out.println("Re-verification requires a previous migration result. Use 'execute' for full verification.");
	}

	public void backups() {
		try {
			List<BackupManifest> manifests = _backupService.listBackups();

			if (manifests.isEmpty()) {
				System.out.println("No backups found.");

				return;
			}

			for (BackupManifest manifest : manifests) {
				System.out.println("  " + manifest.getTimestamp() + " -> " + manifest.getBackupDirectory());
			}
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void restore(String timestamp) {
		try {
			List<BackupManifest> manifests = _backupService.listBackups();

			BackupManifest target = manifests.stream().filter(m -> m.getTimestamp().equals(timestamp)).findFirst().orElse(null);

			if (target == null) {
				System.err.println("No backup found with timestamp: " + timestamp);

				return;
			}

			_backupService.restore(target);

			System.out.println("Restore complete. Restart Liferay for changes to take effect.");
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private String _truncate(String value, int maxLength) {
		if (value == null) return "";
		if (value.length() <= maxLength) return value;

		return value.substring(0, maxLength - 3) + "...";
	}

	@Reference
	private SecretDiscoveryService _discoveryService;

	@Reference
	private SecretMigrationService _migrationService;

	@Reference
	private MigrationVerificationService _verificationService;

	@Reference
	private MigrationReportService _reportService;

	@Reference
	private BackupService _backupService;

}
