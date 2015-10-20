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

package com.liferay.message.boards.web.portlet.path;

import com.liferay.message.boards.web.constants.MBPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	immediate = true,
	property = {
		"auth.public.path=/message_boards/find_category",
		"auth.public.path=/message_boards/find_message",
		"auth.public.path=/message_boards/find_thread",
		"auth.public.path=/message_boards/get_message_attachment",
		"auth.public.path=/message_boards/rss",
		"auth.token.ignore.actions=/message_boards/rss",
		"javax.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS
	},
	service = Object.class
)
public class AuthPublicPath {
}