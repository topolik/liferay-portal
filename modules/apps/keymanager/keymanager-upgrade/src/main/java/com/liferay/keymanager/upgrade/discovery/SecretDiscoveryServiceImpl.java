package com.liferay.keymanager.upgrade.discovery;

import com.liferay.keymanager.upgrade.discovery.DiscoveredSecret.Sensitivity;
import com.liferay.keymanager.upgrade.discovery.scanner.SecretScanner;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true, service = SecretDiscoveryService.class)
public class SecretDiscoveryServiceImpl implements SecretDiscoveryService {

	@Override
	public List<DiscoveredSecret> discoverAll() {
		List<DiscoveredSecret> allDiscovered = new ArrayList<>();

		List<SecretScanner> sortedScanners = _scanners.stream()
			.sorted(Comparator.comparingInt(SecretScanner::getPriority))
			.collect(Collectors.toList());

		for (SecretScanner scanner : sortedScanners) {
			try {
				allDiscovered.addAll(scanner.scan());
			}
			catch (Exception e) {
				_log.error("Scanner '" + scanner.getName() + "' failed", e);
			}
		}

		Map<String, DiscoveredSecret> deduped = new LinkedHashMap<>();

		for (DiscoveredSecret secret : allDiscovered) {
			String key = secret.getPropertyKey();
			DiscoveredSecret existing = deduped.get(key);

			if (existing == null || secret.getSensitivity().ordinal() < existing.getSensitivity().ordinal()) {
				deduped.put(key, secret);
			}
		}

		List<DiscoveredSecret> result = new ArrayList<>(deduped.values());

		result.sort(Comparator.comparing(DiscoveredSecret::getSensitivity).thenComparing(DiscoveredSecret::getPropertyKey));

		return result;
	}

	@Override
	public List<DiscoveredSecret> discoverBySource(DiscoveredSecret.Source source) {
		return discoverAll().stream().filter(s -> s.getSource() == source).collect(Collectors.toList());
	}

	@Override
	public List<DiscoveredSecret> discoverBySensitivity(Sensitivity minSensitivity) {
		return discoverAll().stream().filter(s -> s.getSensitivity().ordinal() <= minSensitivity.ordinal()).collect(Collectors.toList());
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "_removeScanner")
	private void _addScanner(SecretScanner scanner) {
		_scanners.add(scanner);
	}

	private void _removeScanner(SecretScanner scanner) {
		_scanners.remove(scanner);
	}

	private final List<SecretScanner> _scanners = Collections.synchronizedList(new ArrayList<>());

	private static final Log _log = LogFactoryUtil.getLog(SecretDiscoveryServiceImpl.class);

}
