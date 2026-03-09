/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence;

import com.liferay.keymanager.provider.db.exception.NoSuchKeyEntryException;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the key entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntryUtil
 * @generated
 */
@ProviderType
public interface KeyEntryPersistence extends BasePersistence<KeyEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KeyEntryUtil} to access the key entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching key entries
	 */
	public java.util.List<KeyEntry> findByCompanyId(long companyId);

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
	public java.util.List<KeyEntry> findByCompanyId(
		long companyId, int start, int end);

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
	public java.util.List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator);

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
	public java.util.List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public KeyEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
				orderByComparator)
		throws NoSuchKeyEntryException;

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public KeyEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator);

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public KeyEntry findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
				orderByComparator)
		throws NoSuchKeyEntryException;

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public KeyEntry fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator);

	/**
	 * Returns the key entries before and after the current key entry in the ordered set where companyId = &#63;.
	 *
	 * @param keyEntryId the primary key of the current key entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public KeyEntry[] findByCompanyId_PrevAndNext(
			long keyEntryId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
				orderByComparator)
		throws NoSuchKeyEntryException;

	/**
	 * Removes all the key entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching key entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	public KeyEntry findByC_A(long companyId, String alias)
		throws NoSuchKeyEntryException;

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public KeyEntry fetchByC_A(long companyId, String alias);

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	public KeyEntry fetchByC_A(
		long companyId, String alias, boolean useFinderCache);

	/**
	 * Removes the key entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the key entry that was removed
	 */
	public KeyEntry removeByC_A(long companyId, String alias)
		throws NoSuchKeyEntryException;

	/**
	 * Returns the number of key entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching key entries
	 */
	public int countByC_A(long companyId, String alias);

	/**
	 * Caches the key entry in the entity cache if it is enabled.
	 *
	 * @param keyEntry the key entry
	 */
	public void cacheResult(KeyEntry keyEntry);

	/**
	 * Caches the key entries in the entity cache if it is enabled.
	 *
	 * @param keyEntries the key entries
	 */
	public void cacheResult(java.util.List<KeyEntry> keyEntries);

	/**
	 * Creates a new key entry with the primary key. Does not add the key entry to the database.
	 *
	 * @param keyEntryId the primary key for the new key entry
	 * @return the new key entry
	 */
	public KeyEntry create(long keyEntryId);

	/**
	 * Removes the key entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry that was removed
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public KeyEntry remove(long keyEntryId) throws NoSuchKeyEntryException;

	public KeyEntry updateImpl(KeyEntry keyEntry);

	/**
	 * Returns the key entry with the primary key or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	public KeyEntry findByPrimaryKey(long keyEntryId)
		throws NoSuchKeyEntryException;

	/**
	 * Returns the key entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry, or <code>null</code> if a key entry with the primary key could not be found
	 */
	public KeyEntry fetchByPrimaryKey(long keyEntryId);

	/**
	 * Returns all the key entries.
	 *
	 * @return the key entries
	 */
	public java.util.List<KeyEntry> findAll();

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
	public java.util.List<KeyEntry> findAll(int start, int end);

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
	public java.util.List<KeyEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator);

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
	public java.util.List<KeyEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KeyEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the key entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of key entries.
	 *
	 * @return the number of key entries
	 */
	public int countAll();

}