/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.impl;

import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.service.base.SecretEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "model.class.name=com.liferay.keymanager.provider.db.model.SecretEntry",
	service = AopService.class
)
public class SecretEntryLocalServiceImpl extends SecretEntryLocalServiceBaseImpl {

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SecretEntry fetchSecretEntry(long companyId, String alias) {
		return secretEntryPersistence.fetchByC_A(companyId, alias);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SecretEntry getSecretEntry(long companyId, String alias)
		throws Exception {

		return secretEntryPersistence.findByC_A(companyId, alias);
	}

}
