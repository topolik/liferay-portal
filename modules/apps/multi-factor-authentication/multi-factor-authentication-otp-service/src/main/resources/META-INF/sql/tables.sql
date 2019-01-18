create table TOTP (
	totpId LONG not null primary key,
	userId LONG,
	sharedSecret VARCHAR(75) null,
	verified BOOLEAN
);