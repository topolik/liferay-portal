create table TOTP (
	totpId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	backupCodes VARCHAR(255) null,
	lastLoginDate DATE null,
	lastLoginIP VARCHAR(75) null,
	lastFailedLoginDate DATE null,
	failedLoginAttempts INTEGER,
	sharedSecret VARCHAR(255) null,
	verified BOOLEAN
);