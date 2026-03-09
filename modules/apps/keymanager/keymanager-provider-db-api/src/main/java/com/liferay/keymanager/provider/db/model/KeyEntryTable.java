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
 * The table class for the &quot;KeyManagerDB_KeyEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see KeyEntry
 * @generated
 */
public class KeyEntryTable extends BaseTable<KeyEntryTable> {

	public static final KeyEntryTable INSTANCE = new KeyEntryTable();

	public final Column<KeyEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<KeyEntryTable, Long> keyEntryId = createColumn(
		"keyEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<KeyEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, String> alias = createColumn(
		"alias_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, String> keyType = createColumn(
		"keyType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, String> algorithm = createColumn(
		"algorithm", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, String> cipherSpec = createColumn(
		"cipherSpec", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, Blob> wrappedKeyBlob = createColumn(
		"wrappedKeyBlob", Blob.class, Types.BLOB, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, String> kekReference = createColumn(
		"kekReference", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<KeyEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private KeyEntryTable() {
		super("KeyManagerDB_KeyEntry", KeyEntryTable::new);
	}

}