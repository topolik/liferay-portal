package com.liferay.keymanager;

import com.liferay.keymanager.exception.KeyResolutionException;

import java.util.List;
import java.util.Map;

public interface KeyResolverService {

	boolean isKeyReference(String value);

	String resolve(String value) throws KeyResolutionException;

	char[] resolveSecure(KeyReference reference) throws KeyResolutionException;

	Map<String, String> resolveAll(Map<String, String> properties) throws KeyResolutionException;

	KeyReference parseReference(String referenceString) throws KeyResolutionException;

	String createReference(String providerId, String alias);

	String storeAndReference(String providerId, String alias, char[] value) throws KeyResolutionException;

	List<KeyProvider> getAvailableProviders();

	void invalidateCache(String referenceString);

	void invalidateAllCaches();

}
