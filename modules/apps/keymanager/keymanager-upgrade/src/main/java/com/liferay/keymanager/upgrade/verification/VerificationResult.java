package com.liferay.keymanager.upgrade.verification;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class VerificationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status { ALL_PASSED, PARTIAL_PASS, ALL_FAILED }

	private final Status _status;
	private final List<VerificationEntry> _entries;
	private final int _passCount;
	private final int _failCount;

	public VerificationResult(Status status, List<VerificationEntry> entries, int passCount, int failCount) {
		_status = status;
		_entries = Collections.unmodifiableList(entries);
		_passCount = passCount;
		_failCount = failCount;
	}

	public Status getStatus() { return _status; }
	public List<VerificationEntry> getEntries() { return _entries; }
	public int getPassCount() { return _passCount; }
	public int getFailCount() { return _failCount; }

	public static class VerificationEntry implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String _propertyKey;
		private final String _reference;
		private final boolean _passed;
		private final String _failureReason;

		public VerificationEntry(String propertyKey, String reference, boolean passed, String failureReason) {
			_propertyKey = propertyKey;
			_reference = reference;
			_passed = passed;
			_failureReason = failureReason;
		}

		public String getPropertyKey() { return _propertyKey; }
		public String getReference() { return _reference; }
		public boolean isPassed() { return _passed; }
		public String getFailureReason() { return _failureReason; }

	}

}
