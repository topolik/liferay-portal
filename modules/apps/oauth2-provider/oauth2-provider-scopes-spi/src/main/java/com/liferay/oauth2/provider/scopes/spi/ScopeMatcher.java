package com.liferay.oauth2.provider.scopes.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This interface represents the strategy used to match scopes. Some of these
 * strategies may be:
 *
 * <ul>
 *     <li>NONE: no scope will match</li>
 *     <li>ALL: all scopes will match</li>
 *     <li>STRICT: only scopes matching a particular string or strings
 *     will match</li>
 *     <li>HIERARCHICAL: scopes following some naming rules might match more
 *     general scopes. For instance using <i>dot notation</i> we can code that
 *     shorter scopes that share a common prefix, for example
 *     <i>everything</i> imply longer scopes such as <i>everything.readonly</i>.
 *	   </li>
 * </ul>
 *
 * ScopeMatcher might also be combined with {@link PrefixHandler} and
 * {@link ScopeMapper} to tailor the matching strategy to the framework
 * configuration.
 *
 * {@link ScopeFinder} implementations SHOULD take this into account to support
 * as many strategies as possible.
 */

public interface ScopeMatcher {

	/**
	 * Specifies if a given scope matches according to the {@link ScopeMatcher}.
	 *
	 * @param name
	 * @return true if the input scope is a match for the {@link ScopeMatcher},
	 * false otherwise.
	 */
	public boolean match(String name);

	/**
	 * Applies the matcher to a collection of scopes. Some implementations
	 * might have optimization opportunities.
	 *
	 * @param names the collection of scopes to match.
	 * @return a collection containing those scopes that matched.
	 */
	public default Collection<String> filter(Collection<String> names) {
		return names.stream().filter(this::match).collect(Collectors.toList());
	}

	/**
	 * Returns a new {@link ScopeMatcher} that takes into account the effect
	 * of the given {@link PrefixHandler}. Some implementations might have
	 * optimization opportunities.
	 *
	 * @param prefixHandler the prefix handler that will affect the scope
	 * matcher.
	 * @return the new ScopeMatcher that takes into account the
	 * {@link PrefixHandler}
	 */
	public default ScopeMatcher prepend(PrefixHandler prefixHandler) {
		return localName -> match(prefixHandler.addPrefix(localName));
	}

	/**
	 * Returns a new {@link ScopeMatcher} that takes into account the effect
	 * of the given {@link ScopeMapper}. Some implementations might have
	 * optimization opportunities.
	 * @param scopeMapper the scope mapper that affects the scope matcher.
	 * @return the new {@link ScopeMatcher} that takes into account the given
	 * {@link ScopeMapper}.
	 */
	public default ScopeMatcher withMapper(ScopeMapper scopeMapper) {
		return localName -> match(scopeMapper.map(localName));
	}

	public default ScopeMatcher and(ScopeMatcher scopeMatcher) {
		return localName ->
			match(localName) && scopeMatcher.match(localName);
	}

	public default ScopeMatcher or(ScopeMatcher scopeMatcher) {
		return localName ->
			match(localName) || scopeMatcher.match(localName);
	}

	public static ScopeMatcher ALL = __ -> true;

	public static ScopeMatcher NONE = __ -> false;

}
