package com.liferay.keymanager.upgrade.verification;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.exception.KeyResolutionException;
import com.liferay.keymanager.upgrade.migration.MigrationPlan;
import com.liferay.keymanager.upgrade.migration.MigrationPlan.MigrationEntry;
import com.liferay.keymanager.upgrade.migration.MigrationResult;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = MigrationVerificationService.class)
public class MigrationVerificationService {

	public VerificationResult verify(MigrationResult migrationResult) {
		MigrationPlan plan = migrationResult.getPlan();

		List<VerificationResult.VerificationEntry> entries = new ArrayList<>();

		int passCount = 0;
		int failCount = 0;

		for (MigrationEntry entry : plan.getEntries()) {
			String expectedValue = entry.getSecret().getCurrentValue();
			String reference = entry.getTargetReference();

			try {
				String resolved = _resolverService.resolve(reference);
				boolean matches = expectedValue.equals(resolved);

				entries.add(new VerificationResult.VerificationEntry(
					entry.getSecret().getPropertyKey(), reference, matches,
					matches ? null : "Resolved value does not match original"));

				if (matches) { passCount++; } else { failCount++; }
			}
			catch (KeyResolutionException e) {
				entries.add(new VerificationResult.VerificationEntry(
					entry.getSecret().getPropertyKey(), reference, false, "Resolution failed: " + e.getMessage()));

				failCount++;
			}
		}

		VerificationResult.Status status;

		if (failCount == 0) { status = VerificationResult.Status.ALL_PASSED; }
		else if (passCount > 0) { status = VerificationResult.Status.PARTIAL_PASS; }
		else { status = VerificationResult.Status.ALL_FAILED; }

		return new VerificationResult(status, entries, passCount, failCount);
	}

	@Reference
	private KeyResolverService _resolverService;

	private static final Log _log = LogFactoryUtil.getLog(MigrationVerificationService.class);

}
