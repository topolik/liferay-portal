package com.liferay.keymanager.upgrade.migration.writer;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Source;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = ConfigurationWriter.class)
public class PortalPropertiesWriter implements ConfigurationWriter {

	@Override
	public Source getSourceType() {
		return Source.PORTAL_PROPERTIES;
	}

	@Override
	public boolean replaceValue(DiscoveredSecret secret, String keyReference) throws Exception {
		String sourceLocation = secret.getSourceLocation();

		if (sourceLocation.startsWith("runtime:")) {
			PropsUtil.set(secret.getPropertyKey(), keyReference);

			return true;
		}

		Path filePath = Paths.get(sourceLocation);

		if (!Files.exists(filePath)) {
			return false;
		}

		List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		List<String> updatedLines = new ArrayList<>();

		boolean found = false;
		String propertyKey = secret.getPropertyKey();

		for (String line : lines) {
			String trimmed = line.trim();

			if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
				updatedLines.add(line);

				continue;
			}

			if (_isPropertyLine(trimmed, propertyKey)) {
				int insertIndex = updatedLines.size();

				updatedLines.add("# [Key Manager] Original value migrated to secure storage on " + java.time.LocalDateTime.now());

				String updatedLine = _replacePropertyValue(line, propertyKey, keyReference);

				updatedLines.add(updatedLine);

				found = true;
			}
			else {
				updatedLines.add(line);
			}
		}

		if (found) {
			Files.write(filePath, updatedLines, StandardCharsets.UTF_8);

			PropsUtil.set(propertyKey, keyReference);
		}

		return found;
	}

	private boolean _isPropertyLine(String line, String propertyKey) {
		String escapedKey = Pattern.quote(propertyKey);

		return line.matches("^" + escapedKey + "\\s*[=:].*");
	}

	private String _replacePropertyValue(String line, String propertyKey, String newValue) {
		String escapedKey = Pattern.quote(propertyKey);

		Pattern pattern = Pattern.compile("^(" + escapedKey + "\\s*[=:]\\s*)(.*)$");

		Matcher matcher = pattern.matcher(line);

		if (matcher.matches()) {
			return matcher.group(1) + newValue;
		}

		return propertyKey + "=" + newValue;
	}

	private static final Log _log = LogFactoryUtil.getLog(PortalPropertiesWriter.class);

}
