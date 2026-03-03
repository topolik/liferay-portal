package com.liferay.keymanager.upgrade.migration;

import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.KeyProviderRegistry;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.keymanager.upgrade.backup.BackupManifest;
import com.liferay.keymanager.upgrade.backup.BackupService;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Sensitivity;
import com.liferay.keymanager.upgrade.discovery.SecretDiscoveryService;
import com.liferay.keymanager.upgrade.migration.MigrationPlan.MigrationEntry;
import com.liferay.keymanager.upgrade.migration.MigrationResult.MigrationError;
import com.liferay.keymanager.upgrade.migration.writer.ConfigurationWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true, service = SecretMigrationService.class)
public class SecretMigrationServiceImpl implements SecretMigrationService {

	@Override
	public MigrationPlan createPlan(MigrationStrategy strategy, String targetProviderId) {
		List<DiscoveredSecret> allSecrets = _discoveryService.discoverAll();

		MigrationPlan.Builder planBuilder = new MigrationPlan.Builder()
			.strategy(strategy).targetProviderId(targetProviderId);

		for (DiscoveredSecret secret : allSecrets) {
			if (secret.isAlreadyReference()) {
				planBuilder.addSkipped(secret);

				continue;
			}

			if (!_matchesSensitivity(secret.getSensitivity(), strategy)) {
				planBuilder.addSkipped(secret);

				continue;
			}

			String alias = secret.getSuggestedAlias();
			String reference = _resolverService.createReference(targetProviderId, alias);

			planBuilder.addEntry(new MigrationEntry(secret, alias, reference));
		}

		return planBuilder.build();
	}

	@Override
	public MigrationResult executePlan(MigrationPlan plan) {
		Instant startedAt = Instant.now();

		MigrationResult.Builder resultBuilder = new MigrationResult.Builder().plan(plan).startedAt(startedAt);

		if (plan.getStrategy() == MigrationStrategy.DRY_RUN) {
			return resultBuilder.status(MigrationResult.Status.DRY_RUN)
				.successCount(0).failureCount(0).skippedCount(plan.getSkippedCount())
				.completedAt(Instant.now()).build();
		}

		// Backup
		try {
			List<DiscoveredSecret> toBackup = new ArrayList<>();

			for (MigrationEntry entry : plan.getEntries()) {
				toBackup.add(entry.getSecret());
			}

			BackupManifest backup = _backupService.createBackup(toBackup);

			resultBuilder.backupLocation(backup.getBackupDirectory());
		}
		catch (Exception e) {
			return resultBuilder.status(MigrationResult.Status.FAILED)
				.failureCount(plan.getTotalCount()).completedAt(Instant.now())
				.addError(new MigrationError("BACKUP", "N/A", "Backup failed: " + e.getMessage(), _getStackTrace(e)))
				.build();
		}

		// Get provider
		KeyProvider targetProvider;

		try {
			targetProvider = _registry.getProvider(plan.getTargetProviderId())
				.orElseThrow(() -> new KeyProviderException("Provider not found: " + plan.getTargetProviderId()));
		}
		catch (KeyProviderException e) {
			return resultBuilder.status(MigrationResult.Status.FAILED)
				.failureCount(plan.getTotalCount()).completedAt(Instant.now())
				.addError(new MigrationError("PROVIDER", plan.getTargetProviderId(), e.getMessage(), _getStackTrace(e)))
				.build();
		}

		Map<DiscoveredSecret.Source, ConfigurationWriter> writerMap = new HashMap<>();

		for (ConfigurationWriter writer : _writers) {
			writerMap.put(writer.getSourceType(), writer);
		}

		int successCount = 0;
		int failureCount = 0;

		for (MigrationEntry entry : plan.getEntries()) {
			DiscoveredSecret secret = entry.getSecret();

			try {
				char[] secretValue = secret.getCurrentValue().toCharArray();

				try {
					targetProvider.storeKey(entry.getTargetAlias(), secretValue);
				}
				finally {
					Arrays.fill(secretValue, '\0');
				}

				ConfigurationWriter writer = writerMap.get(secret.getSource());

				if (writer != null) {
					writer.replaceValue(secret, entry.getTargetReference());
				}

				successCount++;
			}
			catch (Exception e) {
				failureCount++;

				resultBuilder.addError(new MigrationError(
					secret.getPropertyKey(), secret.getSourceLocation(), e.getMessage(), _getStackTrace(e)));
			}
		}

		MigrationResult.Status status;

		if (failureCount == 0) {
			status = MigrationResult.Status.SUCCESS;
		}
		else if (successCount > 0) {
			status = MigrationResult.Status.PARTIAL_SUCCESS;
		}
		else {
			status = MigrationResult.Status.FAILED;
		}

		return resultBuilder.status(status).successCount(successCount).failureCount(failureCount)
			.skippedCount(plan.getSkippedCount()).completedAt(Instant.now()).build();
	}

	private boolean _matchesSensitivity(Sensitivity sensitivity, MigrationStrategy strategy) {
		switch (strategy) {
			case HIGH_SENSITIVITY_ONLY: return sensitivity == Sensitivity.HIGH;
			case HIGH_AND_MEDIUM_SENSITIVITY: return sensitivity == Sensitivity.HIGH || sensitivity == Sensitivity.MEDIUM;
			case ALL_SECRETS: case DRY_RUN: return true;
			default: return false;
		}
	}

	private String _getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));

		return sw.toString();
	}

	@Reference
	private SecretDiscoveryService _discoveryService;

	@Reference
	private KeyResolverService _resolverService;

	@Reference
	private KeyProviderRegistry _registry;

	@Reference
	private BackupService _backupService;

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "_removeWriter")
	private void _addWriter(ConfigurationWriter writer) { _writers.add(writer); }

	private void _removeWriter(ConfigurationWriter writer) { _writers.remove(writer); }

	private final List<ConfigurationWriter> _writers = Collections.synchronizedList(new ArrayList<>());

	private static final Log _log = LogFactoryUtil.getLog(SecretMigrationServiceImpl.class);

}
