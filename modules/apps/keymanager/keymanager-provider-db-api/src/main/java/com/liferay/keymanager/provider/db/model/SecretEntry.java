/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the SecretEntry service. Represents a row in the &quot;KeyManagerDB_SecretEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.keymanager.provider.db.model.impl.SecretEntryImpl"
)
@ProviderType
public interface SecretEntry extends PersistedModel, SecretEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.keymanager.provider.db.model.impl.SecretEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<SecretEntry, Long> SECRET_ENTRY_ID_ACCESSOR =
		new Accessor<SecretEntry, Long>() {

			@Override
			public Long get(SecretEntry secretEntry) {
				return secretEntry.getSecretEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<SecretEntry> getTypeClass() {
				return SecretEntry.class;
			}

		};

}