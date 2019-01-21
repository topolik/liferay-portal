create table HOTP (
	hotpId LONG not null primary key,
	userId LONG,
	count LONG,
	sharedSecret VARCHAR(75) null,
	verified BOOLEAN
);

create table TOTP (
	totpId LONG not null primary key,
	userId LONG,
	sharedSecret VARCHAR(75) null,
	verified BOOLEAN
);