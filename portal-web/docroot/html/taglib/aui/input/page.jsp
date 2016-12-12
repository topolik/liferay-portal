<%--
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
--%>

<%@ include file="/html/taglib/aui/input/init.jsp" %>

<liferay-util:buffer var="helpMessageContent">
	<c:if test="<%= Validator.isNotNull(helpMessage) %>">
		<liferay-ui:icon-help message="<%= helpMessage %>" />
	</c:if>
</liferay-util:buffer>

<liferay-util:buffer var="labelContent">
	<c:if test="<%= Validator.isNotNull(label) %>">
		<c:if test='<%= type.equals("toggle-switch") %>'>
			<span class="toggle-switch-label">
		</c:if>

		<liferay-ui:message key="<%= label %>" localizeKey="<%= localizeLabel %>" />

		<c:if test='<%= type.equals("toggle-switch") %>'>
			</span>
		</c:if>

		<c:if test='<%= required && showRequiredLabel && !type.equals("radio") %>'>
			<aui:icon cssClass="reference-mark text-warning" image="asterisk" markupView="lexicon" />

			<span class="hide-accessible"><liferay-ui:message key="required" /></span>
		</c:if>

		<c:if test='<%= Validator.isNotNull(helpMessage) && !type.equals("toggle-switch") %>'>
			<%= XSS.html(helpMessageContent) %>
		</c:if>

		<c:if test="<%= changesContext %>">
			<span class="hide-accessible">(<liferay-ui:message key="changing-the-value-of-this-field-reloads-the-page" />)</span>
		</c:if>
	</c:if>

	<c:if test='<%= type.equals("toggle-card") || type.equals("toggle-switch") %>'>

		<%
		String buttonIconOff = (String)dynamicAttributes.get("buttonIconOff");
		String buttonIconOn = (String)dynamicAttributes.get("buttonIconOn");
		String iconOff = (String)dynamicAttributes.get("iconOff");
		String iconOn = (String)dynamicAttributes.get("iconOn");
		String labelOff = (String)dynamicAttributes.get("labelOff");
		String labelOn = (String)dynamicAttributes.get("labelOn");

		if (localizeLabel) {
			if (Validator.isNotNull(labelOff)) {
				labelOff = LanguageUtil.get(resourceBundle, labelOff);
			}

			if (Validator.isNotNull(labelOn)) {
				labelOn = LanguageUtil.get(resourceBundle, labelOn);
			}
		}
		%>

		<c:if test='<%= type.equals("toggle-card") %>'>

			<%
			if (Validator.isNull(iconOff)) {
				iconOff = "icon-circle-blank";
			}

			if (Validator.isNull(iconOn)) {
				iconOn = "icon-ok";
			}

			if (Validator.isNull(labelOff)) {
				labelOff = label;
			}

			if (Validator.isNull(labelOn)) {
				labelOn = label;
			}
			%>

			<div class="toggle-card-container">
				<div class="toggle-card-cell">
					<div class="toggle-card-icon">
						<span class="toggle-card-off <%= XSS.attribute(iconOff) %>"></span>
						<span class="toggle-card-on <%= XSS.attribute(iconOn) %>"></span>
					</div>

					<div class="toggle-card-label">
						<c:if test="<%= Validator.isNotNull(labelOff) %>">
							<span class="toggle-card-off"><%= XSS.htmlBody(labelOff) %></span>
						</c:if>

						<c:if test="<%= Validator.isNotNull(labelOn) %>">
							<span class="toggle-card-on"><%= XSS.htmlBody(labelOn) %></span>
						</c:if>
					</div>
				</div>
			</div>
		</c:if>

		<c:if test='<%= type.equals("toggle-switch") %>'>
			<span aria-hidden="true" class="toggle-switch-bar">
				<span class="toggle-switch-handle" data-label-off="<%= (Validator.isNotNull(labelOff) ? XSS.attribute(labelOff) : LanguageUtil.get(resourceBundle, "no")) %>" data-label-on="<%= (Validator.isNotNull(labelOn) ? XSS.attribute(labelOn) : LanguageUtil.get(resourceBundle, "yes")) %>">
					<c:if test="<%= Validator.isNotNull(buttonIconOn) %>">
						<span class="button-icon <%= Validator.isNotNull(buttonIconOff) ? "button-icon-on" : StringPool.BLANK %> toggle-switch-icon <%= XSS.attribute(buttonIconOn) %>"></span>
					</c:if>

					<c:if test="<%= Validator.isNotNull(buttonIconOff) %>">
						<span class="button-icon button-icon-off toggle-switch-icon <%= XSS.attribute(buttonIconOff) %>"></span>
					</c:if>

					<c:if test="<%= Validator.isNotNull(iconOn) %>">
						<span class="toggle-switch-icon toggle-switch-icon-on <%= XSS.attribute(iconOn) %>"></span>
					</c:if>

					<c:if test="<%= Validator.isNotNull(iconOff) %>">
						<span class="toggle-switch-icon toggle-switch-icon-off <%= XSS.attribute(iconOff) %>"></span>
					</c:if>
				</span>
			</span>

			<c:if test="<%= Validator.isNotNull(helpMessage) %>">
				<span class="toggle-switch-text toggle-switch-text-right"><%= XSS.htmlBody(helpMessageContent) %></span>
			</c:if>
		</c:if>
	</c:if>
</liferay-util:buffer>

<c:if test='<%= !type.equals("hidden") && !wrappedField && useInputWrapper %>'>
	<div class="<%= XSS.attribute(inputWrapperClass) %>">
</c:if>

<%
boolean choiceField = checkboxField || radioField;
%>

<c:if test='<%= !type.equals("assetCategories") && !type.equals("hidden") && Validator.isNotNull(labelContent) %>'>
	<label <%= XSS.safeHtmlOnly(labelTag) %>>
		<c:if test='<%= !choiceField && !inlineLabel.equals("right") %>'>
				<%= XSS.htmlBody(labelContent) %>
			</label>
		</c:if>
</c:if>

<c:if test="<%= Validator.isNotNull(prefix) || Validator.isNotNull(suffix) %>">
	<div class="<%= XSS.attribute(addOnCssClass) %>">
		<c:if test="<%= Validator.isNotNull(prefix) %>">
			<span class="<%= XSS.attribute(helpTextCssClass) %>"><liferay-ui:message key="<%= prefix %>" /></span>
		</c:if>
</c:if>

<c:choose>
	<c:when test='<%= (model != null) && type.equals("assetCategories") %>'>
		<liferay-ui:asset-categories-selector
			className="<%= model.getName() %>"
			classPK="<%= _getClassPK(bean, classPK) %>"
			classTypePK="<%= classTypePK %>"
			ignoreRequestValue="<%= ignoreRequestValue %>"
			showRequiredLabel="<%= showRequiredLabel %>"
		/>
	</c:when>
	<c:when test='<%= (model != null) && type.equals("assetTags") %>'>
		<liferay-ui:asset-tags-selector
			autoFocus="<%= autoFocus %>"
			className="<%= model.getName() %>"
			classPK="<%= _getClassPK(bean, classPK) %>"
			id="<%= namespace + id %>"
			ignoreRequestValue="<%= ignoreRequestValue %>"
		/>
	</c:when>
	<c:when test="<%= (model != null) && Validator.isNull(type) %>">
		<liferay-ui:input-field
			autoComplete='<%= GetterUtil.getBoolean(dynamicAttributes.get("autocomplete"), true) %>'
			autoFocus="<%= autoFocus %>"
			bean="<%= bean %>"
			cssClass="<%= fieldCssClass %>"
			dateTogglerCheckboxLabel="<%= dateTogglerCheckboxLabel %>"
			defaultLanguageId="<%= defaultLanguageId %>"
			defaultValue="<%= value %>"
			disabled="<%= disabled %>"
			field="<%= field %>"
			fieldParam="<%= fieldParam %>"
			format='<%= (Format)dynamicAttributes.get("format") %>'
			formName="<%= formName %>"
			id="<%= id %>"
			ignoreRequestValue="<%= ignoreRequestValue %>"
			languageId="<%= languageId %>"
			model="<%= model %>"
			placeholder="<%= placeholder %>"
			timeFormat='<%= GetterUtil.getString(dynamicAttributes.get("timeFormat")) %>'
		/>
	</c:when>
	<c:when test='<%= baseType.equals("checkbox") %>'>

		<%
		String valueString = null;

		if (value != null) {
			valueString = value.toString();

			if (Validator.isBoolean(valueString)) {
				checked = GetterUtil.getBoolean(valueString);

				valueString = null;
			}
		}

		if (!ignoreRequestValue && Validator.isNotNull(ParamUtil.getString(request, "checkboxNames"))) {
			if (Validator.isNotNull(valueString)) {
				String[] requestValues = ParamUtil.getParameterValues(request, name);

				checked = ArrayUtil.contains(requestValues, valueString);
			}
			else {
				checked = ParamUtil.getBoolean(request, name, checked);
			}
		}
		%>

		<input <%= checked ? "checked" : StringPool.BLANK %> class="<%= XSS.attribute(fieldCssClass) %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= XSS.attribute(namespace) + XSS.attribute(id) %>" name="<%= XSS.attribute(namespace) + XSS.attribute(name) %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onChange)) + "\"" : StringPool.BLANK %> onClick="<%= XSS.attribute(XSS.verifiedJavaScriptOnly(onClick)) %>" <%= Validator.isNotNull(title) ? "title=\"" + XSS.attribute(XSSLang.get(resourceBundle, title)) + "\"" : StringPool.BLANK %> type="checkbox" <%= Validator.isNotNull(valueString) ? ("value=\"" + XSS.attribute(valueString)) + "\"" : StringPool.BLANK %> <%= XSS.safeHtmlOnly(AUIUtil.buildData(data)) %> <%= XSS.safeHtmlOnly(InlineUtil.buildDynamicAttributes(dynamicAttributes)) %> />
	</c:when>
	<c:when test='<%= type.equals("radio") %>'>

		<%
		String valueString = String.valueOf(checked);

		if (value != null) {
			valueString = value.toString();
		}

		if (!ignoreRequestValue) {
			String requestValue = ParamUtil.getString(request, name);

			if (Validator.isNotNull(requestValue)) {
				checked = valueString.equals(requestValue);
			}
		}
		%>

		<input <%= checked ? "checked" : StringPool.BLANK %> class="<%= XSS.attribute(fieldCssClass) %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= XSS.attribute(namespace) + XSS.attribute(id) %>" name="<%= XSS.attribute(namespace) + XSS.attribute(name) %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onChange)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onClick)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + XSS.attribute(XSSLang.get(resourceBundle, title)) + "\"" : StringPool.BLANK %> type="radio" value="<%= XSS.attribute(valueString) %>" <%= XSS.safeHtmlOnly(AUIUtil.buildData(data)) %> <%= XSS.safeHtmlOnly(InlineUtil.buildDynamicAttributes(dynamicAttributes)) %> />
	</c:when>
	<c:when test='<%= type.equals("resource") %>'>
		<liferay-ui:input-resource id="<%= id %>" title="<%= title %>" url="<%= String.valueOf(value) %>" />
	</c:when>
	<c:when test='<%= type.equals("timeZone") %>'>

		<%
		int displayStyle = TimeZone.LONG;

		if (dynamicAttributes.get("displayStyle") != null) {
			displayStyle = GetterUtil.getInteger((String)dynamicAttributes.get("displayStyle"));
		}

		if (Validator.isNull(value)) {
			TimeZone defaultTimeZone = TimeZoneUtil.getDefault();

			value = BeanPropertiesUtil.getStringSilent(bean, field, defaultTimeZone.getID());
		}
		%>

		<liferay-ui:input-time-zone
			autoFocus="<%= autoFocus %>"
			daylight='<%= GetterUtil.getBoolean((String)dynamicAttributes.get("daylight")) %>'
			disabled="<%= disabled %>"
			displayStyle="<%= displayStyle %>"
			name="<%= name %>"
			nullable='<%= GetterUtil.getBoolean((String)dynamicAttributes.get("nullable")) %>'
			value="<%= value.toString() %>"
		/>
	</c:when>
	<c:otherwise>

		<%
		String valueString = StringPool.BLANK;

		if (value != null) {
			valueString = value.toString();
		}

		if (type.equals("hidden") && (value == null)) {
			valueString = BeanPropertiesUtil.getStringSilent(bean, name);
		}
		else if (!ignoreRequestValue && (Validator.isNull(type) || ArrayUtil.contains(_TYPES, type))) {
			valueString = BeanParamUtil.getStringSilent(bean, request, name, valueString);

			if (Validator.isNotNull(fieldParam)) {
				valueString = ParamUtil.getString(request, fieldParam, valueString);
			}
		}
		%>

		<c:choose>
			<c:when test='<%= localized && (type.equals("editor") || type.equals("text") || type.equals("textarea")) %>'>
				<liferay-ui:input-localized
					autoFocus="<%= autoFocus %>"
					availableLocales="<%= LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId()) %>"
					cssClass="<%= fieldCssClass %>"
					defaultLanguageId="<%= defaultLanguageId %>"
					disabled="<%= disabled %>"
					formName="<%= formName %>"
					id="<%= id %>"
					ignoreRequestValue="<%= ignoreRequestValue %>"
					languageId="<%= languageId %>"
					name="<%= name %>"
					onChange="<%= onChange %>"
					onClick="<%= onClick %>"
					placeholder="<%= placeholder %>"
					type='<%= type.equals("text") ? "input" : type %>'
					xml="<%= valueString %>"
				/>
			</c:when>
			<c:when test='<%= type.equals("editor") %>'>
				<liferay-ui:input-editor
					contents="<%= valueString %>"
					contentsLanguageId="<%= languageId %>"
					cssClass="<%= cssClass %>"
					editorName="ckeditor"
					name="<%= fieldParam %>"
					toolbarSet="simple"
				/>
			</c:when>
			<c:when test='<%= type.equals("textarea") %>'>

				<%
				String[] storedDimensions = resizable ? StringUtil.split(SessionClicks.get(request, _TEXTAREA_WIDTH_HEIGHT_PREFIX + namespace + id, StringPool.BLANK)) : StringPool.EMPTY_ARRAY;
				%>

				<textarea class="<%= XSS.attribute(fieldCssClass) %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= XSS.attribute(namespace) + XSS.attribute(id) %>" <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= XSS.attribute(namespace) + XSS.attribute(name) %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onChange)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onClick)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + XSS.attribute(XSSLang.get(resourceBundle, placeholder)) + "\"" : StringPool.BLANK %> <%= (storedDimensions.length > 1) ? "style=\"height: " + XSS.attribute(storedDimensions[0]) + "; width: " + XSS.attribute(storedDimensions[1]) + ";" + XSS.attribute(title) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + XSS.attribute(XSSLang.get(resourceBundle, title)) + "\"" : StringPool.BLANK %> <%= XSS.safeHtmlOnly(AUIUtil.buildData(data)) %> <%= XSS.safeHtmlOnly(InlineUtil.buildDynamicAttributes(dynamicAttributes)) %>><%= XSS.htmlBody(valueString) %></textarea>

				<c:if test="<%= autoSize %>">
					<aui:script use="aui-autosize-deprecated">
						A.one('#<%= XSS.js(namespace) + XSS.js(id) %>').plug(A.Plugin.Autosize);
					</aui:script>
				</c:if>

				<c:if test="<%= resizable %>">
					<aui:script use="liferay-store,resize-base">
						var textareaNode = A.one('#<%= XSS.js(namespace) + XSS.js(id) %>');

						var resizeInstance = new A.Resize(
							{
								after: {
									'end': function(event) {
										Liferay.Store('<%= _TEXTAREA_WIDTH_HEIGHT_PREFIX %><%= XSS.js(namespace) + XSS.js(id) %>', textareaNode.getStyle('height') + ',' + textareaNode.getStyle('width'));
									}
								},
								autoHide: true,
								handles: 'r, br, b',
								node: textareaNode
							}
						);

						textareaNode.setData('resizeInstance', resizeInstance);
					</aui:script>
				</c:if>
			</c:when>
			<c:otherwise>
				<input <%= type.equals("image") ? "alt=\"" + XSS.attribute(XSSLang.get(resourceBundle, title)) + "\"" : StringPool.BLANK %> class="<%= XSS.attribute(fieldCssClass) %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= XSS.attribute(namespace) + XSS.attribute(id) %>" <%= (max != null) ? "max=\"" + XSS.attribute(max) + "\"": StringPool.BLANK %> <%= (min != null) ? "min=\"" + XSS.attribute(min) + "\"": StringPool.BLANK %> <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= XSS.attribute(namespace) + XSS.attribute(name) %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onChange)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + XSS.attribute(XSS.verifiedJavaScriptOnly(onClick)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + XSS.attribute(XSSLang.get(resourceBundle, placeholder)) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + XSS.attribute(XSSLang.get(resourceBundle, title)) + "\"" : StringPool.BLANK %> type="<%= Validator.isNull(type) ? "text" : XSS.attribute(type) %>" <%= !type.equals("image") ? "value=\"" + XSS.attribute(valueString) + "\"" : StringPool.BLANK %> <%= XSS.safeHtmlOnly(AUIUtil.buildData(data)) %> <%= XSS.safeHtmlOnly(InlineUtil.buildDynamicAttributes(dynamicAttributes)) %> />
			</c:otherwise>
		</c:choose>

		<c:if test="<%= autoFocus %>">
			<aui:script>
				Liferay.Util.focusFormField('#<%= XSS.js(namespace) + XSS.js(id) %>');
			</aui:script>
		</c:if>
	</c:otherwise>
</c:choose>

<c:if test="<%= Validator.isNotNull(prefix) || Validator.isNotNull(suffix) %>">
		<c:if test="<%= Validator.isNotNull(suffix) %>">
			<span class="<%= XSS.attribute(helpTextCssClass) %>"><liferay-ui:message key="<%= suffix %>" /></span>
		</c:if>
	</div>
</c:if>

<c:if test='<%= !type.equals("assetCategories") && !type.equals("hidden") && Validator.isNotNull(labelContent) %>'>
	<c:if test='<%= choiceField || inlineLabel.equals("right") %>'>
			<%= XSS.html(labelContent) %>
		</label>
	</c:if>
</c:if>

<c:if test='<%= !type.equals("hidden") && !wrappedField && useInputWrapper %>'>
	</div>
</c:if>

<%!
private long _getClassPK(Object bean, long classPK) {
	if ((bean != null) && (classPK <= 0)) {
		if (bean instanceof ClassedModel) {
			ClassedModel classedModel = (ClassedModel)bean;

			Serializable primaryKeyObj = classedModel.getPrimaryKeyObj();

			if (primaryKeyObj instanceof Long) {
				classPK = (Long)primaryKeyObj;
			}
			else {
				classPK = GetterUtil.getLong(primaryKeyObj.toString());
			}
		}
	}

	return classPK;
}

private static final String _TEXTAREA_WIDTH_HEIGHT_PREFIX = "liferay_resize_";

private static final String[] _TYPES = {"color", "email", "number", "range", "tel", "text", "textarea"};
%>