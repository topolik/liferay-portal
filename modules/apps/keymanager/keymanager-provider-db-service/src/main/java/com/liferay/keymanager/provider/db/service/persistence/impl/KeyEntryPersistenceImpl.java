/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence.impl;

import com.liferay.keymanager.provider.db.exception.NoSuchKeyEntryException;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.model.KeyEntryTable;
import com.liferay.keymanager.provider.db.model.impl.KeyEntryImpl;
import com.liferay.keymanager.provider.db.model.impl.KeyEntryModelImpl;
import com.liferay.keymanager.provider.db.service.persistence.KeyEntryPersistence;
import com.liferay.keymanager.provider.db.service.persistence.KeyEntryUtil;
import com.liferay.keymanager.provider.db.service.persistence.impl.constants.KeyManagerDBPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the key entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KeyEntryPersistence.class)
public class KeyEntryPersistenceImpl
	extends BasePersistenceImpl<KeyEntry> implements KeyEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KeyEntryUtil</code> to access the key entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KeyEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching key entries
	 */
	@Override
	public List<KeyEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<KeyEntry> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
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
	@Override
	public List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KeyEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
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
	@Override
	public List<KeyEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KeyEntry> orderByComparator, boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByCompanyId;
				finderArgs = new Object[] {companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCompanyId;
			finderArgs = new Object[] {
				companyId, start, end, orderByComparator
			};
		}

		List<KeyEntry> list = null;

		if (useFinderCache) {
			list = (List<KeyEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (KeyEntry keyEntry : list) {
					if (companyId != keyEntry.getCompanyId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_KEYENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(KeyEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<KeyEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	@Override
	public KeyEntry findByCompanyId_First(
			long companyId, OrderByComparator<KeyEntry> orderByComparator)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (keyEntry != null) {
			return keyEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchKeyEntryException(sb.toString());
	}

	/**
	 * Returns the first key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	@Override
	public KeyEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<KeyEntry> orderByComparator) {

		List<KeyEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	@Override
	public KeyEntry findByCompanyId_Last(
			long companyId, OrderByComparator<KeyEntry> orderByComparator)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = fetchByCompanyId_Last(companyId, orderByComparator);

		if (keyEntry != null) {
			return keyEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchKeyEntryException(sb.toString());
	}

	/**
	 * Returns the last key entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	@Override
	public KeyEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<KeyEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<KeyEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public KeyEntry[] findByCompanyId_PrevAndNext(
			long keyEntryId, long companyId,
			OrderByComparator<KeyEntry> orderByComparator)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = findByPrimaryKey(keyEntryId);

		Session session = null;

		try {
			session = openSession();

			KeyEntry[] array = new KeyEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, keyEntry, companyId, orderByComparator, true);

			array[1] = keyEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, keyEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KeyEntry getByCompanyId_PrevAndNext(
		Session session, KeyEntry keyEntry, long companyId,
		OrderByComparator<KeyEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_KEYENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(KeyEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(keyEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KeyEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the key entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (KeyEntry keyEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(keyEntry);
		}
	}

	/**
	 * Returns the number of key entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching key entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_KEYENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"keyEntry.companyId = ?";

	private FinderPath _finderPathFetchByC_A;

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry
	 * @throws NoSuchKeyEntryException if a matching key entry could not be found
	 */
	@Override
	public KeyEntry findByC_A(long companyId, String alias)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = fetchByC_A(companyId, alias);

		if (keyEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", alias=");
			sb.append(alias);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchKeyEntryException(sb.toString());
		}

		return keyEntry;
	}

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	@Override
	public KeyEntry fetchByC_A(long companyId, String alias) {
		return fetchByC_A(companyId, alias, true);
	}

	/**
	 * Returns the key entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching key entry, or <code>null</code> if a matching key entry could not be found
	 */
	@Override
	public KeyEntry fetchByC_A(
		long companyId, String alias, boolean useFinderCache) {

		alias = Objects.toString(alias, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, alias};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_A, finderArgs, this);
		}

		if (result instanceof KeyEntry) {
			KeyEntry keyEntry = (KeyEntry)result;

			if ((companyId != keyEntry.getCompanyId()) ||
				!Objects.equals(alias, keyEntry.getAlias())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_KEYENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			boolean bindAlias = false;

			if (alias.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_ALIAS_3);
			}
			else {
				bindAlias = true;

				sb.append(_FINDER_COLUMN_C_A_ALIAS_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAlias) {
					queryPos.add(alias);
				}

				List<KeyEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_A, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {companyId, alias};
							}

							_log.warn(
								"KeyEntryPersistenceImpl.fetchByC_A(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					KeyEntry keyEntry = list.get(0);

					result = keyEntry;

					cacheResult(keyEntry);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (KeyEntry)result;
		}
	}

	/**
	 * Removes the key entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the key entry that was removed
	 */
	@Override
	public KeyEntry removeByC_A(long companyId, String alias)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = findByC_A(companyId, alias);

		return remove(keyEntry);
	}

	/**
	 * Returns the number of key entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching key entries
	 */
	@Override
	public int countByC_A(long companyId, String alias) {
		KeyEntry keyEntry = fetchByC_A(companyId, alias);

		if (keyEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"keyEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ALIAS_2 =
		"keyEntry.alias = ?";

	private static final String _FINDER_COLUMN_C_A_ALIAS_3 =
		"(keyEntry.alias IS NULL OR keyEntry.alias = '')";

	public KeyEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("alias", "alias_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KeyEntry.class);

		setModelImplClass(KeyEntryImpl.class);
		setModelPKClass(long.class);

		setTable(KeyEntryTable.INSTANCE);
	}

	/**
	 * Caches the key entry in the entity cache if it is enabled.
	 *
	 * @param keyEntry the key entry
	 */
	@Override
	public void cacheResult(KeyEntry keyEntry) {
		entityCache.putResult(
			KeyEntryImpl.class, keyEntry.getPrimaryKey(), keyEntry);

		finderCache.putResult(
			_finderPathFetchByC_A,
			new Object[] {keyEntry.getCompanyId(), keyEntry.getAlias()},
			keyEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the key entries in the entity cache if it is enabled.
	 *
	 * @param keyEntries the key entries
	 */
	@Override
	public void cacheResult(List<KeyEntry> keyEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (keyEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (KeyEntry keyEntry : keyEntries) {
			if (entityCache.getResult(
					KeyEntryImpl.class, keyEntry.getPrimaryKey()) == null) {

				cacheResult(keyEntry);
			}
		}
	}

	/**
	 * Clears the cache for all key entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(KeyEntryImpl.class);

		finderCache.clearCache(KeyEntryImpl.class);
	}

	/**
	 * Clears the cache for the key entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(KeyEntry keyEntry) {
		entityCache.removeResult(KeyEntryImpl.class, keyEntry);
	}

	@Override
	public void clearCache(List<KeyEntry> keyEntries) {
		for (KeyEntry keyEntry : keyEntries) {
			entityCache.removeResult(KeyEntryImpl.class, keyEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(KeyEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(KeyEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		KeyEntryModelImpl keyEntryModelImpl) {

		Object[] args = new Object[] {
			keyEntryModelImpl.getCompanyId(), keyEntryModelImpl.getAlias()
		};

		finderCache.putResult(_finderPathFetchByC_A, args, keyEntryModelImpl);
	}

	/**
	 * Creates a new key entry with the primary key. Does not add the key entry to the database.
	 *
	 * @param keyEntryId the primary key for the new key entry
	 * @return the new key entry
	 */
	@Override
	public KeyEntry create(long keyEntryId) {
		KeyEntry keyEntry = new KeyEntryImpl();

		keyEntry.setNew(true);
		keyEntry.setPrimaryKey(keyEntryId);

		keyEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return keyEntry;
	}

	/**
	 * Removes the key entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry that was removed
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	@Override
	public KeyEntry remove(long keyEntryId) throws NoSuchKeyEntryException {
		return remove((Serializable)keyEntryId);
	}

	/**
	 * Removes the key entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the key entry
	 * @return the key entry that was removed
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	@Override
	public KeyEntry remove(Serializable primaryKey)
		throws NoSuchKeyEntryException {

		Session session = null;

		try {
			session = openSession();

			KeyEntry keyEntry = (KeyEntry)session.get(
				KeyEntryImpl.class, primaryKey);

			if (keyEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchKeyEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(keyEntry);
		}
		catch (NoSuchKeyEntryException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected KeyEntry removeImpl(KeyEntry keyEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(keyEntry)) {
				keyEntry = (KeyEntry)session.get(
					KeyEntryImpl.class, keyEntry.getPrimaryKeyObj());
			}

			if (keyEntry != null) {
				session.delete(keyEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (keyEntry != null) {
			clearCache(keyEntry);
		}

		return keyEntry;
	}

	@Override
	public KeyEntry updateImpl(KeyEntry keyEntry) {
		boolean isNew = keyEntry.isNew();

		if (!(keyEntry instanceof KeyEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(keyEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(keyEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in keyEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KeyEntry implementation " +
					keyEntry.getClass());
		}

		KeyEntryModelImpl keyEntryModelImpl = (KeyEntryModelImpl)keyEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (keyEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				keyEntry.setCreateDate(date);
			}
			else {
				keyEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!keyEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				keyEntry.setModifiedDate(date);
			}
			else {
				keyEntry.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(keyEntry);
			}
			else {
				session.evict(KeyEntryImpl.class, keyEntry.getPrimaryKeyObj());

				session.saveOrUpdate(keyEntry);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			KeyEntryImpl.class, keyEntryModelImpl, false, true);

		cacheUniqueFindersCache(keyEntryModelImpl);

		if (isNew) {
			keyEntry.setNew(false);
		}

		keyEntry.resetOriginalValues();

		return keyEntry;
	}

	/**
	 * Returns the key entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the key entry
	 * @return the key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	@Override
	public KeyEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchKeyEntryException {

		KeyEntry keyEntry = fetchByPrimaryKey(primaryKey);

		if (keyEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchKeyEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return keyEntry;
	}

	/**
	 * Returns the key entry with the primary key or throws a <code>NoSuchKeyEntryException</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry
	 * @throws NoSuchKeyEntryException if a key entry with the primary key could not be found
	 */
	@Override
	public KeyEntry findByPrimaryKey(long keyEntryId)
		throws NoSuchKeyEntryException {

		return findByPrimaryKey((Serializable)keyEntryId);
	}

	/**
	 * Returns the key entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param keyEntryId the primary key of the key entry
	 * @return the key entry, or <code>null</code> if a key entry with the primary key could not be found
	 */
	@Override
	public KeyEntry fetchByPrimaryKey(long keyEntryId) {
		return fetchByPrimaryKey((Serializable)keyEntryId);
	}

	/**
	 * Returns all the key entries.
	 *
	 * @return the key entries
	 */
	@Override
	public List<KeyEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<KeyEntry> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<KeyEntry> findAll(
		int start, int end, OrderByComparator<KeyEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<KeyEntry> findAll(
		int start, int end, OrderByComparator<KeyEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<KeyEntry> list = null;

		if (useFinderCache) {
			list = (List<KeyEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_KEYENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_KEYENTRY;

				sql = sql.concat(KeyEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<KeyEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the key entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (KeyEntry keyEntry : findAll()) {
			remove(keyEntry);
		}
	}

	/**
	 * Returns the number of key entries.
	 *
	 * @return the number of key entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_KEYENTRY);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "keyEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KEYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return KeyEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the key entry persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathFetchByC_A = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_A",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "alias_"}, true);

		KeyEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KeyEntryUtil.setPersistence(null);

		entityCache.removeCache(KeyEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = KeyManagerDBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KeyManagerDBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KeyManagerDBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_KEYENTRY =
		"SELECT keyEntry FROM KeyEntry keyEntry";

	private static final String _SQL_SELECT_KEYENTRY_WHERE =
		"SELECT keyEntry FROM KeyEntry keyEntry WHERE ";

	private static final String _SQL_COUNT_KEYENTRY =
		"SELECT COUNT(keyEntry) FROM KeyEntry keyEntry";

	private static final String _SQL_COUNT_KEYENTRY_WHERE =
		"SELECT COUNT(keyEntry) FROM KeyEntry keyEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "keyEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No KeyEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KeyEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KeyEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"alias"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}