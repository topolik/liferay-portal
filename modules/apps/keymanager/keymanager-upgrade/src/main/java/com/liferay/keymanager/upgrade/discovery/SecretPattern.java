package com.liferay.keymanager.upgrade.discovery;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Sensitivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SecretPattern {

	public static final Set<String> KNOWN_SECRET_PROPERTIES = Set.of(
		"mail.session.mail.smtp.password", "mail.session.mail.pop3.password",
		"jdbc.default.password", "ldap.security.credentials",
		"dl.store.s3.secret.key", "dl.store.s3.access.key",
		"captcha.engine.recaptcha.key.private", "captcha.engine.recaptcha.key.site",
		"amazon.access.key.id", "amazon.secret.access.key",
		"tunneling.servlet.shared.secret", "auth.token.shared.secret"
	);

	public static final List<PropertyNamePattern> NAME_PATTERNS;

	static {
		List<PropertyNamePattern> patterns = new ArrayList<>();

		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*password.*"), Sensitivity.HIGH, "Contains 'password'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*\\.secret\\.?.*"), Sensitivity.HIGH, "Contains 'secret'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*private[._-]?key.*"), Sensitivity.HIGH, "Contains 'private key'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*credentials?.*"), Sensitivity.HIGH, "Contains 'credential'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*api[._-]?key.*"), Sensitivity.MEDIUM, "Contains 'api key'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*access[._-]?key.*"), Sensitivity.MEDIUM, "Contains 'access key'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*client[._-]?secret.*"), Sensitivity.MEDIUM, "Contains 'client secret'"));
		patterns.add(new PropertyNamePattern(Pattern.compile("(?i).*token.*"), Sensitivity.LOW, "Contains 'token'"));

		NAME_PATTERNS = Collections.unmodifiableList(patterns);
	}

	public static final Set<String> SKIP_VALUES = Set.of("", "null", "none", "changeit", "password", "TODO", "CHANGEME");

	public static final int MIN_SECRET_LENGTH = 4;

	public static class PropertyNamePattern {

		private final Pattern _pattern;
		private final Sensitivity _sensitivity;
		private final String _reason;

		public PropertyNamePattern(Pattern pattern, Sensitivity sensitivity, String reason) {
			_pattern = pattern;
			_sensitivity = sensitivity;
			_reason = reason;
		}

		public Pattern getPattern() { return _pattern; }
		public Sensitivity getSensitivity() { return _sensitivity; }
		public String getReason() { return _reason; }

	}

	private SecretPattern() {
	}

}
