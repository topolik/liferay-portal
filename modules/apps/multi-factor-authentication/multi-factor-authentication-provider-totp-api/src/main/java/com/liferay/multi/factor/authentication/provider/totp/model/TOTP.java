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

package com.liferay.multi.factor.authentication.provider.totp.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

/**
 * The extended model interface for the TOTP service. Represents a row in the &quot;TOTP&quot; database table, with each column mapped to a property of this class.
 *
 * @author arthurchan35
 * @see TOTPModel
 * @see com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPImpl
 * @see com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl
 * @generated
 */
@ImplementationClassName("com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPImpl")
@ProviderType
public interface TOTP extends TOTPModel, PersistedModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<TOTP, Long> TOTP_ID_ACCESSOR = new Accessor<TOTP, Long>() {
			@Override
			public Long get(TOTP totp) {
				return totp.getTotpId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<TOTP> getTypeClass() {
				return TOTP.class;
			}
		};
}