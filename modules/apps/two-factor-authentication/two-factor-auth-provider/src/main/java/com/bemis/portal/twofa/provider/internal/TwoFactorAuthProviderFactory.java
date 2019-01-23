package com.bemis.portal.twofa.provider.internal;

import com.bemis.portal.twofa.provider.constants.TwoFactorAuthProviderConstants;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = TwoFactorAuthProviderFactory.class)
public class TwoFactorAuthProviderFactory {

	public TwoFactorAuthProvider getTwoFactorAuthProvider(String providerType) {
		return _twoFactorAuthProviders.get(providerType);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unsetTwoFactorAuthProvider"
	)
	public void setTwoFactorAuthProvider(
		TwoFactorAuthProvider twoFactorAuthProvider,
		Map<String, Object> properties) {

		String providerType = (String)properties.get(
			TwoFactorAuthProviderConstants.TWO_FA_PROVIDER_CONFIG);

		_twoFactorAuthProviders.put(providerType, twoFactorAuthProvider);
	}

	public void unsetTwoFactorAuthProvider(
		TwoFactorAuthProvider twoFactorAuthProvider,
		Map<String, Object> properties) {

		String providerType = (String)properties.get(
			TwoFactorAuthProviderConstants.TWO_FA_PROVIDER_CONFIG);

		if (Validator.isNull(providerType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No such Two Factor Auth provider exists for service: " +
						providerType);
			}

			return;
		}

		_twoFactorAuthProviders.remove(providerType);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TwoFactorAuthProviderFactory.class);

	private Map<String, TwoFactorAuthProvider> _twoFactorAuthProviders =
		new ConcurrentHashMap<>();

}