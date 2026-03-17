package com.liferay.keymanager.internal.configuration.persistence.listener;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.keymanager.ConfigurationKeyReference;
import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Objects;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "model.class.name=*",
	service = ConfigurationModelListener.class
)
public class KeymanagerConfigurationModelListener 
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties) 
			throws ConfigurationModelListenerException {
		
		if (Validator.isNull(pid) || properties == null) {
			return;
		}

		long companyId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);
		
		long groupId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		ConfigurationPidMapping configurationPidMapping =
			_settingsLocatorHelper.getConfigurationPidMapping(
				ConfigurationPidUtil.getRawPid(pid));

		Class<?> configurationBeanClass = 
			configurationPidMapping.getConfigurationBeanClass();

		for (Method method : configurationBeanClass.getMethods()) {
			Meta.AD ad = method.getAnnotation(Meta.AD.class);
	
			if (ad == null) {
				continue;
			}

			Meta.Type type = ad.type();

			if (!Meta.Type.Password.equals(type)) {
				continue;
			}

			String key = method.getName();

			if (!_isNull(ad.id())) {
				key = ad.id();
			}

			Object value = properties.get(key);
			
			if (value != null && !(value instanceof String)) {
				continue;
			}
			
			String password = (String) value;
			
			if (KeyReference.isKeyReference(password)) {
				continue;
			}
			
			KeyReference keyReference = 
				new ConfigurationKeyReference(
					configurationBeanClass.getName(), key, companyId, groupId);
			
			SecureSecret secureSecret = 
				new SecureSecret(keyReference, password);

			try {
				_secSecretManager.putSecret(companyId, secureSecret);
				
				properties.put(key, keyReference.toString());
			}
			catch (SecretManagerException e) {
				throw new ConfigurationModelListenerException(
					StringBundler.concat("Unable to store secret of ", pid, 
						" with key ", key, " into SecretManager: ", 
						e.getMessage()), configurationBeanClass, 
					KeymanagerConfigurationModelListener.class, properties);
			}
		}
	}

	private static boolean _isNull(String value) {
		if (Objects.equals(Meta.NULL, value) || Validator.isNull(value)) {
			return true;
		}

		return false;
	}	
	
	@Reference
	private SecretManager _secSecretManager;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;
}