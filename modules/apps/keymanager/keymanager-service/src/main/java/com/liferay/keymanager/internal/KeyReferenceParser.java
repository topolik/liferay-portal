package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyResolutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

@Component(service = KeyReferenceParser.class)
public class KeyReferenceParser {

	private static final Pattern _KEY_REF_PATTERN = Pattern.compile(
		"\\$\\{keyref:([a-zA-Z0-9\\-_]+)/(.+?)\\}");

	public boolean isKeyReference(String value) {
		if (value == null) {
			return false;
		}

		return value.contains(KeyManagerConstants.KEY_REFERENCE_PREFIX);
	}

	public KeyReference parse(String referenceString) throws KeyResolutionException {
		if (referenceString == null || referenceString.isEmpty()) {
			throw new KeyResolutionException("Reference string must not be null or empty");
		}

		Matcher matcher = _KEY_REF_PATTERN.matcher(referenceString);

		if (!matcher.find()) {
			throw new KeyResolutionException(
				"Invalid key reference format: " + referenceString +
				". Expected format: ${keyref:provider/alias}");
		}

		return new KeyReference(matcher.group(1), matcher.group(2), matcher.group(0));
	}

	public List<KeyReference> parseAll(String value) throws KeyResolutionException {
		List<KeyReference> references = new ArrayList<>();

		if (value == null) {
			return references;
		}

		Matcher matcher = _KEY_REF_PATTERN.matcher(value);

		while (matcher.find()) {
			references.add(new KeyReference(matcher.group(1), matcher.group(2), matcher.group(0)));
		}

		return references;
	}

	public String replaceAll(String value, ThrowingFunction<KeyReference, String> resolver)
		throws KeyResolutionException {

		if (value == null || !isKeyReference(value)) {
			return value;
		}

		Matcher matcher = _KEY_REF_PATTERN.matcher(value);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			KeyReference ref = new KeyReference(matcher.group(1), matcher.group(2), matcher.group(0));

			String resolved = resolver.apply(ref);

			matcher.appendReplacement(sb, Matcher.quoteReplacement(resolved));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	@FunctionalInterface
	public interface ThrowingFunction<T, R> {

		R apply(T t) throws KeyResolutionException;

	}

}
