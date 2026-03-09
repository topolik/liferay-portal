/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model.impl;

import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing KeyEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class KeyEntryCacheModel
	implements CacheModel<KeyEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KeyEntryCacheModel)) {
			return false;
		}

		KeyEntryCacheModel keyEntryCacheModel = (KeyEntryCacheModel)object;

		if ((keyEntryId == keyEntryCacheModel.keyEntryId) &&
			(mvccVersion == keyEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, keyEntryId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(21);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", keyEntryId=");
		sb.append(keyEntryId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", alias=");
		sb.append(alias);
		sb.append(", keyType=");
		sb.append(keyType);
		sb.append(", algorithm=");
		sb.append(algorithm);
		sb.append(", cipherSpec=");
		sb.append(cipherSpec);
		sb.append(", kekReference=");
		sb.append(kekReference);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public KeyEntry toEntityModel() {
		KeyEntryImpl keyEntryImpl = new KeyEntryImpl();

		keyEntryImpl.setMvccVersion(mvccVersion);
		keyEntryImpl.setKeyEntryId(keyEntryId);
		keyEntryImpl.setCompanyId(companyId);

		if (alias == null) {
			keyEntryImpl.setAlias("");
		}
		else {
			keyEntryImpl.setAlias(alias);
		}

		if (keyType == null) {
			keyEntryImpl.setKeyType("");
		}
		else {
			keyEntryImpl.setKeyType(keyType);
		}

		if (algorithm == null) {
			keyEntryImpl.setAlgorithm("");
		}
		else {
			keyEntryImpl.setAlgorithm(algorithm);
		}

		if (cipherSpec == null) {
			keyEntryImpl.setCipherSpec("");
		}
		else {
			keyEntryImpl.setCipherSpec(cipherSpec);
		}

		if (kekReference == null) {
			keyEntryImpl.setKekReference("");
		}
		else {
			keyEntryImpl.setKekReference(kekReference);
		}

		if (createDate == Long.MIN_VALUE) {
			keyEntryImpl.setCreateDate(null);
		}
		else {
			keyEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			keyEntryImpl.setModifiedDate(null);
		}
		else {
			keyEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		keyEntryImpl.resetOriginalValues();

		return keyEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		keyEntryId = objectInput.readLong();

		companyId = objectInput.readLong();
		alias = objectInput.readUTF();
		keyType = objectInput.readUTF();
		algorithm = objectInput.readUTF();
		cipherSpec = objectInput.readUTF();
		kekReference = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(keyEntryId);

		objectOutput.writeLong(companyId);

		if (alias == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(alias);
		}

		if (keyType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(keyType);
		}

		if (algorithm == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(algorithm);
		}

		if (cipherSpec == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(cipherSpec);
		}

		if (kekReference == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(kekReference);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);
	}

	public long mvccVersion;
	public long keyEntryId;
	public long companyId;
	public String alias;
	public String keyType;
	public String algorithm;
	public String cipherSpec;
	public String kekReference;
	public long createDate;
	public long modifiedDate;

}