/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import java.sql.Blob;

/**
 * The Blob model class for lazy loading the ciphertextBlob column in SecretEntry.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntry
 * @generated
 */
public class SecretEntryCiphertextBlobBlobModel {

	public SecretEntryCiphertextBlobBlobModel() {
	}

	public SecretEntryCiphertextBlobBlobModel(long secretEntryId) {
		_secretEntryId = secretEntryId;
	}

	public SecretEntryCiphertextBlobBlobModel(
		long secretEntryId, Blob ciphertextBlobBlob) {

		_secretEntryId = secretEntryId;
		_ciphertextBlobBlob = ciphertextBlobBlob;
	}

	public long getSecretEntryId() {
		return _secretEntryId;
	}

	public void setSecretEntryId(long secretEntryId) {
		_secretEntryId = secretEntryId;
	}

	public Blob getCiphertextBlobBlob() {
		return _ciphertextBlobBlob;
	}

	public void setCiphertextBlobBlob(Blob ciphertextBlobBlob) {
		_ciphertextBlobBlob = ciphertextBlobBlob;
	}

	private long _secretEntryId;
	private Blob _ciphertextBlobBlob;

}