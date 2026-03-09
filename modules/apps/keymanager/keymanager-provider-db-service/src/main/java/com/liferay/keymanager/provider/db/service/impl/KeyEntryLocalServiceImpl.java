/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.impl;

import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.service.base.KeyEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "model.class.name=com.liferay.keymanager.provider.db.model.KeyEntry",
	service = AopService.class
)
public class KeyEntryLocalServiceImpl extends KeyEntryLocalServiceBaseImpl {

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KeyEntry fetchKeyEntry(long companyId, String alias) {
		return keyEntryPersistence.fetchByC_A(companyId, alias);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KeyEntry getKeyEntry(long companyId, String alias) throws Exception {
		return keyEntryPersistence.findByC_A(companyId, alias);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<String> getKeyIdentifiers(long companyId) {
		List<KeyEntry> keyEntries = keyEntryPersistence.findByCompanyId(
			companyId);

		return keyEntries.stream(
		).map(
			KeyEntry::getAlias
		).collect(
			Collectors.toList()
		);
	}

}
