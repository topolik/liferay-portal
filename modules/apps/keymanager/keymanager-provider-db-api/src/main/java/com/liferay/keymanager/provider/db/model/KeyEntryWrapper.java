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
 * This class is a wrapper for {@link KeyEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntry
 * @generated
 */
public class KeyEntryWrapper
	extends BaseModelWrapper<KeyEntry>
	implements KeyEntry, ModelWrapper<KeyEntry> {

	public KeyEntryWrapper(KeyEntry keyEntry) {
		super(keyEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("keyEntryId", getKeyEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("alias", getAlias());
		attributes.put("keyType", getKeyType());
		attributes.put("algorithm", getAlgorithm());
		attributes.put("cipherSpec", getCipherSpec());
		attributes.put("wrappedKeyBlob", getWrappedKeyBlob());
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

		Long keyEntryId = (Long)attributes.get("keyEntryId");

		if (keyEntryId != null) {
			setKeyEntryId(keyEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String alias = (String)attributes.get("alias");

		if (alias != null) {
			setAlias(alias);
		}

		String keyType = (String)attributes.get("keyType");

		if (keyType != null) {
			setKeyType(keyType);
		}

		String algorithm = (String)attributes.get("algorithm");

		if (algorithm != null) {
			setAlgorithm(algorithm);
		}

		String cipherSpec = (String)attributes.get("cipherSpec");

		if (cipherSpec != null) {
			setCipherSpec(cipherSpec);
		}

		Blob wrappedKeyBlob = (Blob)attributes.get("wrappedKeyBlob");

		if (wrappedKeyBlob != null) {
			setWrappedKeyBlob(wrappedKeyBlob);
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
	public KeyEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the algorithm of this key entry.
	 *
	 * @return the algorithm of this key entry
	 */
	@Override
	public String getAlgorithm() {
		return model.getAlgorithm();
	}

	/**
	 * Returns the alias of this key entry.
	 *
	 * @return the alias of this key entry
	 */
	@Override
	public String getAlias() {
		return model.getAlias();
	}

	/**
	 * Returns the cipher spec of this key entry.
	 *
	 * @return the cipher spec of this key entry
	 */
	@Override
	public String getCipherSpec() {
		return model.getCipherSpec();
	}

	/**
	 * Returns the company ID of this key entry.
	 *
	 * @return the company ID of this key entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this key entry.
	 *
	 * @return the create date of this key entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the kek reference of this key entry.
	 *
	 * @return the kek reference of this key entry
	 */
	@Override
	public String getKekReference() {
		return model.getKekReference();
	}

	/**
	 * Returns the key entry ID of this key entry.
	 *
	 * @return the key entry ID of this key entry
	 */
	@Override
	public long getKeyEntryId() {
		return model.getKeyEntryId();
	}

	/**
	 * Returns the key type of this key entry.
	 *
	 * @return the key type of this key entry
	 */
	@Override
	public String getKeyType() {
		return model.getKeyType();
	}

	/**
	 * Returns the modified date of this key entry.
	 *
	 * @return the modified date of this key entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this key entry.
	 *
	 * @return the mvcc version of this key entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this key entry.
	 *
	 * @return the primary key of this key entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the wrapped key blob of this key entry.
	 *
	 * @return the wrapped key blob of this key entry
	 */
	@Override
	public Blob getWrappedKeyBlob() {
		return model.getWrappedKeyBlob();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the algorithm of this key entry.
	 *
	 * @param algorithm the algorithm of this key entry
	 */
	@Override
	public void setAlgorithm(String algorithm) {
		model.setAlgorithm(algorithm);
	}

	/**
	 * Sets the alias of this key entry.
	 *
	 * @param alias the alias of this key entry
	 */
	@Override
	public void setAlias(String alias) {
		model.setAlias(alias);
	}

	/**
	 * Sets the cipher spec of this key entry.
	 *
	 * @param cipherSpec the cipher spec of this key entry
	 */
	@Override
	public void setCipherSpec(String cipherSpec) {
		model.setCipherSpec(cipherSpec);
	}

	/**
	 * Sets the company ID of this key entry.
	 *
	 * @param companyId the company ID of this key entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this key entry.
	 *
	 * @param createDate the create date of this key entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the kek reference of this key entry.
	 *
	 * @param kekReference the kek reference of this key entry
	 */
	@Override
	public void setKekReference(String kekReference) {
		model.setKekReference(kekReference);
	}

	/**
	 * Sets the key entry ID of this key entry.
	 *
	 * @param keyEntryId the key entry ID of this key entry
	 */
	@Override
	public void setKeyEntryId(long keyEntryId) {
		model.setKeyEntryId(keyEntryId);
	}

	/**
	 * Sets the key type of this key entry.
	 *
	 * @param keyType the key type of this key entry
	 */
	@Override
	public void setKeyType(String keyType) {
		model.setKeyType(keyType);
	}

	/**
	 * Sets the modified date of this key entry.
	 *
	 * @param modifiedDate the modified date of this key entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this key entry.
	 *
	 * @param mvccVersion the mvcc version of this key entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this key entry.
	 *
	 * @param primaryKey the primary key of this key entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the wrapped key blob of this key entry.
	 *
	 * @param wrappedKeyBlob the wrapped key blob of this key entry
	 */
	@Override
	public void setWrappedKeyBlob(Blob wrappedKeyBlob) {
		model.setWrappedKeyBlob(wrappedKeyBlob);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected KeyEntryWrapper wrap(KeyEntry keyEntry) {
		return new KeyEntryWrapper(keyEntry);
	}

}