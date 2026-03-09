/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SecretEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntryLocalService
 * @generated
 */
public class SecretEntryLocalServiceWrapper
	implements SecretEntryLocalService,
			   ServiceWrapper<SecretEntryLocalService> {

	public SecretEntryLocalServiceWrapper() {
		this(null);
	}

	public SecretEntryLocalServiceWrapper(
		SecretEntryLocalService secretEntryLocalService) {

		_secretEntryLocalService = secretEntryLocalService;
	}

	/**
	 * Adds the secret entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SecretEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param secretEntry the secret entry
	 * @return the secret entry that was added
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry addSecretEntry(
		com.liferay.keymanager.provider.db.model.SecretEntry secretEntry) {

		return _secretEntryLocalService.addSecretEntry(secretEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _secretEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new secret entry with the primary key. Does not add the secret entry to the database.
	 *
	 * @param secretEntryId the primary key for the new secret entry
	 * @return the new secret entry
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
		createSecretEntry(long secretEntryId) {

		return _secretEntryLocalService.createSecretEntry(secretEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _secretEntryLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the secret entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SecretEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry that was removed
	 * @throws PortalException if a secret entry with the primary key could not be found
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
			deleteSecretEntry(long secretEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _secretEntryLocalService.deleteSecretEntry(secretEntryId);
	}

	/**
	 * Deletes the secret entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SecretEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param secretEntry the secret entry
	 * @return the secret entry that was removed
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
		deleteSecretEntry(
			com.liferay.keymanager.provider.db.model.SecretEntry secretEntry) {

		return _secretEntryLocalService.deleteSecretEntry(secretEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _secretEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _secretEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _secretEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _secretEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _secretEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _secretEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _secretEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _secretEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
		fetchSecretEntry(long secretEntryId) {

		return _secretEntryLocalService.fetchSecretEntry(secretEntryId);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
		fetchSecretEntry(long companyId, String alias) {

		return _secretEntryLocalService.fetchSecretEntry(companyId, alias);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _secretEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public
		com.liferay.keymanager.provider.db.model.
			SecretEntryCiphertextBlobBlobModel getCiphertextBlobBlobModel(
				java.io.Serializable primaryKey) {

		return _secretEntryLocalService.getCiphertextBlobBlobModel(primaryKey);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.
		SecretEntryEncryptedDEKBlobBlobModel getEncryptedDEKBlobBlobModel(
			java.io.Serializable primaryKey) {

		return _secretEntryLocalService.getEncryptedDEKBlobBlobModel(
			primaryKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _secretEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _secretEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _secretEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the secret entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @return the range of secret entries
	 */
	@Override
	public java.util.List<com.liferay.keymanager.provider.db.model.SecretEntry>
		getSecretEntries(int start, int end) {

		return _secretEntryLocalService.getSecretEntries(start, end);
	}

	/**
	 * Returns the number of secret entries.
	 *
	 * @return the number of secret entries
	 */
	@Override
	public int getSecretEntriesCount() {
		return _secretEntryLocalService.getSecretEntriesCount();
	}

	/**
	 * Returns the secret entry with the primary key.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry
	 * @throws PortalException if a secret entry with the primary key could not be found
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry getSecretEntry(
			long secretEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _secretEntryLocalService.getSecretEntry(secretEntryId);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry getSecretEntry(
			long companyId, String alias)
		throws Exception {

		return _secretEntryLocalService.getSecretEntry(companyId, alias);
	}

	@Override
	public java.util.List<String> getSecretIdentifiers(long companyId) {
		return _secretEntryLocalService.getSecretIdentifiers(companyId);
	}

	@Override
	public java.io.InputStream openCiphertextBlobInputStream(
		long secretEntryId) {

		return _secretEntryLocalService.openCiphertextBlobInputStream(
			secretEntryId);
	}

	@Override
	public java.io.InputStream openEncryptedDEKBlobInputStream(
		long secretEntryId) {

		return _secretEntryLocalService.openEncryptedDEKBlobInputStream(
			secretEntryId);
	}

	/**
	 * Updates the secret entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SecretEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param secretEntry the secret entry
	 * @return the secret entry that was updated
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.SecretEntry
		updateSecretEntry(
			com.liferay.keymanager.provider.db.model.SecretEntry secretEntry) {

		return _secretEntryLocalService.updateSecretEntry(secretEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _secretEntryLocalService.getBasePersistence();
	}

	@Override
	public SecretEntryLocalService getWrappedService() {
		return _secretEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SecretEntryLocalService secretEntryLocalService) {

		_secretEntryLocalService = secretEntryLocalService;
	}

	private SecretEntryLocalService _secretEntryLocalService;

}