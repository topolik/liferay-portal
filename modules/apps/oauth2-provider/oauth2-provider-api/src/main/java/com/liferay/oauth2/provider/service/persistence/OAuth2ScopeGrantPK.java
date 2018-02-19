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

package com.liferay.oauth2.provider.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class OAuth2ScopeGrantPK implements Comparable<OAuth2ScopeGrantPK>,
	Serializable {
	public String applicationName;
	public String bundleSymbolicName;
	public String bundleVersion;
	public long companyId;
	public String oAuth2ScopeName;
	public String oAuth2TokenId;

	public OAuth2ScopeGrantPK() {
	}

	public OAuth2ScopeGrantPK(String applicationName,
		String bundleSymbolicName, String bundleVersion, long companyId,
		String oAuth2ScopeName, String oAuth2TokenId) {
		this.applicationName = applicationName;
		this.bundleSymbolicName = bundleSymbolicName;
		this.bundleVersion = bundleVersion;
		this.companyId = companyId;
		this.oAuth2ScopeName = oAuth2ScopeName;
		this.oAuth2TokenId = oAuth2TokenId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String bundleVersion) {
		this.bundleVersion = bundleVersion;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getOAuth2ScopeName() {
		return oAuth2ScopeName;
	}

	public void setOAuth2ScopeName(String oAuth2ScopeName) {
		this.oAuth2ScopeName = oAuth2ScopeName;
	}

	public String getOAuth2TokenId() {
		return oAuth2TokenId;
	}

	public void setOAuth2TokenId(String oAuth2TokenId) {
		this.oAuth2TokenId = oAuth2TokenId;
	}

	@Override
	public int compareTo(OAuth2ScopeGrantPK pk) {
		if (pk == null) {
			return -1;
		}

		int value = 0;

		value = applicationName.compareTo(pk.applicationName);

		if (value != 0) {
			return value;
		}

		value = bundleSymbolicName.compareTo(pk.bundleSymbolicName);

		if (value != 0) {
			return value;
		}

		value = bundleVersion.compareTo(pk.bundleVersion);

		if (value != 0) {
			return value;
		}

		if (companyId < pk.companyId) {
			value = -1;
		}
		else if (companyId > pk.companyId) {
			value = 1;
		}
		else {
			value = 0;
		}

		if (value != 0) {
			return value;
		}

		value = oAuth2ScopeName.compareTo(pk.oAuth2ScopeName);

		if (value != 0) {
			return value;
		}

		value = oAuth2TokenId.compareTo(pk.oAuth2TokenId);

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OAuth2ScopeGrantPK)) {
			return false;
		}

		OAuth2ScopeGrantPK pk = (OAuth2ScopeGrantPK)obj;

		if ((applicationName.equals(pk.applicationName)) &&
				(bundleSymbolicName.equals(pk.bundleSymbolicName)) &&
				(bundleVersion.equals(pk.bundleVersion)) &&
				(companyId == pk.companyId) &&
				(oAuth2ScopeName.equals(pk.oAuth2ScopeName)) &&
				(oAuth2TokenId.equals(pk.oAuth2TokenId))) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hashCode = 0;

		hashCode = HashUtil.hash(hashCode, applicationName);
		hashCode = HashUtil.hash(hashCode, bundleSymbolicName);
		hashCode = HashUtil.hash(hashCode, bundleVersion);
		hashCode = HashUtil.hash(hashCode, companyId);
		hashCode = HashUtil.hash(hashCode, oAuth2ScopeName);
		hashCode = HashUtil.hash(hashCode, oAuth2TokenId);

		return hashCode;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(14);

		sb.append("{");

		sb.append("applicationName=");

		sb.append(applicationName);
		sb.append(", bundleSymbolicName=");

		sb.append(bundleSymbolicName);
		sb.append(", bundleVersion=");

		sb.append(bundleVersion);
		sb.append(", companyId=");

		sb.append(companyId);
		sb.append(", oAuth2ScopeName=");

		sb.append(oAuth2ScopeName);
		sb.append(", oAuth2TokenId=");

		sb.append(oAuth2TokenId);

		sb.append("}");

		return sb.toString();
	}
}