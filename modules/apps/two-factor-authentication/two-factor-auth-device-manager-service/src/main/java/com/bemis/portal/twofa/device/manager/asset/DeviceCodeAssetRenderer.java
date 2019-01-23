package com.bemis.portal.twofa.device.manager.asset;

import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Prathima Shreenath
 */
public class DeviceCodeAssetRenderer extends BaseJSPAssetRenderer<DeviceCode> {

	public DeviceCodeAssetRenderer(DeviceCode deviceCode) {
		_deviceCode = deviceCode;
	}

	@Override
	public DeviceCode getAssetObject() {
		return _deviceCode;
	}

	@Override
	public String getClassName() {
		return DeviceCode.class.getName();
	}

	@Override
	public long getClassPK() {
		return _deviceCode.getDeviceCodeId();
	}

	@Override
	public long getGroupId() {
		return _deviceCode.getGroupId();
	}

	@Override
	public String getJspPath(HttpServletRequest request, String template) {
		return null;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return "DEVICE CODE VERIFICATION : " + _deviceCode.getPortalUserName();
	}

	@Override
	public String getTitle(Locale locale) {
		return DeviceManagerConstants.DEVICE_CODE;
	}

	@Override
	public long getUserId() {
		return _deviceCode.getPortalUserId();
	}

	@Override
	public String getUserName() {
		return _deviceCode.getPortalUserName();
	}

	@Override
	public String getUuid() {
		return null;
	}

	private final DeviceCode _deviceCode;

}