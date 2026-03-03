package com.liferay.keymanager.upgrade.migration;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigrationPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	private final MigrationStrategy _strategy;
	private final String _targetProviderId;
	private final List<MigrationEntry> _entries;
	private final List<DiscoveredSecret> _skipped;

	private MigrationPlan(Builder builder) {
		_strategy = builder._strategy;
		_targetProviderId = builder._targetProviderId;
		_entries = Collections.unmodifiableList(builder._entries);
		_skipped = Collections.unmodifiableList(builder._skipped);
	}

	public MigrationStrategy getStrategy() { return _strategy; }
	public String getTargetProviderId() { return _targetProviderId; }
	public List<MigrationEntry> getEntries() { return _entries; }
	public List<DiscoveredSecret> getSkipped() { return _skipped; }
	public int getTotalCount() { return _entries.size(); }
	public int getSkippedCount() { return _skipped.size(); }

	public static class MigrationEntry implements Serializable {

		private static final long serialVersionUID = 1L;

		private final DiscoveredSecret _secret;
		private final String _targetAlias;
		private final String _targetReference;

		public MigrationEntry(DiscoveredSecret secret, String targetAlias, String targetReference) {
			_secret = secret;
			_targetAlias = targetAlias;
			_targetReference = targetReference;
		}

		public DiscoveredSecret getSecret() { return _secret; }
		public String getTargetAlias() { return _targetAlias; }
		public String getTargetReference() { return _targetReference; }

	}

	public static class Builder {

		private MigrationStrategy _strategy;
		private String _targetProviderId;
		private final List<MigrationEntry> _entries = new ArrayList<>();
		private final List<DiscoveredSecret> _skipped = new ArrayList<>();

		public Builder strategy(MigrationStrategy strategy) { _strategy = strategy; return this; }
		public Builder targetProviderId(String providerId) { _targetProviderId = providerId; return this; }
		public Builder addEntry(MigrationEntry entry) { _entries.add(entry); return this; }
		public Builder addSkipped(DiscoveredSecret secret) { _skipped.add(secret); return this; }

		public MigrationPlan build() { return new MigrationPlan(this); }

	}

}
