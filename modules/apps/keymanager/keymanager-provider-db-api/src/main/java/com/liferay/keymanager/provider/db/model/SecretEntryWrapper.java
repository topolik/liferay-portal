/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.sql.Blob;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link SecretEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntry
 * @generated
 */
public class SecretEntryWrapper
	extends BaseModelWrapper<SecretEntry>
	implements ModelWrapper<SecretEntry>, SecretEntry {

	public SecretEntryWrapper(SecretEntry secretEntry) {
		super(secretEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("secretEntryId", getSecretEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("alias", getAlias());
		attributes.put("ciphertextBlob", getCiphertextBlob());
		attributes.put("iv", getIv());
		attributes.put("encryptedDEKBlob", getEncryptedDEKBlob());
		attributes.put("kekReference", getKekReference());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long secretEntryId = (Long)attributes.get("secretEntryId");

		if (secretEntryId != null) {
			setSecretEntryId(secretEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String alias = (String)attributes.get("alias");

		if (alias != null) {
			setAlias(alias);
		}

		Blob ciphertextBlob = (Blob)attributes.get("ciphertextBlob");

		if (ciphertextBlob != null) {
			setCiphertextBlob(ciphertextBlob);
		}

		String iv = (String)attributes.get("iv");

		if (iv != null) {
			setIv(iv);
		}

		Blob encryptedDEKBlob = (Blob)attributes.get("encryptedDEKBlob");

		if (encryptedDEKBlob != null) {
			setEncryptedDEKBlob(encryptedDEKBlob);
		}

		String kekReference = (String)attributes.get("kekReference");

		if (kekReference != null) {
			setKekReference(kekReference);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}
	}

	@Override
	public SecretEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the alias of this secret entry.
	 *
	 * @return the alias of this secret entry
	 */
	@Override
	public String getAlias() {
		return model.getAlias();
	}

	/**
	 * Returns the ciphertext blob of this secret entry.
	 *
	 * @return the ciphertext blob of this secret entry
	 */
	@Override
	public Blob getCiphertextBlob() {
		return model.getCiphertextBlob();
	}

	/**
	 * Returns the company ID of this secret entry.
	 *
	 * @return the company ID of this secret entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this secret entry.
	 *
	 * @return the create date of this secret entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the encrypted dek blob of this secret entry.
	 *
	 * @return the encrypted dek blob of this secret entry
	 */
	@Override
	public Blob getEncryptedDEKBlob() {
		return model.getEncryptedDEKBlob();
	}

	/**
	 * Returns the iv of this secret entry.
	 *
	 * @return the iv of this secret entry
	 */
	@Override
	public String getIv() {
		return model.getIv();
	}

	/**
	 * Returns the kek reference of this secret entry.
	 *
	 * @return the kek reference of this secret entry
	 */
	@Override
	public String getKekReference() {
		return model.getKekReference();
	}

	/**
	 * Returns the modified date of this secret entry.
	 *
	 * @return the modified date of this secret entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this secret entry.
	 *
	 * @return the mvcc version of this secret entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this secret entry.
	 *
	 * @return the primary key of this secret entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the secret entry ID of this secret entry.
	 *
	 * @return the secret entry ID of this secret entry
	 */
	@Override
	public long getSecretEntryId() {
		return model.getSecretEntryId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the alias of this secret entry.
	 *
	 * @param alias the alias of this secret entry
	 */
	@Override
	public void setAlias(String alias) {
		model.setAlias(alias);
	}

	/**
	 * Sets the ciphertext blob of this secret entry.
	 *
	 * @param ciphertextBlob the ciphertext blob of this secret entry
	 */
	@Override
	public void setCiphertextBlob(Blob ciphertextBlob) {
		model.setCiphertextBlob(ciphertextBlob);
	}

	/**
	 * Sets the company ID of this secret entry.
	 *
	 * @param companyId the company ID of this secret entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this secret entry.
	 *
	 * @param createDate the create date of this secret entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the encrypted dek blob of this secret entry.
	 *
	 * @param encryptedDEKBlob the encrypted dek blob of this secret entry
	 */
	@Override
	public void setEncryptedDEKBlob(Blob encryptedDEKBlob) {
		model.setEncryptedDEKBlob(encryptedDEKBlob);
	}

	/**
	 * Sets the iv of this secret entry.
	 *
	 * @param iv the iv of this secret entry
	 */
	@Override
	public void setIv(String iv) {
		model.setIv(iv);
	}

	/**
	 * Sets the kek reference of this secret entry.
	 *
	 * @param kekReference the kek reference of this secret entry
	 */
	@Override
	public void setKekReference(String kekReference) {
		model.setKekReference(kekReference);
	}

	/**
	 * Sets the modified date of this secret entry.
	 *
	 * @param modifiedDate the modified date of this secret entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this secret entry.
	 *
	 * @param mvccVersion the mvcc version of this secret entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this secret entry.
	 *
	 * @param primaryKey the primary key of this secret entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the secret entry ID of this secret entry.
	 *
	 * @param secretEntryId the secret entry ID of this secret entry
	 */
	@Override
	public void setSecretEntryId(long secretEntryId) {
		model.setSecretEntryId(secretEntryId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected SecretEntryWrapper wrap(SecretEntry secretEntry) {
		return new SecretEntryWrapper(secretEntry);
	}

}