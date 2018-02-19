create index IX_523E5C67 on OAuth2Application (companyId, clientId[$COLUMN_LENGTH:75$]);

create index IX_5D141D2B on OAuth2RefreshToken (oAuth2ApplicationId, userName[$COLUMN_LENGTH:75$]);

create index IX_8295BE2C on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], companyId, oAuth2ScopeName[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_BA2C0672 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], companyId, oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_593E3746 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], oAuth2ScopeName[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_D52B0E18 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_310FAE60 on OAuth2ScopeGrant (oAuth2TokenId[$COLUMN_LENGTH:75$]);

create index IX_1351786 on OAuth2Token (oAuth2ApplicationId, userName[$COLUMN_LENGTH:75$]);
create index IX_6CD4E94C on OAuth2Token (oAuth2RefreshTokenId[$COLUMN_LENGTH:75$]);