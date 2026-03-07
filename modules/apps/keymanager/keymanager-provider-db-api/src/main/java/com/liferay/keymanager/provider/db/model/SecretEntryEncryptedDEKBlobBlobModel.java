/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import java.sql.Blob;

/**
 * The Blob model class for lazy loading the encryptedDEKBlob column in SecretEntry.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntry
 * @generated
 */
public class SecretEntryEncryptedDEKBlobBlobModel {

	public SecretEntryEncryptedDEKBlobBlobModel() {
	}

	public SecretEntryEncryptedDEKBlobBlobModel(long secretEntryId) {
		_secretEntryId = secretEntryId;
	}

	public SecretEntryEncryptedDEKBlobBlobModel(
		long secretEntryId, Blob encryptedDEKBlobBlob) {

		_secretEntryId = secretEntryId;
		_encryptedDEKBlobBlob = encryptedDEKBlobBlob;
	}

	public long getSecretEntryId() {
		return _secretEntryId;
	}

	public void setSecretEntryId(long secretEntryId) {
		_secretEntryId = secretEntryId;
	}

	public Blob getEncryptedDEKBlobBlob() {
		return _encryptedDEKBlobBlob;
	}

	public void setEncryptedDEKBlobBlob(Blob encryptedDEKBlobBlob) {
		_encryptedDEKBlobBlob = encryptedDEKBlobBlob;
	}

	private long _secretEntryId;
	private Blob _encryptedDEKBlobBlob;

}