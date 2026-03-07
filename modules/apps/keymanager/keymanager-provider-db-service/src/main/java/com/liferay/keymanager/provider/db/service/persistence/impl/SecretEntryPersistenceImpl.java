/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.service.persistence.impl;

import com.liferay.keymanager.provider.db.exception.NoSuchSecretEntryException;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.model.SecretEntryTable;
import com.liferay.keymanager.provider.db.model.impl.SecretEntryImpl;
import com.liferay.keymanager.provider.db.model.impl.SecretEntryModelImpl;
import com.liferay.keymanager.provider.db.service.persistence.SecretEntryPersistence;
import com.liferay.keymanager.provider.db.service.persistence.SecretEntryUtil;
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
 * The persistence implementation for the secret entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SecretEntryPersistence.class)
public class SecretEntryPersistenceImpl
	extends BasePersistenceImpl<SecretEntry> implements SecretEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SecretEntryUtil</code> to access the secret entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SecretEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByC_A;

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry
	 * @throws NoSuchSecretEntryException if a matching secret entry could not be found
	 */
	@Override
	public SecretEntry findByC_A(long companyId, String alias)
		throws NoSuchSecretEntryException {

		SecretEntry secretEntry = fetchByC_A(companyId, alias);

		if (secretEntry == null) {
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

			throw new NoSuchSecretEntryException(sb.toString());
		}

		return secretEntry;
	}

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	@Override
	public SecretEntry fetchByC_A(long companyId, String alias) {
		return fetchByC_A(companyId, alias, true);
	}

	/**
	 * Returns the secret entry where companyId = &#63; and alias = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching secret entry, or <code>null</code> if a matching secret entry could not be found
	 */
	@Override
	public SecretEntry fetchByC_A(
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

		if (result instanceof SecretEntry) {
			SecretEntry secretEntry = (SecretEntry)result;

			if ((companyId != secretEntry.getCompanyId()) ||
				!Objects.equals(alias, secretEntry.getAlias())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_SECRETENTRY_WHERE);

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

				List<SecretEntry> list = query.list();

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
								"SecretEntryPersistenceImpl.fetchByC_A(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					SecretEntry secretEntry = list.get(0);

					result = secretEntry;

					cacheResult(secretEntry);
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
			return (SecretEntry)result;
		}
	}

	/**
	 * Removes the secret entry where companyId = &#63; and alias = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the secret entry that was removed
	 */
	@Override
	public SecretEntry removeByC_A(long companyId, String alias)
		throws NoSuchSecretEntryException {

		SecretEntry secretEntry = findByC_A(companyId, alias);

		return remove(secretEntry);
	}

	/**
	 * Returns the number of secret entries where companyId = &#63; and alias = &#63;.
	 *
	 * @param companyId the company ID
	 * @param alias the alias
	 * @return the number of matching secret entries
	 */
	@Override
	public int countByC_A(long companyId, String alias) {
		SecretEntry secretEntry = fetchByC_A(companyId, alias);

		if (secretEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"secretEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ALIAS_2 =
		"secretEntry.alias = ?";

	private static final String _FINDER_COLUMN_C_A_ALIAS_3 =
		"(secretEntry.alias IS NULL OR secretEntry.alias = '')";

	public SecretEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("alias", "alias_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SecretEntry.class);

		setModelImplClass(SecretEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SecretEntryTable.INSTANCE);
	}

	/**
	 * Caches the secret entry in the entity cache if it is enabled.
	 *
	 * @param secretEntry the secret entry
	 */
	@Override
	public void cacheResult(SecretEntry secretEntry) {
		entityCache.putResult(
			SecretEntryImpl.class, secretEntry.getPrimaryKey(), secretEntry);

		finderCache.putResult(
			_finderPathFetchByC_A,
			new Object[] {secretEntry.getCompanyId(), secretEntry.getAlias()},
			secretEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the secret entries in the entity cache if it is enabled.
	 *
	 * @param secretEntries the secret entries
	 */
	@Override
	public void cacheResult(List<SecretEntry> secretEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (secretEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SecretEntry secretEntry : secretEntries) {
			if (entityCache.getResult(
					SecretEntryImpl.class, secretEntry.getPrimaryKey()) ==
						null) {

				cacheResult(secretEntry);
			}
		}
	}

	/**
	 * Clears the cache for all secret entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SecretEntryImpl.class);

		finderCache.clearCache(SecretEntryImpl.class);
	}

	/**
	 * Clears the cache for the secret entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SecretEntry secretEntry) {
		entityCache.removeResult(SecretEntryImpl.class, secretEntry);
	}

	@Override
	public void clearCache(List<SecretEntry> secretEntries) {
		for (SecretEntry secretEntry : secretEntries) {
			entityCache.removeResult(SecretEntryImpl.class, secretEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SecretEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SecretEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SecretEntryModelImpl secretEntryModelImpl) {

		Object[] args = new Object[] {
			secretEntryModelImpl.getCompanyId(), secretEntryModelImpl.getAlias()
		};

		finderCache.putResult(
			_finderPathFetchByC_A, args, secretEntryModelImpl);
	}

	/**
	 * Creates a new secret entry with the primary key. Does not add the secret entry to the database.
	 *
	 * @param secretEntryId the primary key for the new secret entry
	 * @return the new secret entry
	 */
	@Override
	public SecretEntry create(long secretEntryId) {
		SecretEntry secretEntry = new SecretEntryImpl();

		secretEntry.setNew(true);
		secretEntry.setPrimaryKey(secretEntryId);

		secretEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return secretEntry;
	}

	/**
	 * Removes the secret entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry that was removed
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	@Override
	public SecretEntry remove(long secretEntryId)
		throws NoSuchSecretEntryException {

		return remove((Serializable)secretEntryId);
	}

	/**
	 * Removes the secret entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the secret entry
	 * @return the secret entry that was removed
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	@Override
	public SecretEntry remove(Serializable primaryKey)
		throws NoSuchSecretEntryException {

		Session session = null;

		try {
			session = openSession();

			SecretEntry secretEntry = (SecretEntry)session.get(
				SecretEntryImpl.class, primaryKey);

			if (secretEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchSecretEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(secretEntry);
		}
		catch (NoSuchSecretEntryException noSuchEntityException) {
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
	protected SecretEntry removeImpl(SecretEntry secretEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(secretEntry)) {
				secretEntry = (SecretEntry)session.get(
					SecretEntryImpl.class, secretEntry.getPrimaryKeyObj());
			}

			if (secretEntry != null) {
				session.delete(secretEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (secretEntry != null) {
			clearCache(secretEntry);
		}

		return secretEntry;
	}

	@Override
	public SecretEntry updateImpl(SecretEntry secretEntry) {
		boolean isNew = secretEntry.isNew();

		if (!(secretEntry instanceof SecretEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(secretEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(secretEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in secretEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SecretEntry implementation " +
					secretEntry.getClass());
		}

		SecretEntryModelImpl secretEntryModelImpl =
			(SecretEntryModelImpl)secretEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (secretEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				secretEntry.setCreateDate(date);
			}
			else {
				secretEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!secretEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				secretEntry.setModifiedDate(date);
			}
			else {
				secretEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(secretEntry);
			}
			else {
				session.evict(
					SecretEntryImpl.class, secretEntry.getPrimaryKeyObj());

				session.saveOrUpdate(secretEntry);
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
			SecretEntryImpl.class, secretEntryModelImpl, false, true);

		cacheUniqueFindersCache(secretEntryModelImpl);

		if (isNew) {
			secretEntry.setNew(false);
		}

		secretEntry.resetOriginalValues();

		return secretEntry;
	}

	/**
	 * Returns the secret entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the secret entry
	 * @return the secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	@Override
	public SecretEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchSecretEntryException {

		SecretEntry secretEntry = fetchByPrimaryKey(primaryKey);

		if (secretEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchSecretEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return secretEntry;
	}

	/**
	 * Returns the secret entry with the primary key or throws a <code>NoSuchSecretEntryException</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry
	 * @throws NoSuchSecretEntryException if a secret entry with the primary key could not be found
	 */
	@Override
	public SecretEntry findByPrimaryKey(long secretEntryId)
		throws NoSuchSecretEntryException {

		return findByPrimaryKey((Serializable)secretEntryId);
	}

	/**
	 * Returns the secret entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param secretEntryId the primary key of the secret entry
	 * @return the secret entry, or <code>null</code> if a secret entry with the primary key could not be found
	 */
	@Override
	public SecretEntry fetchByPrimaryKey(long secretEntryId) {
		return fetchByPrimaryKey((Serializable)secretEntryId);
	}

	/**
	 * Returns all the secret entries.
	 *
	 * @return the secret entries
	 */
	@Override
	public List<SecretEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<SecretEntry> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<SecretEntry> findAll(
		int start, int end, OrderByComparator<SecretEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<SecretEntry> findAll(
		int start, int end, OrderByComparator<SecretEntry> orderByComparator,
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

		List<SecretEntry> list = null;

		if (useFinderCache) {
			list = (List<SecretEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_SECRETENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_SECRETENTRY;

				sql = sql.concat(SecretEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<SecretEntry>)QueryUtil.list(
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
	 * Removes all the secret entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SecretEntry secretEntry : findAll()) {
			remove(secretEntry);
		}
	}

	/**
	 * Returns the number of secret entries.
	 *
	 * @return the number of secret entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_SECRETENTRY);

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
		return "secretEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SECRETENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SecretEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the secret entry persistence.
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

		_finderPathFetchByC_A = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_A",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "alias_"}, true);

		SecretEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SecretEntryUtil.setPersistence(null);

		entityCache.removeCache(SecretEntryImpl.class.getName());
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

	private static final String _SQL_SELECT_SECRETENTRY =
		"SELECT secretEntry FROM SecretEntry secretEntry";

	private static final String _SQL_SELECT_SECRETENTRY_WHERE =
		"SELECT secretEntry FROM SecretEntry secretEntry WHERE ";

	private static final String _SQL_COUNT_SECRETENTRY =
		"SELECT COUNT(secretEntry) FROM SecretEntry secretEntry";

	private static final String _SQL_COUNT_SECRETENTRY_WHERE =
		"SELECT COUNT(secretEntry) FROM SecretEntry secretEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "secretEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SecretEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SecretEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SecretEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"alias"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}