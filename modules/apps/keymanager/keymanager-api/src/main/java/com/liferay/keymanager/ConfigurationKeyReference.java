/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class ConfigurationKeyReference extends KeyReference {

	public static boolean isConfigurationKeyReference(String value) {
		if (value == null) {
			return false;
		}

		KeyReference keyReference = KeyReference.fromString(value);
		
		if (keyReference == null) {
			return false;
		}
		
		Matcher matcher = _pattern.matcher(value);

		if (!matcher.matches()) {
			return false;
		}
		
		return true;
	}

	public static ConfigurationKeyReference fromString(String value) {
		if (value == null) {
			return null;
		}
		
		KeyReference keyReference = KeyReference.fromString(value);

		if (keyReference == null) {
			return null;
		}

		Matcher matcher = _pattern.matcher(value);

		if (!matcher.matches()) {
			return null;
		}

		String pid = matcher.group(1);
		long companyId = GetterUtil.getLong(matcher.group(2));
		long groupId = GetterUtil.getLong(matcher.group(3));
		String key = matcher.group(4);

		return new ConfigurationKeyReference(pid, key, companyId, groupId);
	}

	public ConfigurationKeyReference(String pid, String key) {
		this(pid, key, CompanyConstants.SYSTEM);
	}

	public ConfigurationKeyReference(String pid, String key, long companyId) {
		this(pid, key, companyId, GroupConstants.DEFAULT_PARENT_GROUP_ID);
	}

	public ConfigurationKeyReference(String pid, String key, long companyId, long groupId) {
		this(KeyReference.Type.SECRET, ANY_PROVIDER, pid, key, companyId, groupId);
	}

	public ConfigurationKeyReference(Type type, String provider, String pid, String key, long companyId, long groupId) {
		super(type, provider, StringBundler.concat(
			"config:", pid, ":", Long.toString(companyId), ":", Long.toString(groupId), ":", key));

		this._pid = pid;
		this._key = key;
		this._companyId = companyId;
		this._groupId = groupId;
	}
	
	private static final Pattern _pattern = Pattern.compile(
		"^config:([^:]+):([^:]+):([^:]+):([^:]+)$");

	private static final long serialVersionUID = 1L;

	public String getPid() {
		return _pid;
	}
	
	public String getKey() {
		return _key;
	}
	
	public long getCompanyId() {
		return _companyId;
	}
	
	public long getGroupId() {
		return _groupId;
	}
	
	private final String _pid;
	private final String _key;
	private final long _companyId;
	private final long _groupId;
}