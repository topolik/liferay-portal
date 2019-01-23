/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.bemis.portal.twofa.device.manager.service.persistence.test;

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalServiceUtil;
import com.bemis.portal.twofa.device.manager.service.persistence.DeviceCodePersistence;
import com.bemis.portal.twofa.device.manager.service.persistence.DeviceCodeUtil;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class DeviceCodePersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DeviceCodeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DeviceCode> iterator = _deviceCodes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DeviceCode deviceCode = _persistence.create(pk);

		Assert.assertNotNull(deviceCode);

		Assert.assertEquals(deviceCode.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		_persistence.remove(newDeviceCode);

		DeviceCode existingDeviceCode = _persistence.fetchByPrimaryKey(newDeviceCode.getPrimaryKey());

		Assert.assertNull(existingDeviceCode);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDeviceCode();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DeviceCode newDeviceCode = _persistence.create(pk);

		newDeviceCode.setGroupId(RandomTestUtil.nextLong());

		newDeviceCode.setCompanyId(RandomTestUtil.nextLong());

		newDeviceCode.setUserId(RandomTestUtil.nextLong());

		newDeviceCode.setUserName(RandomTestUtil.randomString());

		newDeviceCode.setCreateDate(RandomTestUtil.nextDate());

		newDeviceCode.setModifiedDate(RandomTestUtil.nextDate());

		newDeviceCode.setPortalUserId(RandomTestUtil.nextLong());

		newDeviceCode.setPortalUserName(RandomTestUtil.randomString());

		newDeviceCode.setEmailAddress(RandomTestUtil.randomString());

		newDeviceCode.setDeviceCode(RandomTestUtil.randomString());

		newDeviceCode.setDeviceIP(RandomTestUtil.randomString());

		newDeviceCode.setValidationCode(RandomTestUtil.nextInt());

		_deviceCodes.add(_persistence.update(newDeviceCode));

		DeviceCode existingDeviceCode = _persistence.findByPrimaryKey(newDeviceCode.getPrimaryKey());

		Assert.assertEquals(existingDeviceCode.getDeviceCodeId(),
			newDeviceCode.getDeviceCodeId());
		Assert.assertEquals(existingDeviceCode.getGroupId(),
			newDeviceCode.getGroupId());
		Assert.assertEquals(existingDeviceCode.getCompanyId(),
			newDeviceCode.getCompanyId());
		Assert.assertEquals(existingDeviceCode.getUserId(),
			newDeviceCode.getUserId());
		Assert.assertEquals(existingDeviceCode.getUserName(),
			newDeviceCode.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingDeviceCode.getCreateDate()),
			Time.getShortTimestamp(newDeviceCode.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingDeviceCode.getModifiedDate()),
			Time.getShortTimestamp(newDeviceCode.getModifiedDate()));
		Assert.assertEquals(existingDeviceCode.getPortalUserId(),
			newDeviceCode.getPortalUserId());
		Assert.assertEquals(existingDeviceCode.getPortalUserName(),
			newDeviceCode.getPortalUserName());
		Assert.assertEquals(existingDeviceCode.getEmailAddress(),
			newDeviceCode.getEmailAddress());
		Assert.assertEquals(existingDeviceCode.getDeviceCode(),
			newDeviceCode.getDeviceCode());
		Assert.assertEquals(existingDeviceCode.getDeviceIP(),
			newDeviceCode.getDeviceIP());
		Assert.assertEquals(existingDeviceCode.getValidationCode(),
			newDeviceCode.getValidationCode());
	}

	@Test
	public void testCountByPortalUserId() throws Exception {
		_persistence.countByPortalUserId(RandomTestUtil.nextLong());

		_persistence.countByPortalUserId(0L);
	}

	@Test
	public void testCountByEmailAddress() throws Exception {
		_persistence.countByEmailAddress(StringPool.BLANK);

		_persistence.countByEmailAddress(StringPool.NULL);

		_persistence.countByEmailAddress((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		DeviceCode existingDeviceCode = _persistence.findByPrimaryKey(newDeviceCode.getPrimaryKey());

		Assert.assertEquals(existingDeviceCode, newDeviceCode);
	}

	@Test(expected = NoSuchDeviceCodeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<DeviceCode> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("Bemis_DeviceCode",
			"deviceCodeId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"portalUserId", true, "portalUserName", true, "emailAddress", true,
			"deviceCode", true, "deviceIP", true, "validationCode", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		DeviceCode existingDeviceCode = _persistence.fetchByPrimaryKey(newDeviceCode.getPrimaryKey());

		Assert.assertEquals(existingDeviceCode, newDeviceCode);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DeviceCode missingDeviceCode = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDeviceCode);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		DeviceCode newDeviceCode1 = addDeviceCode();
		DeviceCode newDeviceCode2 = addDeviceCode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDeviceCode1.getPrimaryKey());
		primaryKeys.add(newDeviceCode2.getPrimaryKey());

		Map<Serializable, DeviceCode> deviceCodes = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, deviceCodes.size());
		Assert.assertEquals(newDeviceCode1,
			deviceCodes.get(newDeviceCode1.getPrimaryKey()));
		Assert.assertEquals(newDeviceCode2,
			deviceCodes.get(newDeviceCode2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DeviceCode> deviceCodes = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(deviceCodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDeviceCode.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DeviceCode> deviceCodes = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, deviceCodes.size());
		Assert.assertEquals(newDeviceCode,
			deviceCodes.get(newDeviceCode.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DeviceCode> deviceCodes = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(deviceCodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDeviceCode.getPrimaryKey());

		Map<Serializable, DeviceCode> deviceCodes = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, deviceCodes.size());
		Assert.assertEquals(newDeviceCode,
			deviceCodes.get(newDeviceCode.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = DeviceCodeLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<DeviceCode>() {
				@Override
				public void performAction(DeviceCode deviceCode) {
					Assert.assertNotNull(deviceCode);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DeviceCode.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deviceCodeId",
				newDeviceCode.getDeviceCodeId()));

		List<DeviceCode> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		DeviceCode existingDeviceCode = result.get(0);

		Assert.assertEquals(existingDeviceCode, newDeviceCode);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DeviceCode.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deviceCodeId",
				RandomTestUtil.nextLong()));

		List<DeviceCode> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DeviceCode.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"deviceCodeId"));

		Object newDeviceCodeId = newDeviceCode.getDeviceCodeId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("deviceCodeId",
				new Object[] { newDeviceCodeId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDeviceCodeId = result.get(0);

		Assert.assertEquals(existingDeviceCodeId, newDeviceCodeId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DeviceCode.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"deviceCodeId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("deviceCodeId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DeviceCode newDeviceCode = addDeviceCode();

		_persistence.clearCache();

		DeviceCode existingDeviceCode = _persistence.findByPrimaryKey(newDeviceCode.getPrimaryKey());

		Assert.assertEquals(Long.valueOf(existingDeviceCode.getPortalUserId()),
			ReflectionTestUtil.<Long>invoke(existingDeviceCode,
				"getOriginalPortalUserId", new Class<?>[0]));

		Assert.assertTrue(Objects.equals(existingDeviceCode.getEmailAddress(),
				ReflectionTestUtil.invoke(existingDeviceCode,
					"getOriginalEmailAddress", new Class<?>[0])));
	}

	protected DeviceCode addDeviceCode() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DeviceCode deviceCode = _persistence.create(pk);

		deviceCode.setGroupId(RandomTestUtil.nextLong());

		deviceCode.setCompanyId(RandomTestUtil.nextLong());

		deviceCode.setUserId(RandomTestUtil.nextLong());

		deviceCode.setUserName(RandomTestUtil.randomString());

		deviceCode.setCreateDate(RandomTestUtil.nextDate());

		deviceCode.setModifiedDate(RandomTestUtil.nextDate());

		deviceCode.setPortalUserId(RandomTestUtil.nextLong());

		deviceCode.setPortalUserName(RandomTestUtil.randomString());

		deviceCode.setEmailAddress(RandomTestUtil.randomString());

		deviceCode.setDeviceCode(RandomTestUtil.randomString());

		deviceCode.setDeviceIP(RandomTestUtil.randomString());

		deviceCode.setValidationCode(RandomTestUtil.nextInt());

		_deviceCodes.add(_persistence.update(deviceCode));

		return deviceCode;
	}

	private List<DeviceCode> _deviceCodes = new ArrayList<DeviceCode>();
	private DeviceCodePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}