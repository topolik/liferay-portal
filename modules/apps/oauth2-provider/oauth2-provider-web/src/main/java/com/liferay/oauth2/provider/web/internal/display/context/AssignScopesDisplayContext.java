/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.oauth2.provider.scope.liferay.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.web.internal.AssignableScopes;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Stian Sigvartsen
 */
public class AssignScopesDisplayContext {

	public AssignScopesDisplayContext(
		ApplicationDescriptorLocator applicationDescriptorLocator,
		Locale locale, long companyId,
		ScopeDescriptorLocator scopeDescriptorLocator,
		ScopeLocator scopeLocator) {

		_applicationDescriptorLocator = applicationDescriptorLocator;
		_locale = locale;

		Set<String> scopeAliases = new HashSet<>(
			scopeLocator.getScopeAliases(companyId));

		for (String scopeAlias : scopeAliases) {
			AssignableScopes assignableScopes = new AssignableScopes(
				applicationDescriptorLocator, _locale, scopeDescriptorLocator);

			assignableScopes.addLiferayOAuth2Scopes(
				scopeLocator.getLiferayOAuth2Scopes(companyId, scopeAlias));

			_assignableScopessRelations.put(
				assignableScopes, new Relations(scopeAlias));

			Set<String> applicationNames =
				assignableScopes.getApplicationNames();

			if (applicationNames.size() > 1) {
				for (String applicationName : applicationNames) {
					Set<AssignableScopes> assignableScopess =
						_globalAssignableScopessByApplicationName.
							computeIfAbsent(
								applicationName, a -> new HashSet<>());

					assignableScopess.add(assignableScopes);
				}
			}
			else if (applicationNames.size() == 1) {
				Iterator<String> iterator = applicationNames.iterator();

				String applicationName = iterator.next();

				Set<AssignableScopes> assignableScopess =
					_localAssignableScopessByApplicationName.computeIfAbsent(
						applicationName, a -> new HashSet<>());

				assignableScopess.add(assignableScopes);
			}
		}
	}

	public String getApplicationDescription(String applicationName) {
		ApplicationDescriptor applicationDescriptor =
			_applicationDescriptorLocator.getApplicationDescriptor(
				applicationName);

		if (applicationDescriptor == null) {
			return applicationName;
		}

		return applicationDescriptor.describeApplication(_locale);
	}

	public Set<String> getApplicationNames() {
		Set<String> applicationNames = new HashSet<>();

		applicationNames.addAll(
			_globalAssignableScopessByApplicationName.keySet());
		applicationNames.addAll(
			_localAssignableScopessByApplicationName.keySet());

		return applicationNames;
	}

	public Map<String, String> getApplicationNamesDescriptions() {
		Map<String, String> applicationNamesDescriptions = new HashMap<>();

		for (String applicationName : getApplicationNames()) {
			applicationNamesDescriptions.put(
				applicationName, getApplicationDescription(applicationName));
		}

		return applicationNamesDescriptions;
	}

	public Map<AssignableScopes, Relations>
		getAssignableScopesRelationsMap(String applicationName) {

		Set<AssignableScopes> localAssignableScopess =
			_localAssignableScopessByApplicationName.get(applicationName);

		Map<AssignableScopes, Relations> localRelations = new HashMap<>();

		if (localAssignableScopess != null) {
			localRelations.putAll(
				getAssignableScopessRelations(localAssignableScopess));
		}

		Set<AssignableScopes> globalAssignableScopess =
			_globalAssignableScopessByApplicationName.get(applicationName);

		if (globalAssignableScopess == null) {
			return localRelations;
		}

		Map<AssignableScopes, Relations> assignableScopesRelationsMap =
			new HashMap<>(localRelations);

		for (AssignableScopes assignableScopes :
				globalAssignableScopess) {

			AssignableScopes applicationAssignableScopes =
				assignableScopes.getApplicationAssignableScopes(
					applicationName);

			for (Map.Entry<AssignableScopes, Relations> entry :
					localRelations.entrySet()) {

				if (assignableScopes.contains(entry.getKey())) {
					Relations relations = entry.getValue();

					relations._globalAssignableScopess.add(
						assignableScopes);
				}

				applicationAssignableScopes =
					applicationAssignableScopes.subtract(entry.getKey());
			}

			Relations relations =
				assignableScopesRelationsMap.computeIfAbsent(
					applicationAssignableScopes, a -> new Relations());

			relations._globalAssignableScopess.add(assignableScopes);
		}

		return _normalize(assignableScopesRelationsMap);
	}

	public Map<AssignableScopes, Relations>
		getGlobalAssignableScopessRelations() {

		Collection<Set<AssignableScopes>> assignableScopessCollection =
			_globalAssignableScopessByApplicationName.values();

		Stream<Set<AssignableScopes>> stream =
			assignableScopessCollection.stream();

		return stream.flatMap(
			Set::stream
		).collect(
			Collectors.toSet()
		).stream(
		).filter(
			_assignableScopessRelations::containsKey
		).collect(
			Collectors.toMap(
				Function.identity(), _assignableScopessRelations::get)
		);
	}

	public class Relations {

		public Relations() {
			_scopeAlias = StringPool.BLANK;
		}

		public Relations(String scopeAlias) {
			_scopeAlias = scopeAlias;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if ((o == null) || (getClass() != o.getClass())) {
				return false;
			}

			Relations relations = (Relations)o;

			if (Objects.equals(
					_globalAssignableScopess,
					relations._globalAssignableScopess) &&
				Objects.equals(_scopeAlias, relations._scopeAlias)) {

				return true;
			}

			return false;
		}

		public Set<String> getGlobalScopeAliases() {
			Stream<AssignableScopes> stream =
				_globalAssignableScopess.stream();

			return stream.map(
				_assignableScopessRelations::get
			).map(
				Relations::getScopeAlias
			).collect(
				Collectors.toSet()
			);
		}

		public String getScopeAlias() {
			return _scopeAlias;
		}

		@Override
		public int hashCode() {
			return Objects.hash(_globalAssignableScopess, _scopeAlias);
		}

		private Set<AssignableScopes> _globalAssignableScopess =
			new HashSet<>();
		private final String _scopeAlias;

	}

	protected Map<AssignableScopes, Relations>
		getAssignableScopessRelations(
			Set<AssignableScopes> assignableScopess) {

		Stream<AssignableScopes> assignableScopessStream =
			assignableScopess.stream();

		return assignableScopessStream.filter(
			_assignableScopessRelations::containsKey
		).collect(
			Collectors.toMap(
				Function.identity(), _assignableScopessRelations::get)
		);
	}

	private static <K, V> Map<V, K> _invertMap(Map<K, V> map) {
		Map<V, K> ret = new HashMap<>(map.size());

		for (Map.Entry<K, V> entry : map.entrySet()) {
			ret.put(entry.getValue(), entry.getKey());
		}

		return ret;
	}

	private Map<AssignableScopes, Relations> _normalize(
		Map<AssignableScopes, Relations> assignableScopesRelationsMap) {

		Map<AssignableScopes, Relations>
			combinedAssignableScopessRelationsMap = new HashMap<>();

		for (Map.Entry<AssignableScopes, Relations>
				assignableScopessRelationsEntry :
					assignableScopesRelationsMap.entrySet()) {

			Relations relations = assignableScopessRelationsEntry.getValue();

			String scopeAlias = relations.getScopeAlias();

			AssignableScopes assignableScopes =
				assignableScopessRelationsEntry.getKey();

			// Preserve AssignableScopess that are assigned an alias

			if (!Validator.isBlank(scopeAlias)) {
				combinedAssignableScopessRelationsMap.put(
					assignableScopes, relations);

				continue;
			}

			// Reduce other AssignableScopess down to individual
			// application scopes. But keep the master AssignableScopes
			// relations of each original AssignableScopes

			Set<AssignableScopes> applicationScopeAssignableScopess =
				assignableScopes.splitByApplicationScopes();

			for (AssignableScopes applicationScopeAssignableScopes :
					applicationScopeAssignableScopess) {

				Relations combinedRelations =
					combinedAssignableScopessRelationsMap.computeIfAbsent(
						applicationScopeAssignableScopes,
						__ -> new Relations());

				combinedRelations._globalAssignableScopess.addAll(
					relations._globalAssignableScopess);
			}
		}

		HashMap<Relations, AssignableScopes> relationsAssignableScopess =
			new HashMap<>();

		// Finally merge those by identical master AssignableScopes relations

		for (Map.Entry<AssignableScopes, Relations>
				entry : combinedAssignableScopessRelationsMap.entrySet()) {

			relationsAssignableScopess.compute(
				entry.getValue(),
				(key, existingValue) -> {
					if (existingValue != null) {
						return existingValue.add(entry.getKey());
					}
					else {
						return entry.getKey();
					}
				});
		}

		return _invertMap(relationsAssignableScopess);
	}

	private final ApplicationDescriptorLocator _applicationDescriptorLocator;
	private Map<AssignableScopes, Relations> _assignableScopessRelations =
		new HashMap<>();
	private Map<String, Set<AssignableScopes>>
		_globalAssignableScopessByApplicationName = new HashMap<>();
	private Map<String, Set<AssignableScopes>>
		_localAssignableScopessByApplicationName = new HashMap<>();
	private final Locale _locale;

}