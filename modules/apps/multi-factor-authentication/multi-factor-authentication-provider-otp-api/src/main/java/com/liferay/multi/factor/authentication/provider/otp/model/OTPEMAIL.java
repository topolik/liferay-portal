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
 * The extended model interface for the OTPEMAIL service. Represents a row in the &quot;OTPEMAIL&quot; database table, with each column mapped to a property of this class.
 *
 * @author arthurchan35
 * @see OTPEMAILModel
 * @see com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILImpl
 * @see com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILModelImpl
 * @generated
 */
@ImplementationClassName("com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILImpl")
@ProviderType
public interface OTPEMAIL extends OTPEMAILModel, PersistedModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<OTPEMAIL, Long> OTP_EMAIL_ID_ACCESSOR = new Accessor<OTPEMAIL, Long>() {
			@Override
			public Long get(OTPEMAIL otpemail) {
				return otpemail.getOtpEmailId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<OTPEMAIL> getTypeClass() {
				return OTPEMAIL.class;
			}
		};
}