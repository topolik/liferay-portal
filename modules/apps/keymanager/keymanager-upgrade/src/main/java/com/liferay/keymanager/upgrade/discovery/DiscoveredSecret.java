package com.liferay.keymanager.upgrade.discovery;

import java.io.Serializable;
import java.time.Instant;

public class DiscoveredSecret implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Source {
		PORTAL_PROPERTIES, OSGI_CONFIGURATION, DATABASE_CONFIGURATION, SYSTEM_SETTINGS, EXPANDO_FIELD
	}

	public enum Sensitivity {
		HIGH, MEDIUM, LOW
	}

	private final String _propertyKey;
	private final String _currentValue;
	private final Source _source;
	private final String _sourceLocation;
	private final Sensitivity _sensitivity;
	private final String _detectionReason;
	private final Instant _discoveredAt;
	private final boolean _alreadyReference;

	private DiscoveredSecret(Builder builder) {
		_propertyKey = builder._propertyKey;
		_currentValue = builder._currentValue;
		_source = builder._source;
		_sourceLocation = builder._sourceLocation;
		_sensitivity = builder._sensitivity;
		_detectionReason = builder._detectionReason;
		_discoveredAt = Instant.now();
		_alreadyReference = builder._alreadyReference;
	}

	public String getPropertyKey() { return _propertyKey; }
	public String getCurrentValue() { return _currentValue; }
	public Source getSource() { return _source; }
	public String getSourceLocation() { return _sourceLocation; }
	public Sensitivity getSensitivity() { return _sensitivity; }
	public String getDetectionReason() { return _detectionReason; }
	public Instant getDiscoveredAt() { return _discoveredAt; }
	public boolean isAlreadyReference() { return _alreadyReference; }

	public String getSuggestedAlias() {
		return _propertyKey.replaceAll("[^a-zA-Z0-9]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "").toLowerCase();
	}

	@Override
	public String toString() {
		return String.format("DiscoveredSecret{key='%s', source=%s, sensitivity=%s}", _propertyKey, _source, _sensitivity);
	}

	public static class Builder {

		private String _propertyKey;
		private String _currentValue;
		private Source _source;
		private String _sourceLocation;
		private Sensitivity _sensitivity = Sensitivity.MEDIUM;
		private String _detectionReason;
		private boolean _alreadyReference = false;

		public Builder propertyKey(String key) { _propertyKey = key; return this; }
		public Builder currentValue(String value) { _currentValue = value; return this; }
		public Builder source(Source source) { _source = source; return this; }
		public Builder sourceLocation(String location) { _sourceLocation = location; return this; }
		public Builder sensitivity(Sensitivity sensitivity) { _sensitivity = sensitivity; return this; }
		public Builder detectionReason(String reason) { _detectionReason = reason; return this; }
		public Builder alreadyReference(boolean alreadyReference) { _alreadyReference = alreadyReference; return this; }

		public DiscoveredSecret build() {
			if (_propertyKey == null || _source == null) {
				throw new IllegalStateException("propertyKey and source are required");
			}

			return new DiscoveredSecret(this);
		}

	}

}
