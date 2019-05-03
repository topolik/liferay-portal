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
		String passwordsEncryptionAlgorithm = PropsUtil.get(
			PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY);

		if (Validator.isBlank(passwordsEncryptionAlgorithm)) {
			passwordsEncryptionAlgorithm =
				PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);
		}

		int index = passwordsEncryptionAlgorithm.indexOf(CharPool.SLASH);

		if (index > -1) {
			passwordsEncryptionAlgorithm =
				passwordsEncryptionAlgorithm.substring(0, index);
		}

		passwordsEncryptionAlgorithm = StringUtil.toUpperCase(
			passwordsEncryptionAlgorithm);

		String sql = StringBundler.concat(
			"update ", UserTable.TABLE_NAME, " set password_= CONCAT('{",
			passwordsEncryptionAlgorithm,
			"}', password) where password_ not like '{%'");

		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}
}