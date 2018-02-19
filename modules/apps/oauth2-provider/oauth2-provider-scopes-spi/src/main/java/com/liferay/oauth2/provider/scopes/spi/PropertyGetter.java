package com.liferay.oauth2.provider.scopes.spi;

/**
 * Convenience interface to represent any class holding properties.
 * It can be easily instantiated from
 * {@link org.osgi.framework.ServiceReference} using
 * <code>serviceReference::getProperty</code> or from a {@link java.util.Map}
 * using <code>map::get</code>.
 * This class helps to decouple from OSGi api.
 *
 */
@FunctionalInterface
public interface PropertyGetter {

	Object get(String propertyName);

}