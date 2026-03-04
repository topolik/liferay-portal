/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"osgi.command.function=delete", "osgi.command.function=invalidateCache",
		"osgi.command.function=list", "osgi.command.function=providers",
		"osgi.command.function=ref", "osgi.command.function=resolve",
		"osgi.command.function=store", "osgi.command.scope=keymanager"
	},
	service = KeyManagerCommands.class
)
public class KeyManagerCommands {

	public void delete(String providerId, String alias) {
		try {
			KeyProvider keyProvider = _keyProviderRegistry.getProvider(
				providerId
			).orElseThrow(
				() -> new KeyProviderException("Provider not found: " + providerId)
			);

			keyProvider.deleteKey(alias);

			_keyResolverService.invalidateCache(
				_keyResolverService.createReference(providerId, alias));

			System.out.println("Key deleted: " + alias);
		}
		catch (KeyProviderException kpe) {
			System.err.println("Error: " + kpe.getMessage());
		}
	}

	public void invalidateCache() {
		_keyResolverService.invalidateAllCaches();

		System.out.println("All key caches invalidated.");
	}

	public void list(String providerId) {
		try {
			KeyProvider keyProvider = _keyProviderRegistry.getProvider(
				providerId
			).orElseThrow(
				() -> new KeyProviderException("Provider not found: " + providerId)
			);

			List<String> aliases = keyProvider.listAliases();

			System.out.println("Keys in provider '" + providerId + "':");

			for (String alias : aliases) {
				KeyMetadata keyMetadata = keyProvider.getKeyMetadata(alias);

				System.out.println(
					"  " + alias + " [type=" + keyMetadata.getKeyType() +
						", version=" + keyMetadata.getVersion() + "]");
			}

			System.out.println("Total: " + aliases.size() + " keys");
		}
		catch (KeyProviderException kpe) {
			System.err.println("Error: " + kpe.getMessage());
		}
	}

	public void providers() {
		List<KeyProvider> keyProviders =
			_keyProviderRegistry.getAllProviders();

		System.out.println("Registered Key Providers:");
		System.out.printf(
			"%-15s %-30s %-10s %-10s%n", "ID", "Name", "Priority", "Available");
		System.out.println("-".repeat(70));

		for (KeyProvider keyProvider : keyProviders) {
			System.out.printf(
				"%-15s %-30s %-10d %-10s%n", keyProvider.getProviderId(),
				keyProvider.getDisplayName(), keyProvider.getPriority(),
				keyProvider.isAvailable() ? "YES" : "NO");
		}
	}

	public void ref(String providerId, String alias) {
		System.out.println(_keyResolverService.createReference(providerId, alias));
	}

	public void resolve(String reference) {
		try {
			String resolved = _keyResolverService.resolve(reference);

			System.out.println(
				"Resolved successfully. Value length: " + resolved.length());
			System.out.println(
				"First 3 chars: " +
					resolved.substring(0, Math.min(3, resolved.length())) +
						"***");
		}
		catch (KeyResolutionException kre) {
			System.err.println("Error: " + kre.getMessage());
		}
	}

	public void store(String providerId, String alias, String value) {
		try {
			char[] valueChars = value.toCharArray();

			String reference = _keyResolverService.storeAndReference(
				providerId, alias, valueChars);

			Arrays.fill(valueChars, '\0');

			System.out.println("Key stored successfully.");
			System.out.println("Reference: " + reference);
		}
		catch (KeyResolutionException kre) {
			System.err.println("Error: " + kre.getMessage());
		}
	}

	@Reference
	private KeyProviderRegistry _keyProviderRegistry;

	@Reference
	private KeyResolverService _keyResolverService;

}
