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

package com.liferay.multi.factor.authentication.provider.otp.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

/**
 * The extended model interface for the OTP service. Represents a row in the &quot;OTP&quot; database table, with each column mapped to a property of this class.
 *
 * @author arthurchan35
 * @see OTPModel
 * @see com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPImpl
 * @see com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPModelImpl
 * @generated
 */
@ImplementationClassName("com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPImpl")
@ProviderType
public interface OTP extends OTPModel, PersistedModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<OTP, Long> OTP_ID_ACCESSOR = new Accessor<OTP, Long>() {
			@Override
			public Long get(OTP otp) {
				return otp.getOtpId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<OTP> getTypeClass() {
				return OTP.class;
			}
		};
}