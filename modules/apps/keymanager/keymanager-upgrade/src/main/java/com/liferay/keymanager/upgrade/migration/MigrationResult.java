package com.liferay.keymanager.upgrade.migration;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigrationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status { SUCCESS, PARTIAL_SUCCESS, FAILED, DRY_RUN }

	private final Status _status;
	private final MigrationPlan _plan;
	private final int _successCount;
	private final int _failureCount;
	private final int _skippedCount;
	private final List<MigrationError> _errors;
	private final Instant _startedAt;
	private final Instant _completedAt;
	private final String _backupLocation;

	private MigrationResult(Builder builder) {
		_status = builder._status;
		_plan = builder._plan;
		_successCount = builder._successCount;
		_failureCount = builder._failureCount;
		_skippedCount = builder._skippedCount;
		_errors = Collections.unmodifiableList(builder._errors);
		_startedAt = builder._startedAt;
		_completedAt = builder._completedAt;
		_backupLocation = builder._backupLocation;
	}

	public Status getStatus() { return _status; }
	public MigrationPlan getPlan() { return _plan; }
	public int getSuccessCount() { return _successCount; }
	public int getFailureCount() { return _failureCount; }
	public int getSkippedCount() { return _skippedCount; }
	public List<MigrationError> getErrors() { return _errors; }
	public Instant getStartedAt() { return _startedAt; }
	public Instant getCompletedAt() { return _completedAt; }
	public String getBackupLocation() { return _backupLocation; }
	public Duration getDuration() { return Duration.between(_startedAt, _completedAt); }

	public static class MigrationError implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String _propertyKey;
		private final String _sourceLocation;
		private final String _errorMessage;
		private final String _stackTrace;

		public MigrationError(String propertyKey, String sourceLocation, String errorMessage, String stackTrace) {
			_propertyKey = propertyKey;
			_sourceLocation = sourceLocation;
			_errorMessage = errorMessage;
			_stackTrace = stackTrace;
		}

		public String getPropertyKey() { return _propertyKey; }
		public String getSourceLocation() { return _sourceLocation; }
		public String getErrorMessage() { return _errorMessage; }
		public String getStackTrace() { return _stackTrace; }

	}

	public static class Builder {

		private Status _status;
		private MigrationPlan _plan;
		private int _successCount;
		private int _failureCount;
		private int _skippedCount;
		private final List<MigrationError> _errors = new ArrayList<>();
		private Instant _startedAt;
		private Instant _completedAt;
		private String _backupLocation;

		public Builder status(Status status) { _status = status; return this; }
		public Builder plan(MigrationPlan plan) { _plan = plan; return this; }
		public Builder successCount(int count) { _successCount = count; return this; }
		public Builder failureCount(int count) { _failureCount = count; return this; }
		public Builder skippedCount(int count) { _skippedCount = count; return this; }
		public Builder addError(MigrationError error) { _errors.add(error); return this; }
		public Builder startedAt(Instant at) { _startedAt = at; return this; }
		public Builder completedAt(Instant at) { _completedAt = at; return this; }
		public Builder backupLocation(String location) { _backupLocation = location; return this; }

		public MigrationResult build() { return new MigrationResult(this); }

	}

}
