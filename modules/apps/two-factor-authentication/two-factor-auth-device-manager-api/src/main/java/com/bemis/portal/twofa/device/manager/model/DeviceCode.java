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
 * The extended model interface for the DeviceCode service. Represents a row in the &quot;Bemis_DeviceCode&quot; database table, with each column mapped to a property of this class.
 *
 * @author Prathima Shreenath
 * @see DeviceCodeModel
 * @see com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeImpl
 * @see com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeModelImpl
 * @generated
 */
@ImplementationClassName("com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeImpl")
@ProviderType
public interface DeviceCode extends DeviceCodeModel, PersistedModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to {@link com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<DeviceCode, Long> DEVICE_CODE_ID_ACCESSOR = new Accessor<DeviceCode, Long>() {
			@Override
			public Long get(DeviceCode deviceCode) {
				return deviceCode.getDeviceCodeId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<DeviceCode> getTypeClass() {
				return DeviceCode.class;
			}
		};
}