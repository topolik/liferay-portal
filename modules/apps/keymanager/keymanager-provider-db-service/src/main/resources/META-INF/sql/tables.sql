create table KeyManagerDB_SecretEntry (
	mvccVersion LONG default 0 not null,
	secretEntryId LONG not null primary key,
	companyId LONG,
	alias_ VARCHAR(75) null,
	ciphertextBlob BLOB,
	iv VARCHAR(75) null,
	encryptedDEKBlob BLOB,
	dekIv VARCHAR(75) null,
	kekReference VARCHAR(75) null,
	algorithm VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null
);