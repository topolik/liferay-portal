/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.v7_2_x;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.v7_2_x.util.UserTable;
import com.liferay.portal.util.PropsUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author arthurchan35
 */
public class UpgradeUser extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (connection.getAutoCommit()) {
			connection.setAutoCommit(false);
		}

		_addPrefixToPasswords();

		if (connection.getAutoCommit()) {
			connection.setAutoCommit(true);
		}
	}

	private void _addPrefixToPasswords() throws SQLException {
		Statement stmt1 = connection.createStatement();

		String legacyAlgorithm = PropsUtil.get(
			PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY);

		String currentAlgorithm = PropsUtil.get(
			PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);

		StringBundler selectQuerySB = new StringBundler(4);

		selectQuerySB.append("select userId, password_ from ");
		selectQuerySB.append(UserTable.TABLE_NAME);
		selectQuerySB.append(" where defaultUser = false and password_ not ");
		selectQuerySB.append("like \"{%\"");

		ResultSet rs = stmt1.executeQuery(selectQuerySB.toString());

		Statement stmt2 = connection.createStatement();

		String updateQueryPrefix =
			"update " + UserTable.TABLE_NAME + " set password_= \"{";

		if (Validator.isNotNull(legacyAlgorithm)) {
			_addUpdateQueries(rs, updateQueryPrefix, legacyAlgorithm, stmt2);
		}
		else {
			_addUpdateQueries(rs, updateQueryPrefix, currentAlgorithm, stmt2);
		}

		stmt2.executeBatch();

		connection.commit();
	}

	private void _addUpdateQueries(
			ResultSet rs, String updateQueryPrefix, String algorithm,
			Statement stmt)
		throws SQLException {

		while (rs.next()) {
			StringBundler updateQuerySB = new StringBundler(8);

			updateQuerySB.append(updateQueryPrefix);

			long userId = rs.getLong("userId");
			String password = rs.getString("password_");

			updateQuerySB.append(_getAlgorithmName(algorithm));
			updateQuerySB.append(CharPool.CLOSE_CURLY_BRACE);

			updateQuerySB.append(password);
			updateQuerySB.append(CharPool.QUOTE);
			updateQuerySB.append(" where userId = ");
			updateQuerySB.append(userId);
			updateQuerySB.append(CharPool.SEMICOLON);

			stmt.addBatch(updateQuerySB.toString());
		}
	}

	private String _getAlgorithmName(String algorithm) {
		int index = algorithm.indexOf(CharPool.SLASH);

		if (index > 0) {
			algorithm = algorithm.substring(0, index);
		}

		return StringUtil.toUpperCase(algorithm);
	}

}