/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.audit;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class KeyAuditServiceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_auditRouterUtilMockedStatic = Mockito.mockStatic(AuditRouterUtil.class);

		ReflectionTestUtil.setFieldValue(
			_keyAuditService, "_jsonFactory", _jsonFactory);
	}

	@After
	public void tearDown() {
		_auditRouterUtilMockedStatic.close();
	}

	@Test
	public void testAuditAccess() throws Exception {
		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.when(
			_jsonFactory.createJSONObject()
		).thenReturn(
			jsonObject
		);

		Mockito.when(
			jsonObject.put(Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		Mockito.when(
			jsonObject.put(Mockito.anyString(), Mockito.anyBoolean())
		).thenReturn(
			jsonObject
		);

		_keyAuditService.auditAccess("provider", "alias", true, "message");

		_auditRouterUtilMockedStatic.verify(
			() -> AuditRouterUtil.route(Mockito.any(AuditMessage.class)));
	}

	@Mock
	private JSONFactory _jsonFactory;

	@InjectMocks
	private KeyAuditService _keyAuditService;

	private MockedStatic<AuditRouterUtil> _auditRouterUtilMockedStatic;

}
