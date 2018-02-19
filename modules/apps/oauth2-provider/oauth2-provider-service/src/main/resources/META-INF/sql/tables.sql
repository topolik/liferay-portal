create table OAuth2Application (
	oAuth2ApplicationId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	clientId VARCHAR(75) null,
	clientSecret VARCHAR(75) null,
	redirectUri VARCHAR(75) null,
	clientConfidential BOOLEAN,
	description VARCHAR(75) null,
	name VARCHAR(75) null,
	webUrl VARCHAR(75) null
);

create table OAuth2RefreshToken (
	oAuth2RefreshTokenId VARCHAR(75) not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	lifeTime LONG,
	oAuth2ApplicationId LONG
);

create table OAuth2ScopeGrant (
	applicationName VARCHAR(75) not null,
	bundleSymbolicName VARCHAR(75) not null,
	bundleVersion VARCHAR(75) not null,
	companyId LONG not null,
	oAuth2ScopeName VARCHAR(75) not null,
	oAuth2TokenId VARCHAR(75) not null,
	createDate DATE null,
	primary key (applicationName, bundleSymbolicName, bundleVersion, companyId, oAuth2ScopeName, oAuth2TokenId)
);

create table OAuth2Token (
	oAuth2TokenId VARCHAR(75) not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	lifeTime LONG,
	oAuth2ApplicationId LONG,
	oAuth2TokenType VARCHAR(75) null,
	oAuth2RefreshTokenId VARCHAR(75) null,
	scopes VARCHAR(75) null
);