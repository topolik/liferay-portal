/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence;

import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the secret entry service. This utility wraps <code>com.liferay.keymanager.provider.db.service.persistence.impl.SecretEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntryPersistence
 * @generated
 */
public class SecretEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(SecretEntry secretEntry) {
		getPersistence().clearCache(secretEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, SecretEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SecretEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SecretEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SecretEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SecretEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SecretEntry update(SecretEntry secretEntry) {
		return getPersistence().update(secretEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SecretEntry update(
		SecretEntry secretEntry, ServiceContext serviceContext) {

		return getPersistence().update(secretEntry, serviceContext);
	}

	/**
	 * Returns all the secret entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching secret entries
	 */
	public static List<SecretEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the secret entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @return the range of matching secret entries
	 */
	public static List<SecretEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the secret entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching secret entries
	 */
	public static List<SecretEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SecretEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the secret entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching secret entries
	 */
	public static List<SecretEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SecretEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public static SecretEntry findByCompanyId_First(
			long companyId, OrderByComparator<SecretEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public static SecretEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<SecretEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public static SecretEntry findByCompanyId_Last(
			long companyId, OrderByComparator<SecretEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public static SecretEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<SecretEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the secret entries before and after the current secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param secretEntryId the primary key of the current secret entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public static SecretEntry[] findByCompanyId_PrevAndNext(
			long secretEntryId, long companyId,
			OrderByComparator<SecretEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().findByCompanyId_PrevAndNext(
			secretEntryId, companyId, orderByComparator);
	}

	/**
	 * Removes all the secret entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of secret entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching secret entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public static SecretEntry findByC_A(long companyId, String alias)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().findByC_A(companyId, alias);
	}

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public static SecretEntry fetchByC_A(long companyId, String alias) {
		return getPersistence().fetchByC_A(companyId, alias);
	}

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public static SecretEntry fetchByC_A(
		long companyId, String alias, boolean useFinderCache) {

		return getPersistence().fetchByC_A(companyId, alias, useFinderCache);
	}

	/**
	 * Removes the secret entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the secret entry that was removed
	 */
	public static SecretEntry removeByC_A(long companyId, String alias)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().removeByC_A(companyId, alias);
	}

	/**
	 * Returns the number of secret entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching secret entries
	 */
	public static int countByC_A(long companyId, String alias) {
		return getPersistence().countByC_A(companyId, alias);
	}

	/**
	 * Caches the secret entry in the entity cache if it is enabled.
	 *
	 * @param secretEntry the secret entry
	 */
	public static void cacheResult(SecretEntry secretEntry) {
		getPersistence().cacheResult(secretEntry);
	}

	/**
	 * Caches the secret entries in the entity cache if it is enabled.
	 *
	 * @param secretEntries the secret entries
	 */
	public static void cacheResult(List<SecretEntry> secretEntries) {
		getPersistence().cacheResult(secretEntries);
	}

	/**
	 * Creates a new secret entry with the primary key. Does not add the secret entry to the database.
	 *
	 * @param secretEntryId the primary key for the new secret entry
	 * @return the new secret entry
	 */
	public static SecretEntry create(long secretEntryId) {
		return getPersistence().create(secretEntryId);
	}

	/**
	 * Removes the secret entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry that was removed
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public static SecretEntry remove(long secretEntryId)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().remove(secretEntryId);
	}

	public static SecretEntry updateImpl(SecretEntry secretEntry) {
		return getPersistence().updateImpl(secretEntry);
	}

	/**
	 * Returns the secret entry with the primary key or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public static SecretEntry findByPrimaryKey(long secretEntryId)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchSecretEntryException {

		return getPersistence().findByPrimaryKey(secretEntryId);
	}

	/**
	 * Returns the secret entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry, or <code>null</code> if a secret entry with the primary key could not be found
	 */
	public static SecretEntry fetchByPrimaryKey(long secretEntryId) {
		return getPersistence().fetchByPrimaryKey(secretEntryId);
	}

	/**
	 * Returns all the secret entries.
	 *
	 * @return the secret entries
	 */
	public static List<SecretEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the secret entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @return the range of secret entries
	 */
	public static List<SecretEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the secret entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of secret entries
	 */
	public static List<SecretEntry> findAll(
		int start, int end, OrderByComparator<SecretEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the secret entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SecretEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of secret entries
	 * @param end the upper bound of the range of secret entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of secret entries
	 */
	public static List<SecretEntry> findAll(
		int start, int end, OrderByComparator<SecretEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the secret entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of secret entries.
	 *
	 * @return the number of secret entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SecretEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(SecretEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile SecretEntryPersistence _persistence;

}