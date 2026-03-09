/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence;

import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the key entry service. This utility wraps <code>com.liferay.keymanager.provider.db.service.persistence.impl.KeyEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntryPersistence
 * @generated
 */
public class KeyEntryUtil {

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
	public static void clearCache(KeyEntry keyEntry) {
		getPersistence().clearCache(keyEntry);
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
	public static Map<Serializable, KeyEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KeyEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KeyEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KeyEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KeyEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KeyEntry update(KeyEntry keyEntry) {
		return getPersistence().update(keyEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KeyEntry update(
		KeyEntry keyEntry, ServiceContext serviceContext) {

		return getPersistence().update(keyEntry, serviceContext);
	}

	/**
	 * Returns all the key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching key entries
	 */
	public static List<KeyEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the key entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @return the range of matching key entries
	 */
	public static List<KeyEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the key entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching key entries
	 */
	public static List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KeyEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the key entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching key entries
	 */
	public static List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KeyEntry> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public static KeyEntry findByCompanyId_First(
			long companyId, OrderByComparator<KeyEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public static KeyEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<KeyEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public static KeyEntry findByCompanyId_Last(
			long companyId, OrderByComparator<KeyEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public static KeyEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<KeyEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the key entries before and after the current key entry in the ordered set where companyId = &#63;.
	 *
	 * @param keyEntryId the primary key of the current key entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public static KeyEntry[] findByCompanyId_PrevAndNext(
			long keyEntryId, long companyId,
			OrderByComparator<KeyEntry> orderByComparator)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().findByCompanyId_PrevAndNext(
			keyEntryId, companyId, orderByComparator);
	}

	/**
	 * Removes all the key entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching key entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public static KeyEntry findByC_A(long companyId, String alias)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().findByC_A(companyId, alias);
	}

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public static KeyEntry fetchByC_A(long companyId, String alias) {
		return getPersistence().fetchByC_A(companyId, alias);
	}

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public static KeyEntry fetchByC_A(
		long companyId, String alias, boolean useFinderCache) {

		return getPersistence().fetchByC_A(companyId, alias, useFinderCache);
	}

	/**
	 * Removes the key entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the key entry that was removed
	 */
	public static KeyEntry removeByC_A(long companyId, String alias)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().removeByC_A(companyId, alias);
	}

	/**
	 * Returns the number of key entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching key entries
	 */
	public static int countByC_A(long companyId, String alias) {
		return getPersistence().countByC_A(companyId, alias);
	}

	/**
	 * Caches the key entry in the entity cache if it is enabled.
	 *
	 * @param keyEntry the key entry
	 */
	public static void cacheResult(KeyEntry keyEntry) {
		getPersistence().cacheResult(keyEntry);
	}

	/**
	 * Caches the key entries in the entity cache if it is enabled.
	 *
	 * @param keyEntries the key entries
	 */
	public static void cacheResult(List<KeyEntry> keyEntries) {
		getPersistence().cacheResult(keyEntries);
	}

	/**
	 * Creates a new key entry with the primary key. Does not add the key entry to the database.
	 *
	 * @param keyEntryId the primary key for the new key entry
	 * @return the new key entry
	 */
	public static KeyEntry create(long keyEntryId) {
		return getPersistence().create(keyEntryId);
	}

	/**
	 * Removes the key entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry that was removed
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public static KeyEntry remove(long keyEntryId)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().remove(keyEntryId);
	}

	public static KeyEntry updateImpl(KeyEntry keyEntry) {
		return getPersistence().updateImpl(keyEntry);
	}

	/**
	 * Returns the key entry with the primary key or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public static KeyEntry findByPrimaryKey(long keyEntryId)
		throws com.liferay.keymanager.provider.db.exception.
			NoSuchKeyEntryException {

		return getPersistence().findByPrimaryKey(keyEntryId);
	}

	/**
	 * Returns the key entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry, or <code>null</code> if a key entry with the primary key could not be found
	 */
	public static KeyEntry fetchByPrimaryKey(long keyEntryId) {
		return getPersistence().fetchByPrimaryKey(keyEntryId);
	}

	/**
	 * Returns all the key entries.
	 *
	 * @return the key entries
	 */
	public static List<KeyEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the key entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @return the range of key entries
	 */
	public static List<KeyEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the key entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of key entries
	 */
	public static List<KeyEntry> findAll(
		int start, int end, OrderByComparator<KeyEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the key entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KeyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of key entries
	 * @param end the upper bound of the range of key entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of key entries
	 */
	public static List<KeyEntry> findAll(
		int start, int end, OrderByComparator<KeyEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the key entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of key entries.
	 *
	 * @return the number of key entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static KeyEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(KeyEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile KeyEntryPersistence _persistence;

}