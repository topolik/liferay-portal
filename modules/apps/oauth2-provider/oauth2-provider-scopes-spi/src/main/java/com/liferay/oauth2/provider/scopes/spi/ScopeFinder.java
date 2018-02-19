package com.liferay.oauth2.provider.scopes.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * This class is the entry point to the OAuth2 Scopes framework. Applications
 * will need to register one ScopeFinder, or have one registered on their
 * behalf, with the property osgi.jaxrs.name. This name must be unique across
 * the instance. The property matches with the mandatory property for
 * OSGi JAX-RS spec.
 *
 */
public interface ScopeFinder {

	/**
	 * Returns the list of scopes, internal to the application, that the given
	 * ScopeMatcher matches. Implementations may return several scopes for a
	 * given <i>query</i>. For example, if a particular implementation
	 * wants the scope <i>RW</i> (standing for <i>read-write</i>) to imply the
	 * scope <i>RO</i> (standing for <i>read-only</i>) it can test whether "RW"
	 * is a match using <code>scopeMatcher.match("RW")</code> and, if
	 * <i>true</i>, then return a collection containing both <i>RW</i> and
	 * <i>RO</i>.
	 * Implementations <b>SHOULD</b> test all their available scopes against
	 * the matcher to allow for different matching strategies to succeed.
	 * Only the scopes returned can be used on
	 * {@link com.liferay.oauth2.provider.scopes.api.RequiresScope} annotation
	 * or in invocations to
	 * {@link com.liferay.oauth2.provider.scopes.api.ScopeChecker} methods.
	 *
	 * @param scopeMatcher to filter the application available scopes.
	 * @return a collection of the available scopes that the implementation
	 * will associate with the given {@link ScopeMatcher}.
	 */
	public Collection<String> findScopes(ScopeMatcher scopeMatcher);

	static ScopeFinder empty(ScopeMatcher matcher) {
		return __ -> Collections.emptyList();
	}

	/**
	 * Default implementation that creates a matcher using the factory returned
	 * by {@link ScopeFinder#getDefaultScopeMatcherFactory()}
	 *
	 * @param scope the incoming query to match against.
	 * @return a collection of the available scopes that the implementation
	 * will associate with the given incoming scope.
	 */
	public default Collection<String> findScopes(String scope) {
		ScopeMatcherFactory scopeMatcherFactory =
			getDefaultScopeMatcherFactory();

		return findScopes(scopeMatcherFactory.create(scope));
	}

	/**
	 * Returns the preferred {@link ScopeMatcherFactory} for this particular
	 * {@link ScopeFinder} implementation. The framework will likely change
	 * this implementation in order to accomodate the scope resolution to
	 * different strategies.
	 * @return the preferred {@link ScopeMatcherFactory} for a given
	 * implementations. Defaults to {@link ScopeMatcherFactory#STRICT}.
	 */
	public default ScopeMatcherFactory getDefaultScopeMatcherFactory() {
		return ScopeMatcherFactory.STRICT;
	}

	/**
	 * This method allows to describe a particular set of scopes for a given
	 * locale. This gives an opportunity to collapse global description that
	 * would be redundant. As an example, if a scope <i>EVERYTHING</i> is
	 * granted, it might be redundant to describe that the application is
	 * requesting to do everything, as well as read or write, it is already
	 * implicit.
	 *
	 * @param scopes the set of scopes to be described.
	 * @param locale the locale requested for the description
	 * @return a description for the set of scopes in the requested locale.
	 */
	public default String describe(
		Collection<String> scopes, Locale locale) {

		return String.join(", ", scopes);
	}

}