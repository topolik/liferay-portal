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

package com.bemis.portal.twofa.device.manager.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

/**
 * The extended model interface for the Device service. Represents a row in the &quot;Bemis_Device&quot; database table, with each column mapped to a property of this class.
 *
 * @author Prathima Shreenath
 * @see DeviceModel
 * @see com.bemis.portal.twofa.device.manager.model.impl.DeviceImpl
 * @see com.bemis.portal.twofa.device.manager.model.impl.DeviceModelImpl
 * @generated
 */
@ImplementationClassName("com.bemis.portal.twofa.device.manager.model.impl.DeviceImpl")
@ProviderType
public interface Device extends DeviceModel, PersistedModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to {@link com.bemis.portal.twofa.device.manager.model.impl.DeviceImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<Device, Long> DEVICE_ID_ACCESSOR = new Accessor<Device, Long>() {
			@Override
			public Long get(Device device) {
				return device.getDeviceId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Device> getTypeClass() {
				return Device.class;
			}
		};
}