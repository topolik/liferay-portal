/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Blob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;KeyManagerDB_SecretEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SecretEntry
 * @generated
 */
public class SecretEntryTable extends BaseTable<SecretEntryTable> {

	public static final SecretEntryTable INSTANCE = new SecretEntryTable();

	public final Column<SecretEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<SecretEntryTable, Long> secretEntryId = createColumn(
		"secretEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SecretEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, String> alias = createColumn(
		"alias_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, Blob> ciphertextBlob = createColumn(
		"ciphertextBlob", Blob.class, Types.BLOB, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, String> iv = createColumn(
		"iv", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, Blob> encryptedDEKBlob = createColumn(
		"encryptedDEKBlob", Blob.class, Types.BLOB, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, String> kekReference = createColumn(
		"kekReference", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SecretEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private SecretEntryTable() {
		super("KeyManagerDB_SecretEntry", SecretEntryTable::new);
	}

}