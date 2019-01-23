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

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException;
import com.bemis.portal.twofa.device.manager.model.Device;
import com.bemis.portal.twofa.device.manager.service.DeviceLocalServiceUtil;
import com.bemis.portal.twofa.device.manager.service.persistence.DevicePersistence;
import com.bemis.portal.twofa.device.manager.service.persistence.DeviceUtil;

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
public class DevicePersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DeviceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Device> iterator = _devices.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Device device = _persistence.create(pk);

		Assert.assertNotNull(device);

		Assert.assertEquals(device.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Device newDevice = addDevice();

		_persistence.remove(newDevice);

		Device existingDevice = _persistence.fetchByPrimaryKey(newDevice.getPrimaryKey());

		Assert.assertNull(existingDevice);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDevice();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Device newDevice = _persistence.create(pk);

		newDevice.setGroupId(RandomTestUtil.nextLong());

		newDevice.setCompanyId(RandomTestUtil.nextLong());

		newDevice.setUserId(RandomTestUtil.nextLong());

		newDevice.setUserName(RandomTestUtil.randomString());

		newDevice.setCreateDate(RandomTestUtil.nextDate());

		newDevice.setModifiedDate(RandomTestUtil.nextDate());

		newDevice.setPortalUserId(RandomTestUtil.nextLong());

		newDevice.setPortalUserName(RandomTestUtil.randomString());

		newDevice.setEmailAddress(RandomTestUtil.randomString());

		newDevice.setDeviceIP(RandomTestUtil.randomString());

		newDevice.setBrowserName(RandomTestUtil.randomString());

		newDevice.setOsName(RandomTestUtil.randomString());

		newDevice.setVerified(RandomTestUtil.randomBoolean());

		newDevice.setTempDevice(RandomTestUtil.randomBoolean());

		_devices.add(_persistence.update(newDevice));

		Device existingDevice = _persistence.findByPrimaryKey(newDevice.getPrimaryKey());

		Assert.assertEquals(existingDevice.getDeviceId(),
			newDevice.getDeviceId());
		Assert.assertEquals(existingDevice.getGroupId(), newDevice.getGroupId());
		Assert.assertEquals(existingDevice.getCompanyId(),
			newDevice.getCompanyId());
		Assert.assertEquals(existingDevice.getUserId(), newDevice.getUserId());
		Assert.assertEquals(existingDevice.getUserName(),
			newDevice.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingDevice.getCreateDate()),
			Time.getShortTimestamp(newDevice.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingDevice.getModifiedDate()),
			Time.getShortTimestamp(newDevice.getModifiedDate()));
		Assert.assertEquals(existingDevice.getPortalUserId(),
			newDevice.getPortalUserId());
		Assert.assertEquals(existingDevice.getPortalUserName(),
			newDevice.getPortalUserName());
		Assert.assertEquals(existingDevice.getEmailAddress(),
			newDevice.getEmailAddress());
		Assert.assertEquals(existingDevice.getDeviceIP(),
			newDevice.getDeviceIP());
		Assert.assertEquals(existingDevice.getBrowserName(),
			newDevice.getBrowserName());
		Assert.assertEquals(existingDevice.getOsName(), newDevice.getOsName());
		Assert.assertEquals(existingDevice.getVerified(),
			newDevice.getVerified());
		Assert.assertEquals(existingDevice.getTempDevice(),
			newDevice.getTempDevice());
	}

	@Test
	public void testCountByPortalUserId() throws Exception {
		_persistence.countByPortalUserId(RandomTestUtil.nextLong());

		_persistence.countByPortalUserId(0L);
	}

	@Test
	public void testCountByPortalUserId_DeviceIP() throws Exception {
		_persistence.countByPortalUserId_DeviceIP(RandomTestUtil.nextLong(),
			StringPool.BLANK);

		_persistence.countByPortalUserId_DeviceIP(0L, StringPool.NULL);

		_persistence.countByPortalUserId_DeviceIP(0L, (String)null);
	}

	@Test
	public void testCountByVerified_PortalUserId() throws Exception {
		_persistence.countByVerified_PortalUserId(RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByVerified_PortalUserId(RandomTestUtil.randomBoolean(),
			0L);
	}

	@Test
	public void testCountByVerified_PortalUserId_DeviceIP()
		throws Exception {
		_persistence.countByVerified_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), StringPool.BLANK);

		_persistence.countByVerified_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			0L, StringPool.NULL);

		_persistence.countByVerified_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			0L, (String)null);
	}

	@Test
	public void testCountByTempDevice_PortalUserId_DeviceIP()
		throws Exception {
		_persistence.countByTempDevice_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), StringPool.BLANK);

		_persistence.countByTempDevice_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			0L, StringPool.NULL);

		_persistence.countByTempDevice_PortalUserId_DeviceIP(RandomTestUtil.randomBoolean(),
			0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Device newDevice = addDevice();

		Device existingDevice = _persistence.findByPrimaryKey(newDevice.getPrimaryKey());

		Assert.assertEquals(existingDevice, newDevice);
	}

	@Test(expected = NoSuchDeviceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<Device> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("Bemis_Device", "deviceId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"portalUserId", true, "portalUserName", true, "emailAddress", true,
			"deviceIP", true, "browserName", true, "osName", true, "verified",
			true, "tempDevice", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Device newDevice = addDevice();

		Device existingDevice = _persistence.fetchByPrimaryKey(newDevice.getPrimaryKey());

		Assert.assertEquals(existingDevice, newDevice);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Device missingDevice = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDevice);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		Device newDevice1 = addDevice();
		Device newDevice2 = addDevice();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDevice1.getPrimaryKey());
		primaryKeys.add(newDevice2.getPrimaryKey());

		Map<Serializable, Device> devices = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, devices.size());
		Assert.assertEquals(newDevice1, devices.get(newDevice1.getPrimaryKey()));
		Assert.assertEquals(newDevice2, devices.get(newDevice2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Device> devices = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(devices.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		Device newDevice = addDevice();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDevice.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Device> devices = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, devices.size());
		Assert.assertEquals(newDevice, devices.get(newDevice.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Device> devices = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(devices.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		Device newDevice = addDevice();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDevice.getPrimaryKey());

		Map<Serializable, Device> devices = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, devices.size());
		Assert.assertEquals(newDevice, devices.get(newDevice.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = DeviceLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Device>() {
				@Override
				public void performAction(Device device) {
					Assert.assertNotNull(device);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		Device newDevice = addDevice();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Device.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deviceId",
				newDevice.getDeviceId()));

		List<Device> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Device existingDevice = result.get(0);

		Assert.assertEquals(existingDevice, newDevice);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Device.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deviceId",
				RandomTestUtil.nextLong()));

		List<Device> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		Device newDevice = addDevice();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Device.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("deviceId"));

		Object newDeviceId = newDevice.getDeviceId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("deviceId",
				new Object[] { newDeviceId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDeviceId = result.get(0);

		Assert.assertEquals(existingDeviceId, newDeviceId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Device.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("deviceId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("deviceId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Device newDevice = addDevice();

		_persistence.clearCache();

		Device existingDevice = _persistence.findByPrimaryKey(newDevice.getPrimaryKey());

		Assert.assertEquals(Long.valueOf(existingDevice.getPortalUserId()),
			ReflectionTestUtil.<Long>invoke(existingDevice,
				"getOriginalPortalUserId", new Class<?>[0]));
		Assert.assertTrue(Objects.equals(existingDevice.getDeviceIP(),
				ReflectionTestUtil.invoke(existingDevice,
					"getOriginalDeviceIP", new Class<?>[0])));

		Assert.assertEquals(Boolean.valueOf(existingDevice.getVerified()),
			ReflectionTestUtil.<Boolean>invoke(existingDevice,
				"getOriginalVerified", new Class<?>[0]));
		Assert.assertEquals(Long.valueOf(existingDevice.getPortalUserId()),
			ReflectionTestUtil.<Long>invoke(existingDevice,
				"getOriginalPortalUserId", new Class<?>[0]));
		Assert.assertTrue(Objects.equals(existingDevice.getDeviceIP(),
				ReflectionTestUtil.invoke(existingDevice,
					"getOriginalDeviceIP", new Class<?>[0])));

		Assert.assertEquals(Boolean.valueOf(existingDevice.getTempDevice()),
			ReflectionTestUtil.<Boolean>invoke(existingDevice,
				"getOriginalTempDevice", new Class<?>[0]));
		Assert.assertEquals(Long.valueOf(existingDevice.getPortalUserId()),
			ReflectionTestUtil.<Long>invoke(existingDevice,
				"getOriginalPortalUserId", new Class<?>[0]));
		Assert.assertTrue(Objects.equals(existingDevice.getDeviceIP(),
				ReflectionTestUtil.invoke(existingDevice,
					"getOriginalDeviceIP", new Class<?>[0])));
	}

	protected Device addDevice() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Device device = _persistence.create(pk);

		device.setGroupId(RandomTestUtil.nextLong());

		device.setCompanyId(RandomTestUtil.nextLong());

		device.setUserId(RandomTestUtil.nextLong());

		device.setUserName(RandomTestUtil.randomString());

		device.setCreateDate(RandomTestUtil.nextDate());

		device.setModifiedDate(RandomTestUtil.nextDate());

		device.setPortalUserId(RandomTestUtil.nextLong());

		device.setPortalUserName(RandomTestUtil.randomString());

		device.setEmailAddress(RandomTestUtil.randomString());

		device.setDeviceIP(RandomTestUtil.randomString());

		device.setBrowserName(RandomTestUtil.randomString());

		device.setOsName(RandomTestUtil.randomString());

		device.setVerified(RandomTestUtil.randomBoolean());

		device.setTempDevice(RandomTestUtil.randomBoolean());

		_devices.add(_persistence.update(device));

		return device;
	}

	private List<Device> _devices = new ArrayList<Device>();
	private DevicePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}