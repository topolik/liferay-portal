package com.liferay.keymanager.upgrade.backup;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class BackupManifest implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _timestamp;
	private final String _backupDirectory;
	private final Map<String, String> _entries = new LinkedHashMap<>();

	public BackupManifest(String timestamp, String backupDirectory) {
		_timestamp = timestamp;
		_backupDirectory = backupDirectory;
	}

	public String getTimestamp() { return _timestamp; }
	public String getBackupDirectory() { return _backupDirectory; }
	public Map<String, String> getEntries() { return _entries; }

	public void addEntry(String originalLocation, String backupLocation) {
		_entries.put(originalLocation, backupLocation);
	}

}
