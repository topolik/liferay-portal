package com.bemis.portal.twofa.device.manager.asset.factory;

import com.bemis.portal.twofa.device.manager.asset.DeviceCodeAssetRenderer;
import com.bemis.portal.twofa.device.manager.constants.DeviceManagerConstants;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.service.DeviceCodeLocalService;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Prathima Shreenath
 */
@Component(
	immediate = true,
	property = {"javax.portlet.name=" + DeviceManagerConstants.DEVICE_CODE},
	service = AssetRendererFactory.class
)
public class DeviceCodeAssetRendererFactory
	extends BaseAssetRendererFactory<DeviceCode> {

	public static final String TYPE = "device_code_verification";

	@Override
	public AssetRenderer<DeviceCode> getAssetRenderer(long classPK, int type)
		throws PortalException {

		DeviceCode deviceCode = _deviceCodeLocalService.getDeviceCode(classPK);

		return new DeviceCodeAssetRenderer(deviceCode);
	}

	public String getClassName() {
		return DeviceCode.class.getName();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private DeviceCodeLocalService _deviceCodeLocalService;

}