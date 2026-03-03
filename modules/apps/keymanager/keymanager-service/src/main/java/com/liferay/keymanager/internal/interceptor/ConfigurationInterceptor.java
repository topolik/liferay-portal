package com.liferay.keymanager.internal.interceptor;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.exception.KeyResolutionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"cm.target=*",
		ConfigurationPlugin.CM_RANKING + ":Integer=1000"
	},
	service = ConfigurationPlugin.class
)
public class ConfigurationInterceptor implements ConfigurationPlugin {

	@Override
	public void modifyConfiguration(ServiceReference<?> reference, Dictionary<String, Object> properties) {
		Enumeration<String> keys = properties.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Object value = properties.get(key);

			if (value instanceof String) {
				String stringValue = (String)value;

				if (_keyResolverService.isKeyReference(stringValue)) {
					try {
						String resolved = _keyResolverService.resolve(stringValue);

						properties.put(key, resolved);
					}
					catch (KeyResolutionException e) {
						_log.error("Failed to resolve key reference for property '" + key + "'", e);
					}
				}
			}
			else if (value instanceof String[]) {
				String[] arrayValue = (String[])value;
				boolean modified = false;

				for (int i = 0; i < arrayValue.length; i++) {
					if (_keyResolverService.isKeyReference(arrayValue[i])) {
						try {
							arrayValue[i] = _keyResolverService.resolve(arrayValue[i]);

							modified = true;
						}
						catch (KeyResolutionException e) {
							_log.error("Failed to resolve key reference in array property '" + key + "'", e);
						}
					}
				}

				if (modified) {
					properties.put(key, arrayValue);
				}
			}
		}
	}

	@Reference
	private KeyResolverService _keyResolverService;

	private static final Log _log = LogFactoryUtil.getLog(ConfigurationInterceptor.class);

}
