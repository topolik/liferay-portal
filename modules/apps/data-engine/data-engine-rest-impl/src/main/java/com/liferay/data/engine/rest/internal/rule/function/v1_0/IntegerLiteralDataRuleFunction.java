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
import com.liferay.data.engine.rest.dto.v1_0.DataDefinitionRuleParameter;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

/**
 * @author Jeyvison Nascimento
 */
public class IntegerLiteralDataRuleFunction implements DataRuleFunction {

	@Override
	public DataRuleFunctionResult validate(
		DataDefinitionField dataDefinitionField,
		DataDefinitionRuleParameter[] dataDefinitionRuleParameters,
		Object value) {

		DataRuleFunctionResult dataRuleFunctionResult =
			DataRuleFunctionResult.of(
				dataDefinitionField, "value-must-be-an-integer-value");

		if (value == null) {
			return dataRuleFunctionResult;
		}

		dataRuleFunctionResult.setValid(
			!Objects.equals(
				GetterUtil.getInteger(value.toString(), Integer.MIN_VALUE),
				Integer.MIN_VALUE));

		return dataRuleFunctionResult;
	}

}