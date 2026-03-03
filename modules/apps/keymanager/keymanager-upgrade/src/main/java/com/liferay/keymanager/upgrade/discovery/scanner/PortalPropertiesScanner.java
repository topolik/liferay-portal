package com.liferay.keymanager.upgrade.discovery.scanner;

import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Sensitivity;
import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Source;
import com.liferay.keymanager.upgrade.discovery.SecretPattern;
import com.liferay.keymanager.upgrade.discovery.SecretPattern.PropertyNamePattern;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = SecretScanner.class, property = "scanner.priority:Integer=10")
public class PortalPropertiesScanner implements SecretScanner {

	@Override
	public Source getSourceType() {
		return Source.PORTAL_PROPERTIES;
	}

	@Override
	public String getName() {
		return "Portal Properties Scanner";
	}

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public List<DiscoveredSecret> scan() throws Exception {
		List<DiscoveredSecret> discovered = new ArrayList<>();

		String liferayHome = System.getProperty("liferay.home", System.getenv().getOrDefault("LIFERAY_HOME", "/opt/liferay"));

		Path portalExtPath = Paths.get(liferayHome, "portal-ext.properties");

		if (Files.exists(portalExtPath)) {
			_scanPropertiesFile(portalExtPath, discovered);
		}

		_scanRuntimeProperties(discovered);

		if (_log.isInfoEnabled()) {
			_log.info("Portal properties scan complete. Found " + discovered.size() + " potential secrets.");
		}

		return discovered;
	}

	private void _scanPropertiesFile(Path filePath, List<DiscoveredSecret> discovered) throws Exception {
		Properties properties = new Properties();

		try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
			properties.load(fis);
		}

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);

			if (value == null || value.trim().length() < SecretPattern.MIN_SECRET_LENGTH) {
				continue;
			}

			if (SecretPattern.SKIP_VALUES.contains(value.trim().toLowerCase())) {
				continue;
			}

			boolean alreadyReference = value.contains(KeyManagerConstants.KEY_REFERENCE_PREFIX);

			if (SecretPattern.KNOWN_SECRET_PROPERTIES.contains(key)) {
				discovered.add(new DiscoveredSecret.Builder()
					.propertyKey(key).currentValue(value).source(Source.PORTAL_PROPERTIES)
					.sourceLocation(filePath.toString()).sensitivity(Sensitivity.HIGH)
					.detectionReason("Known Liferay secret property")
					.alreadyReference(alreadyReference).build());

				continue;
			}

			for (PropertyNamePattern pattern : SecretPattern.NAME_PATTERNS) {
				if (pattern.getPattern().matcher(key).matches()) {
					discovered.add(new DiscoveredSecret.Builder()
						.propertyKey(key).currentValue(value).source(Source.PORTAL_PROPERTIES)
						.sourceLocation(filePath.toString()).sensitivity(pattern.getSensitivity())
						.detectionReason(pattern.getReason())
						.alreadyReference(alreadyReference).build());

					break;
				}
			}
		}
	}

	private void _scanRuntimeProperties(List<DiscoveredSecret> discovered) {
		Properties properties = PropsUtil.getProperties();

		for (String key : SecretPattern.KNOWN_SECRET_PROPERTIES) {
			String value = properties.getProperty(key);

			if (value == null || value.trim().length() < SecretPattern.MIN_SECRET_LENGTH) {
				continue;
			}

			boolean alreadyDiscovered = discovered.stream().anyMatch(d -> d.getPropertyKey().equals(key));

			if (!alreadyDiscovered) {
				discovered.add(new DiscoveredSecret.Builder()
					.propertyKey(key).currentValue(value).source(Source.PORTAL_PROPERTIES)
					.sourceLocation("runtime:PropsUtil").sensitivity(Sensitivity.HIGH)
					.detectionReason("Known secret property (runtime)")
					.alreadyReference(value.contains(KeyManagerConstants.KEY_REFERENCE_PREFIX)).build());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(PortalPropertiesScanner.class);

}
