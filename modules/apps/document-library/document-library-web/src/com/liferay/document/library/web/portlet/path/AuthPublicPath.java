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

package com.liferay.document.library.web.portlet.path;

import com.liferay.document.library.web.constants.DLPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	immediate = true,
	property = {
		"auth.public.path=/document_library/find_file_entry",
		"auth.public.path=/document_library/find_folder",
		"auth.public.path=/document_library/get_file",
		"auth.public.path=/image_gallery_display/find_folder",
		"auth.public.path=/image_gallery_display/find_image",
		"javax.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY
	},
	service = Object.class
)
public class AuthPublicPath {
}