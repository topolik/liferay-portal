/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model.impl;

import com.liferay.keymanager.provider.db.model.SecretEntry;
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
 * The cache model class for representing SecretEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SecretEntryCacheModel
	implements CacheModel<SecretEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SecretEntryCacheModel)) {
			return false;
		}

		SecretEntryCacheModel secretEntryCacheModel =
			(SecretEntryCacheModel)object;

		if ((secretEntryId == secretEntryCacheModel.secretEntryId) &&
			(mvccVersion == secretEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, secretEntryId);

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
		StringBundler sb = new StringBundler(17);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", secretEntryId=");
		sb.append(secretEntryId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", alias=");
		sb.append(alias);
		sb.append(", iv=");
		sb.append(iv);
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
	public SecretEntry toEntityModel() {
		SecretEntryImpl secretEntryImpl = new SecretEntryImpl();

		secretEntryImpl.setMvccVersion(mvccVersion);
		secretEntryImpl.setSecretEntryId(secretEntryId);
		secretEntryImpl.setCompanyId(companyId);

		if (alias == null) {
			secretEntryImpl.setAlias("");
		}
		else {
			secretEntryImpl.setAlias(alias);
		}

		if (iv == null) {
			secretEntryImpl.setIv("");
		}
		else {
			secretEntryImpl.setIv(iv);
		}

		if (kekReference == null) {
			secretEntryImpl.setKekReference("");
		}
		else {
			secretEntryImpl.setKekReference(kekReference);
		}

		if (createDate == Long.MIN_VALUE) {
			secretEntryImpl.setCreateDate(null);
		}
		else {
			secretEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			secretEntryImpl.setModifiedDate(null);
		}
		else {
			secretEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		secretEntryImpl.resetOriginalValues();

		return secretEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		secretEntryId = objectInput.readLong();

		companyId = objectInput.readLong();
		alias = objectInput.readUTF();
		iv = objectInput.readUTF();
		kekReference = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(secretEntryId);

		objectOutput.writeLong(companyId);

		if (alias == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(alias);
		}

		if (iv == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(iv);
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
	public long secretEntryId;
	public long companyId;
	public String alias;
	public String iv;
	public String kekReference;
	public long createDate;
	public long modifiedDate;

}