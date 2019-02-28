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

package com.liferay.multi.factor.authentication.integration.internal;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.integration.internal.configuration.MFAIntegrationVerificationConfiguration;
import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.integration.internal.configuration.MFAIntegrationVerificationConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = MFAIntegrationVerification.class
)
public class MFAIntegrationVerification {

	private MFAIntegrationVerificationConfiguration
		_mfaIntegrationVerificationConfiguration;

	@Activate
	protected void activate(Map<String, Object> properties) {
		_mfaIntegrationVerificationConfiguration =
			ConfigurableUtil.createConfigurable(
				MFAIntegrationVerificationConfiguration.class, properties);
	}

	public void init(MFARegistry mfaRegistry) {
		MFAIntegration mfaIntegration = mfaRegistry.getMFAIntegration(
			_mfaIntegrationVerificationConfiguration.integrationName());

		String[] verifierNamesArray =
			_mfaIntegrationVerificationConfiguration.verifierNames();

		_mfaVerifiersList = new ArrayList(verifierNamesArray.length);

		for (String verifierNames : verifierNamesArray) {
			List<String> verifierNamesList = StringUtil.split(verifierNames);

			List<MFAVerifier> mfaVerifierList = new ArrayList<>(
				verifierNamesList.size());

			for (String verifierName : verifierNamesList) {
				MFAVerifier mfaVerifier = mfaRegistry.getMFAVerifier(
					verifierName);

				if ((mfaIntegration.supportsHeadless() &&
					 mfaVerifier.supportsHeadless()) ||
					(mfaIntegration.supportsBrowser() &&
					 mfaVerifier.supportsBrowser())) {

					mfaVerifierList.add(mfaVerifier);

					_mfaVerifiersNames.add(mfaVerifier.getName());
				}
				else {
					String mfaIntegrationSupports = "headless";
					if (mfaIntegration.supportsBrowser()) {
						mfaIntegrationSupports = "browser";
					}

					String mfaVerifierSupports = "headless";
					if (mfaVerifier.supportsBrowser()) {
						mfaVerifierSupports = "browser";
					}

					_log.error(
						StringBundler.concat(
							"MFA Verifier ", verifierName,
							" is not compatible with interation ",
							mfaIntegration.getName(),
							". The integration supports ",
							mfaIntegrationSupports, " but verifier supports ",
							mfaVerifierSupports));
				}
			}

			if (!mfaVerifierList.isEmpty()) {
				_mfaVerifiersList.add(mfaVerifierList);
			}
		}
	}

	public String getIntegrationName() {
		return _mfaIntegrationVerificationConfiguration.integrationName();
	}

	public List<List<MFAVerifier>> getMFAVerifiersList() {
		return _mfaVerifiersList;
	}

	public boolean hasMFAVerifier(String mfaVerifierName) {
		return mfaVerifierName.contains(mfaVerifierName);
	}

	private List<List<MFAVerifier>> _mfaVerifiersList;
	private Set<String> _mfaVerifiersNames = new HashSet<>();

	private static final Log _log = LogFactoryUtil.getLog(
		MFAIntegrationVerification.class);
}
