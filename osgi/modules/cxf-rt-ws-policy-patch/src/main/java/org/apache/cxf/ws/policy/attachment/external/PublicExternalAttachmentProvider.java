/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package org.apache.cxf.ws.policy.attachment.external;

import org.apache.cxf.Bus;
import org.springframework.core.io.UrlResource;

import java.net.URL;

/**
 * @author Tomas Polesovsky
 */
public class PublicExternalAttachmentProvider extends ExternalAttachmentProvider {
	public PublicExternalAttachmentProvider() {
	}

	public PublicExternalAttachmentProvider(Bus b) {
		super(b);
	}

	public void setLocation(URL location) {
		super.setLocation(new UrlResource(location));
	}
}
