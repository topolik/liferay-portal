package com.liferay.oauth2.provider.scopes.spi;

/**
 * Convenience interface to create different {@link PrefixHandler} for different
 * objects.
 */
public interface PrefixHandlerMapper {

	/**
	 * This method allows to create a {@link PrefixHandler} using the properties
	 * in the {@link PropertyGetter}
	 *
	 * @param propertyGetter the {@link PropertyGetter} to configure the
	 *                       {@link PrefixHandler}
	 * @return the {@link PrefixHandler} for the given {@link PropertyGetter}
	 */
	public PrefixHandler mapFrom(PropertyGetter propertyGetter);

}