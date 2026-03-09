/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link KeyEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntryLocalService
 * @generated
 */
public class KeyEntryLocalServiceWrapper
	implements KeyEntryLocalService, ServiceWrapper<KeyEntryLocalService> {

	public KeyEntryLocalServiceWrapper() {
		this(null);
	}

	public KeyEntryLocalServiceWrapper(
		KeyEntryLocalService keyEntryLocalService) {

		_keyEntryLocalService = keyEntryLocalService;
	}

	/**
	 * Adds the key entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KeyEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param keyEntry the key entry
	 * @return the key entry that was added
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry addKeyEntry(
		com.liferay.keymanager.provider.db.model.KeyEntry keyEntry) {

		return _keyEntryLocalService.addKeyEntry(keyEntry);
	}

	/**
	 * Creates a new key entry with the primary key. Does not add the key entry to the database.
	 *
	 * @param keyEntryId the primary key for the new key entry
	 * @return the new key entry
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry createKeyEntry(
		long keyEntryId) {

		return _keyEntryLocalService.createKeyEntry(keyEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _keyEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the key entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KeyEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param keyEntry the key entry
	 * @return the key entry that was removed
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry deleteKeyEntry(
		com.liferay.keymanager.provider.db.model.KeyEntry keyEntry) {

		return _keyEntryLocalService.deleteKeyEntry(keyEntry);
	}

	/**
	 * Deletes the key entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KeyEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry that was removed
	 * @throws PortalException if a key entry with the primary key could not be found
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry deleteKeyEntry(
			long keyEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _keyEntryLocalService.deleteKeyEntry(keyEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _keyEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _keyEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _keyEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _keyEntryLocalService.dynamicQuery();
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

		return _keyEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.KeyEntryModelImpl</code>.
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

		return _keyEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.KeyEntryModelImpl</code>.
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

		return _keyEntryLocalService.dynamicQuery(
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

		return _keyEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _keyEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry fetchKeyEntry(
		long keyEntryId) {

		return _keyEntryLocalService.fetchKeyEntry(keyEntryId);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry fetchKeyEntry(
		long companyId, String alias) {

		return _keyEntryLocalService.fetchKeyEntry(companyId, alias);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _keyEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _keyEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the key entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.keymanager.provider.db.model.impl.KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @return the range of key entries
	 */
	@Override
	public java.util.List<com.liferay.keymanager.provider.db.model.KeyEntry>
		getKeyEntries(int start, int end) {

		return _keyEntryLocalService.getKeyEntries(start, end);
	}

	/**
	 * Returns the number of key entries.
	 *
	 * @return the number of key entries
	 */
	@Override
	public int getKeyEntriesCount() {
		return _keyEntryLocalService.getKeyEntriesCount();
	}

	/**
	 * Returns the key entry with the primary key.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry
	 * @throws PortalException if a key entry with the primary key could not be found
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry getKeyEntry(
			long keyEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _keyEntryLocalService.getKeyEntry(keyEntryId);
	}

	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry getKeyEntry(
			long companyId, String alias)
		throws Exception {

		return _keyEntryLocalService.getKeyEntry(companyId, alias);
	}

	@Override
	public java.util.List<String> getKeyIdentifiers(long companyId) {
		return _keyEntryLocalService.getKeyIdentifiers(companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _keyEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _keyEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public
		com.liferay.keymanager.provider.db.model.KeyEntryWrappedKeyBlobBlobModel
			getWrappedKeyBlobBlobModel(java.io.Serializable primaryKey) {

		return _keyEntryLocalService.getWrappedKeyBlobBlobModel(primaryKey);
	}

	@Override
	public java.io.InputStream openWrappedKeyBlobInputStream(long keyEntryId) {
		return _keyEntryLocalService.openWrappedKeyBlobInputStream(keyEntryId);
	}

	/**
	 * Updates the key entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KeyEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param keyEntry the key entry
	 * @return the key entry that was updated
	 */
	@Override
	public com.liferay.keymanager.provider.db.model.KeyEntry updateKeyEntry(
		com.liferay.keymanager.provider.db.model.KeyEntry keyEntry) {

		return _keyEntryLocalService.updateKeyEntry(keyEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _keyEntryLocalService.getBasePersistence();
	}

	@Override
	public KeyEntryLocalService getWrappedService() {
		return _keyEntryLocalService;
	}

	@Override
	public void setWrappedService(KeyEntryLocalService keyEntryLocalService) {
		_keyEntryLocalService = keyEntryLocalService;
	}

	private KeyEntryLocalService _keyEntryLocalService;

}