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

package com.liferay.data.engine.rest.internal.rule.function.v1_0;

import com.liferay.data.engine.rest.dto.v1_0.DataDefinitionField;

/**
 * @author Jeyvison Nascimento
 */
public class DataRuleFunctionResult {

	public static DataRuleFunctionResult of(
		DataDefinitionField dataDefinitionField, String errorCode) {

		return new DataRuleFunctionResult(dataDefinitionField, errorCode);
	}

	public DataDefinitionField getDataDefinitionField() {
		return _dataDefinitionField;
	}

	public String getErrorCode() {
		return _errorCode;
	}

	public boolean isValid() {
		return _valid;
	}

	public void setValid(boolean valid) {
		_valid = valid;

		if (valid) {
			_errorCode = null;
		}
	}

	private DataRuleFunctionResult(
		DataDefinitionField dataDefinitionField, String errorCode) {

		_dataDefinitionField = dataDefinitionField;
		_errorCode = errorCode;
	}

	private final DataDefinitionField _dataDefinitionField;
	private String _errorCode;
	private boolean _valid;

}