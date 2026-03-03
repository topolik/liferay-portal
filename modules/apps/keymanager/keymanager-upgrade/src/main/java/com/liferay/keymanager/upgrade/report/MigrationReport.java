package com.liferay.keymanager.upgrade.report;

import com.liferay.keymanager.upgrade.migration.MigrationPlan;
import com.liferay.keymanager.upgrade.migration.MigrationResult;
import com.liferay.keymanager.upgrade.verification.VerificationResult;

import java.io.Serializable;

public class MigrationReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _content;
	private final MigrationPlan _plan;
	private final MigrationResult _result;
	private final VerificationResult _verification;

	public MigrationReport(String content, MigrationPlan plan, MigrationResult result, VerificationResult verification) {
		_content = content;
		_plan = plan;
		_result = result;
		_verification = verification;
	}

	public String getContent() { return _content; }
	public MigrationPlan getPlan() { return _plan; }
	public MigrationResult getResult() { return _result; }
	public VerificationResult getVerification() { return _verification; }

}
