/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence;

import com.liferay.keymanager.provider.db.exception.NoSuchSecretEntryException;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the secret entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntryUtil
 * @generated
 */
@ProviderType
public interface SecretEntryPersistence extends BasePersistence<SecretEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SecretEntryUtil} to access the secret entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the secret entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching secret entries
	 */
	public java.util.List<SecretEntry> findByCompanyId(long companyId);

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
	public java.util.List<SecretEntry> findByCompanyId(
		long companyId, int start, int end);

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
	public java.util.List<SecretEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator);

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
	public java.util.List<SecretEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public SecretEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
				orderByComparator)
		throws NoSuchSecretEntryException;

	/**
	 * Returns the first secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public SecretEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator);

	/**
	 * Returns the last secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public SecretEntry findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
				orderByComparator)
		throws NoSuchSecretEntryException;

	/**
	 * Returns the last secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public SecretEntry fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator);

	/**
	 * Returns the secret entries before and after the current secret entry in the ordered set where companyId = &#63;.
	 *
	 * @param secretEntryId the primary key of the current secret entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public SecretEntry[] findByCompanyId_PrevAndNext(
			long secretEntryId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
				orderByComparator)
		throws NoSuchSecretEntryException;

	/**
	 * Removes all the secret entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of secret entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching secret entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	public SecretEntry findByC_A(long companyId, String alias)
		throws NoSuchSecretEntryException;

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public SecretEntry fetchByC_A(long companyId, String alias);

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	public SecretEntry fetchByC_A(
		long companyId, String alias, boolean useFinderCache);

	/**
	 * Removes the secret entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the secret entry that was removed
	 */
	public SecretEntry removeByC_A(long companyId, String alias)
		throws NoSuchSecretEntryException;

	/**
	 * Returns the number of secret entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching secret entries
	 */
	public int countByC_A(long companyId, String alias);

	/**
	 * Caches the secret entry in the entity cache if it is enabled.
	 *
	 * @param secretEntry the secret entry
	 */
	public void cacheResult(SecretEntry secretEntry);

	/**
	 * Caches the secret entries in the entity cache if it is enabled.
	 *
	 * @param secretEntries the secret entries
	 */
	public void cacheResult(java.util.List<SecretEntry> secretEntries);

	/**
	 * Creates a new secret entry with the primary key. Does not add the secret entry to the database.
	 *
	 * @param secretEntryId the primary key for the new secret entry
	 * @return the new secret entry
	 */
	public SecretEntry create(long secretEntryId);

	/**
	 * Removes the secret entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry that was removed
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public SecretEntry remove(long secretEntryId)
		throws NoSuchSecretEntryException;

	public SecretEntry updateImpl(SecretEntry secretEntry);

	/**
	 * Returns the secret entry with the primary key or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	public SecretEntry findByPrimaryKey(long secretEntryId)
		throws NoSuchSecretEntryException;

	/**
	 * Returns the secret entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry, or <code>null</code> if a secret entry with the primary key could not be found
	 */
	public SecretEntry fetchByPrimaryKey(long secretEntryId);

	/**
	 * Returns all the secret entries.
	 *
	 * @return the secret entries
	 */
	public java.util.List<SecretEntry> findAll();

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
	public java.util.List<SecretEntry> findAll(int start, int end);

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
	public java.util.List<SecretEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator);

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
	public java.util.List<SecretEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SecretEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the secret entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of secret entries.
	 *
	 * @return the number of secret entries
	 */
	public int countAll();

}