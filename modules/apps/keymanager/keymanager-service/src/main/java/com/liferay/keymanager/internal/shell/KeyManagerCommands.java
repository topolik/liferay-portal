package com.liferay.keymanager.internal.shell;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.KeyProviderRegistry;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.keymanager.exception.KeyResolutionException;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"osgi.command.scope=keymanager",
		"osgi.command.function=providers",
		"osgi.command.function=list",
		"osgi.command.function=store",
		"osgi.command.function=resolve",
		"osgi.command.function=delete",
		"osgi.command.function=ref",
		"osgi.command.function=invalidateCache"
	},
	service = KeyManagerCommands.class
)
public class KeyManagerCommands {

	public void providers() {
		List<KeyProvider> providers = _registry.getAllProviders();

		System.out.println("Registered Key Providers:");
		System.out.printf("%-15s %-30s %-10s %-10s%n", "ID", "Name", "Priority", "Available");
		System.out.println("-".repeat(70));

		for (KeyProvider provider : providers) {
			System.out.printf("%-15s %-30s %-10d %-10s%n",
				provider.getProviderId(), provider.getDisplayName(),
				provider.getPriority(), provider.isAvailable() ? "YES" : "NO");
		}
	}

	public void list(String providerId) {
		try {
			KeyProvider provider = _registry.getProvider(providerId)
				.orElseThrow(() -> new KeyProviderException("Provider not found: " + providerId));

			List<String> aliases = provider.listAliases();

			System.out.println("Keys in provider '" + providerId + "':");

			for (String alias : aliases) {
				KeyMetadata metadata = provider.getKeyMetadata(alias);

				System.out.println("  " + alias + " [type=" + metadata.getKeyType() + ", version=" + metadata.getVersion() + "]");
			}

			System.out.println("Total: " + aliases.size() + " keys");
		}
		catch (KeyProviderException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void store(String providerId, String alias, String value) {
		try {
			char[] valueChars = value.toCharArray();

			String reference = _resolverService.storeAndReference(providerId, alias, valueChars);

			Arrays.fill(valueChars, '\0');

			System.out.println("Key stored successfully.");
			System.out.println("Reference: " + reference);
		}
		catch (KeyResolutionException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void resolve(String reference) {
		try {
			String resolved = _resolverService.resolve(reference);

			System.out.println("Resolved successfully. Value length: " + resolved.length());
			System.out.println("First 3 chars: " + resolved.substring(0, Math.min(3, resolved.length())) + "***");
		}
		catch (KeyResolutionException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void delete(String providerId, String alias) {
		try {
			KeyProvider provider = _registry.getProvider(providerId)
				.orElseThrow(() -> new KeyProviderException("Provider not found: " + providerId));

			provider.deleteKey(alias);

			_resolverService.invalidateCache(_resolverService.createReference(providerId, alias));

			System.out.println("Key deleted: " + alias);
		}
		catch (KeyProviderException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void ref(String providerId, String alias) {
		System.out.println(_resolverService.createReference(providerId, alias));
	}

	public void invalidateCache() {
		_resolverService.invalidateAllCaches();

		System.out.println("All key caches invalidated.");
	}

	@Reference
	private KeyResolverService _resolverService;

	@Reference
	private KeyProviderRegistry _registry;

}
