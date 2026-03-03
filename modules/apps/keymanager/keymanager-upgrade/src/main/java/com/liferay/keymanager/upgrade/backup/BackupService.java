package com.liferay.keymanager.upgrade.backup;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = BackupService.class)
public class BackupService {

	private static final DateTimeFormatter _TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	public BackupManifest createBackup(List<DiscoveredSecret> secrets) throws IOException {
		String timestamp = LocalDateTime.now().format(_TIMESTAMP_FORMAT);

		Path backupDir = _getBackupDirectory(timestamp);

		Files.createDirectories(backupDir);

		BackupManifest manifest = new BackupManifest(timestamp, backupDir.toString());

		Set<String> backedUpFiles = new HashSet<>();

		for (DiscoveredSecret secret : secrets) {
			String location = secret.getSourceLocation();

			if (backedUpFiles.contains(location)) {
				continue;
			}

			if (location.startsWith("runtime:") || location.startsWith("ConfigurationAdmin:") || location.startsWith("Configuration_:")) {
				Path dumpFile = backupDir.resolve(_sanitizeFileName(location) + ".backup.txt");

				Files.writeString(dumpFile, "# Backup of " + location + "\n" + secret.getPropertyKey() + "=" + secret.getCurrentValue() + "\n");

				manifest.addEntry(location, dumpFile.toString());
			}
			else {
				Path sourceFile = Paths.get(location);

				if (Files.exists(sourceFile)) {
					Path backupFile = backupDir.resolve(sourceFile.getFileName().toString() + ".backup");

					Files.copy(sourceFile, backupFile, StandardCopyOption.REPLACE_EXISTING);

					manifest.addEntry(location, backupFile.toString());
				}
			}

			backedUpFiles.add(location);
		}

		Path manifestFile = backupDir.resolve("MANIFEST.txt");

		StringBuilder sb = new StringBuilder();

		sb.append("# Key Manager Migration Backup Manifest\n");
		sb.append("timestamp=" + manifest.getTimestamp() + "\n");
		sb.append("backupDir=" + manifest.getBackupDirectory() + "\n\n");

		for (java.util.Map.Entry<String, String> entry : manifest.getEntries().entrySet()) {
			sb.append(entry.getKey() + " -> " + entry.getValue() + "\n");
		}

		Files.writeString(manifestFile, sb.toString());

		return manifest;
	}

	public void restore(BackupManifest manifest) throws IOException {
		for (java.util.Map.Entry<String, String> entry : manifest.getEntries().entrySet()) {
			String originalLocation = entry.getKey();
			String backupLocation = entry.getValue();

			if (originalLocation.startsWith("runtime:") || originalLocation.startsWith("ConfigurationAdmin:")) {
				_log.warn("Cannot auto-restore: " + originalLocation);

				continue;
			}

			Path backupFile = Paths.get(backupLocation);
			Path originalFile = Paths.get(originalLocation);

			if (Files.exists(backupFile)) {
				Files.copy(backupFile, originalFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	public List<BackupManifest> listBackups() throws IOException {
		Path backupsRoot = _getBackupsRoot();

		if (!Files.exists(backupsRoot)) {
			return List.of();
		}

		List<BackupManifest> manifests = new ArrayList<>();

		try (Stream<Path> dirs = Files.list(backupsRoot)) {
			dirs.filter(Files::isDirectory).forEach(dir -> {
				Path manifestFile = dir.resolve("MANIFEST.txt");

				if (Files.exists(manifestFile)) {
					manifests.add(new BackupManifest(dir.getFileName().toString().replace("keymanager-backup-", ""), dir.toString()));
				}
			});
		}

		return manifests;
	}

	private Path _getBackupDirectory(String timestamp) {
		return _getBackupsRoot().resolve("keymanager-backup-" + timestamp);
	}

	private Path _getBackupsRoot() {
		String liferayHome = System.getProperty("liferay.home", "/opt/liferay");

		return Paths.get(liferayHome, "data", "keymanager-backups");
	}

	private String _sanitizeFileName(String location) {
		return location.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_");
	}

	private static final Log _log = LogFactoryUtil.getLog(BackupService.class);

}
