create table KeyManagerDB_KeyEntry (
	mvccVersion LONG default 0 not null,
	keyEntryId LONG not null primary key,
	companyId LONG,
	alias_ VARCHAR(75) null,
	keyType VARCHAR(75) null,
	algorithm VARCHAR(75) null,
	cipherSpec VARCHAR(75) null,
	wrappedKeyBlob BLOB,
	kekReference VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null
);

create table KeyManagerDB_SecretEntry (
	mvccVersion LONG default 0 not null,
	secretEntryId LONG not null primary key,
	companyId LONG,
	alias_ VARCHAR(75) null,
	ciphertextBlob BLOB,
	iv VARCHAR(75) null,
	encryptedDEKBlob BLOB,
	kekReference VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null
);