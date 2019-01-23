create table Bemis_Device (
	deviceId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	portalUserId LONG,
	portalUserName VARCHAR(75) null,
	emailAddress VARCHAR(75) null,
	deviceIP VARCHAR(75) null,
	browserName VARCHAR(75) null,
	osName VARCHAR(75) null,
	verified BOOLEAN,
	tempDevice BOOLEAN
);

create table Bemis_DeviceCode (
	deviceCodeId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	portalUserId LONG,
	portalUserName VARCHAR(75) null,
	emailAddress VARCHAR(75) null,
	deviceCode VARCHAR(75) null,
	deviceIP VARCHAR(75) null,
	validationCode INTEGER
);