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

package com.liferay.vulcan.sample.rest.internal.vulcan.representor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.representor.builder.RepresentorBuilder;

import java.text.DateFormat;

import java.util.function.Function;

import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	service = {ModelRepresentorMapper.class, PersonModelRepresentorMapper.class}
)
public class PersonModelRepresentorMapper
	implements ModelRepresentorMapper<User> {

	@Override
	public void buildRepresentor(RepresentorBuilder<User> representorBuilder) {
		Function<User, Object> birthDateFunction = user -> {
			try {
				DateFormat dateFormat = DateUtil.getISO8601Format();

				return dateFormat.format(user.getBirthday());
			}
			catch (PortalException pe) {
				throw new ServerErrorException(500, pe);
			}
		};

		representorBuilder.getFirstStep(
			user -> String.valueOf(user.getUserId())
		).addField(
			"additionalName", User::getMiddleName
		).addField(
			"birthDate", birthDateFunction
		).addField(
			"email", User::getEmailAddress
		).addField(
			"familyName", User::getLastName
		).addField(
			"givenName", User::getFirstName
		).addField(
			"name", User::getFullName
		).addField(
			"jobTitle", User::getJobTitle
		).addType(
			"Person"
		);
	}

}