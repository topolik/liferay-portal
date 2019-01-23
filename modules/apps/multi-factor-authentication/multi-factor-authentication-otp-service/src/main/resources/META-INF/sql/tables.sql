create table HOTP (
	hotpId LONG not null primary key,
	userId LONG,
	count LONG,
	failedAttempts INTEGER,
	sharedSecret VARCHAR(75) null,
	verified BOOLEAN
);

create table TOTP (
	totpId LONG not null primary key,
	userId LONG,
	failedAttempts INTEGER,
	sharedSecret VARCHAR(75) null,
	verified BOOLEAN
);