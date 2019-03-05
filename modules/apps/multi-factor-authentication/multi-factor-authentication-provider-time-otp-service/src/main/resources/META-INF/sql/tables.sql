create table TimeOTPEntry (
	entryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	sharedSecret VARCHAR(75) null,
	lastSuccessDate DATE null,
	lastSuccessIP VARCHAR(75) null,
	lastFailDate DATE null,
	lastFailIP VARCHAR(75) null,
	failedAttempts INTEGER
);