/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service;

import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SecretEntry. This utility wraps
 * <code>com.liferay.keymanager.provider.db.service.impl.SecretEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntryLocalService
 * @generated
 */
public class SecretEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.keymanager.provider.db.service.impl.SecretEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static SecretEntry addSecretEntry(SecretEntry secretEntry) {
		return getService().addSecretEntry(secretEntry);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new secret entry with the primary key. Does not add the secret entry to the database.
	 *
	 * @param secretEntryId the primary key for the new secret entry
	 * @return the new secret entry
	 */
	public static SecretEntry createSecretEntry(long secretEntryId) {
		return getService().createSecretEntry(secretEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	public static SecretEntry deleteSecretEntry(long secretEntryId)
		throws PortalException {

		return getService().deleteSecretEntry(secretEntryId);
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
	public static SecretEntry deleteSecretEntry(SecretEntry secretEntry) {
		return getService().deleteSecretEntry(secretEntry);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SecretEntry fetchSecretEntry(long secretEntryId) {
		return getService().fetchSecretEntry(secretEntryId);
	}

	public static SecretEntry fetchSecretEntry(long companyId, String alias) {
		return getService().fetchSecretEntry(companyId, alias);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.keymanager.provider.db.model.
			SecretEntryCiphertextBlobBlobModel getCiphertextBlobBlobModel(
				Serializable primaryKey) {

		return getService().getCiphertextBlobBlobModel(primaryKey);
	}

	public static com.liferay.keymanager.provider.db.model.
		SecretEntryEncryptedDEKBlobBlobModel getEncryptedDEKBlobBlobModel(
			Serializable primaryKey) {

		return getService().getEncryptedDEKBlobBlobModel(primaryKey);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static List<SecretEntry> getSecretEntries(int start, int end) {
		return getService().getSecretEntries(start, end);
	}

	/**
	 * Returns the number of secret entries.
	 *
	 * @return the number of secret entries
	 */
	public static int getSecretEntriesCount() {
		return getService().getSecretEntriesCount();
	}

	/**
	 * Returns the secret entry with the primary key.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry
	 * @throws PortalException if a secret entry with the primary key could not be found
	 */
	public static SecretEntry getSecretEntry(long secretEntryId)
		throws PortalException {

		return getService().getSecretEntry(secretEntryId);
	}

	public static SecretEntry getSecretEntry(long companyId, String alias)
		throws Exception {

		return getService().getSecretEntry(companyId, alias);
	}

	public static InputStream openCiphertextBlobInputStream(
		long secretEntryId) {

		return getService().openCiphertextBlobInputStream(secretEntryId);
	}

	public static InputStream openEncryptedDEKBlobInputStream(
		long secretEntryId) {

		return getService().openEncryptedDEKBlobInputStream(secretEntryId);
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
	public static SecretEntry updateSecretEntry(SecretEntry secretEntry) {
		return getService().updateSecretEntry(secretEntry);
	}

	public static SecretEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SecretEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			SecretEntryLocalServiceUtil.class, SecretEntryLocalService.class);

}