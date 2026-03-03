package com.liferay.keymanager.upgrade.report;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.keymanager.upgrade.migration.MigrationPlan;
import com.liferay.keymanager.upgrade.migration.MigrationPlan.MigrationEntry;
import com.liferay.keymanager.upgrade.migration.MigrationResult;
import com.liferay.keymanager.upgrade.migration.MigrationResult.MigrationError;
import com.liferay.keymanager.upgrade.verification.VerificationResult;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = MigrationReportService.class)
public class MigrationReportService {

	public MigrationReport generateReport(MigrationPlan plan, MigrationResult result, VerificationResult verification) {
		StringBuilder report = new StringBuilder();

		report.append("KEY MANAGER - SECRET MIGRATION REPORT\n");
		report.append("Generated: " + LocalDateTime.now() + "\n\n");

		report.append("SUMMARY\n");
		report.append(String.format("  Strategy:    %s%n", plan.getStrategy()));
		report.append(String.format("  Provider:    %s%n", plan.getTargetProviderId()));
		report.append(String.format("  Status:      %s%n", result.getStatus()));
		report.append(String.format("  Duration:    %s%n", result.getDuration()));
		report.append(String.format("  Migrated:    %d%n", result.getSuccessCount()));
		report.append(String.format("  Failed:      %d%n", result.getFailureCount()));
		report.append(String.format("  Skipped:     %d%n", result.getSkippedCount()));
		report.append(String.format("  Backup:      %s%n%n", result.getBackupLocation()));

		report.append("MIGRATED SECRETS\n");

		for (MigrationEntry entry : plan.getEntries()) {
			report.append(String.format("  %s -> %s%n", entry.getSecret().getPropertyKey(), entry.getTargetReference()));
		}

		if (!result.getErrors().isEmpty()) {
			report.append("\nERRORS\n");

			for (MigrationError error : result.getErrors()) {
				report.append(String.format("  %s: %s%n", error.getPropertyKey(), error.getErrorMessage()));
			}
		}

		return new MigrationReport(report.toString(), plan, result, verification);
	}

	public Path saveReport(MigrationReport report) throws IOException {
		String liferayHome = System.getProperty("liferay.home", "/opt/liferay");
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

		Path reportPath = Paths.get(liferayHome, "data", "keymanager-backups", "migration-report-" + timestamp + ".txt");

		Files.createDirectories(reportPath.getParent());
		Files.writeString(reportPath, report.getContent());

		return reportPath;
	}

	private static final Log _log = LogFactoryUtil.getLog(MigrationReportService.class);

}
