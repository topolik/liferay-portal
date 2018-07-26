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

package com.liferay.dynamic.data.mapping.internal;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.portal.json.transformer.ObjectTransformer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONContext;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

/**
 * @author Stian Sigvartsen
 */
public class DDMDataProviderInstanceJSONTransformer extends ObjectTransformer {

	@Override
	public void transform(JSONContext jsonContext, Object object) {
		DDMDataProviderInstance dDMDataProviderInstance =
			(DDMDataProviderInstance)object;

		String definition = dDMDataProviderInstance.getDefinition();

		try {
			JSONObject rootJSONObject = JSONFactoryUtil.createJSONObject(
				definition);

			JSONArray currentJSONArray = rootJSONObject.getJSONArray(
				"fieldValues");

			JSONArray newJSONArray = JSONFactoryUtil.createJSONArray();

			for (int i = 0; i < currentJSONArray.length(); i++) {
				JSONObject jsonObject = currentJSONArray.getJSONObject(i);

				Object name = jsonObject.get("name");

				if (!name.equals("password")) {
					newJSONArray.put(jsonObject);
				}
			}

			rootJSONObject.put("fieldValues", newJSONArray);

			dDMDataProviderInstance.setDefinition(
				rootJSONObject.toJSONString());
		}
		catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		super.transform(jsonContext, object);
	}

}