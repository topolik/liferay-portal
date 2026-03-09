/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import java.sql.Blob;

/**
 * The Blob model class for lazy loading the wrappedKeyBlob column in KeyEntry.
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntry
 * @generated
 */
public class KeyEntryWrappedKeyBlobBlobModel {

	public KeyEntryWrappedKeyBlobBlobModel() {
	}

	public KeyEntryWrappedKeyBlobBlobModel(long keyEntryId) {
		_keyEntryId = keyEntryId;
	}

	public KeyEntryWrappedKeyBlobBlobModel(
		long keyEntryId, Blob wrappedKeyBlobBlob) {

		_keyEntryId = keyEntryId;
		_wrappedKeyBlobBlob = wrappedKeyBlobBlob;
	}

	public long getKeyEntryId() {
		return _keyEntryId;
	}

	public void setKeyEntryId(long keyEntryId) {
		_keyEntryId = keyEntryId;
	}

	public Blob getWrappedKeyBlobBlob() {
		return _wrappedKeyBlobBlob;
	}

	public void setWrappedKeyBlobBlob(Blob wrappedKeyBlobBlob) {
		_wrappedKeyBlobBlob = wrappedKeyBlobBlob;
	}

	private long _keyEntryId;
	private Blob _wrappedKeyBlobBlob;

}