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
 * The extended model interface for the KeyEntry service. Represents a row in the &quot;KeyManagerDB_KeyEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.keymanager.provider.db.model.impl.KeyEntryImpl"
)
@ProviderType
public interface KeyEntry extends KeyEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.keymanager.provider.db.model.impl.KeyEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<KeyEntry, Long> KEY_ENTRY_ID_ACCESSOR =
		new Accessor<KeyEntry, Long>() {

			@Override
			public Long get(KeyEntry keyEntry) {
				return keyEntry.getKeyEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<KeyEntry> getTypeClass() {
				return KeyEntry.class;
			}

		};

}