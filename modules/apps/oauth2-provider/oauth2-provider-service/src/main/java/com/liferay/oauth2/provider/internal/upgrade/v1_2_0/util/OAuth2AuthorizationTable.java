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

package com.liferay.oauth2.provider.internal.upgrade.v1_2_0.util;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author	  Brian Wing Shun Chan
 * @generated
 */
public class OAuth2AuthorizationTable {

	public static final String TABLE_NAME = "OAuth2Authorization";

	public static final Object[][] TABLE_COLUMNS = {
		{"oAuth2AuthorizationId", Types.BIGINT}, {"companyId", Types.BIGINT},
		{"userId", Types.BIGINT}, {"userName", Types.VARCHAR},
		{"createDate", Types.TIMESTAMP}, {"oAuth2ApplicationId", Types.BIGINT},
		{"oA2AScopeAliasesId", Types.BIGINT},
		{"accessTokenContent", Types.CLOB},
		{"accessTokenContentHash", Types.BIGINT},
		{"accessTokenCreateDate", Types.TIMESTAMP},
		{"accessTokenExpirationDate", Types.TIMESTAMP},
		{"remoteHostInfo", Types.VARCHAR}, {"remoteIPInfo", Types.VARCHAR},
		{"refreshTokenContent", Types.CLOB},
		{"refreshTokenContentHash", Types.BIGINT},
		{"refreshTokenCreateDate", Types.TIMESTAMP},
		{"refreshTokenExpirationDate", Types.TIMESTAMP}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
new HashMap<String, Integer>();

static {
TABLE_COLUMNS_MAP.put("oAuth2AuthorizationId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("oAuth2ApplicationId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("oA2AScopeAliasesId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("accessTokenContent", Types.CLOB);

TABLE_COLUMNS_MAP.put("accessTokenContentHash", Types.BIGINT);

TABLE_COLUMNS_MAP.put("accessTokenCreateDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("accessTokenExpirationDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("remoteHostInfo", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("remoteIPInfo", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("refreshTokenContent", Types.CLOB);

TABLE_COLUMNS_MAP.put("refreshTokenContentHash", Types.BIGINT);

TABLE_COLUMNS_MAP.put("refreshTokenCreateDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("refreshTokenExpirationDate", Types.TIMESTAMP);

}
	public static final String TABLE_SQL_CREATE =
"create table OAuth2Authorization (oAuth2AuthorizationId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,oAuth2ApplicationId LONG,oA2AScopeAliasesId LONG,accessTokenContent TEXT null,accessTokenContentHash LONG,accessTokenCreateDate DATE null,accessTokenExpirationDate DATE null,remoteHostInfo VARCHAR(255) null,remoteIPInfo VARCHAR(39) null,refreshTokenContent TEXT null,refreshTokenContentHash LONG,refreshTokenCreateDate DATE null,refreshTokenExpirationDate DATE null)";

	public static final String TABLE_SQL_DROP =
"drop table OAuth2Authorization";

	public static final String[] TABLE_SQL_ADD_INDEXES = {
		"create index IX_77D3B9EA on OAuth2Authorization (accessTokenContentHash)",
		"create index IX_70DD169C on OAuth2Authorization (oAuth2ApplicationId)",
		"create index IX_10C77BD5 on OAuth2Authorization (refreshTokenContentHash)",
		"create index IX_719D503E on OAuth2Authorization (userId)"
	};

}