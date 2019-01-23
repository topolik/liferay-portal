package com.bemis.portal.twofa.login.web.executor;

import com.bemis.portal.twofa.device.manager.model.DeviceInfo;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.portlet.ActionRequest;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Miyagi
 */
@PrepareForTest(PortalUtil.class)
@RunWith(PowerMockRunner.class)
public class TwoFactorAuthDeviceInfoExtractTest {

	@Test
	public void testBlankFallbackToLocalhost()
		throws PortalException, UnknownHostException {

		setRequestWithHeader(StringPool.BLANK);
		String expectedAddress = InetAddress.getLocalHost().getHostAddress();

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", expectedAddress, extractDeviceInfo.getDeviceIP());
	}

	@Test
	public void testIpAddressListFromProxy() throws PortalException {
		setRequestWithHeader("10.32.108.21,10.32.2.5,10.34.0.1");

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", "10.32.108.21", extractDeviceInfo.getDeviceIP());
	}

	@Test
	public void testIpAddressListWithPortFromProxy() throws PortalException {
		setRequestWithHeader("10.32.108.21:9500,10.32.2.5:221,10.34.0.1");

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", "10.32.108.21", extractDeviceInfo.getDeviceIP());
	}

	@Test
	public void testIpAddressOnly() throws PortalException {
		setRequestWithHeader("10.32.108.23");

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", "10.32.108.23", extractDeviceInfo.getDeviceIP());
	}

	@Test
	public void testIpAddressWithPort() throws PortalException {
		setRequestWithHeader("10.32.108.23:8501");

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", "10.32.108.23", extractDeviceInfo.getDeviceIP());
	}

	@Test
	public void testNullFallbackToLocalhost()
		throws PortalException, UnknownHostException {

		setRequestWithHeader(null);
		String expectedAddress = InetAddress.getLocalHost().getHostAddress();

		DeviceInfo extractDeviceInfo = _twoFactorAuthExecutor.extractDeviceInfo(
			getActionRequest());

		Assert.assertEquals(
			"DeviceIP", expectedAddress, extractDeviceInfo.getDeviceIP());
	}

	protected ActionRequest getActionRequest() {
		ThemeDisplay theme = Mockito.mock(ThemeDisplay.class);

		Mockito.when(theme.getCompanyId()).thenReturn(1L);

		ActionRequest actionRequest = Mockito.mock(ActionRequest.class);

		Mockito.when(
			actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY)).thenReturn(theme);
		return actionRequest;
	}

	protected void setRequest(HttpServletRequest req) {
		PowerMockito.mockStatic(PortalUtil.class);
		PowerMockito.when(
			PortalUtil.getOriginalServletRequest(
				Mockito.any(HttpServletRequest.class))).thenReturn(req);
	}

	protected void setRequestWithHeader(String header) {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		Mockito.when(request.getHeader(_X_FORWARDED_FOR)).thenReturn(header);
		setRequest(request);
	}

	@Mock
	protected UserLocalService userLocalService;

	private static final String _X_FORWARDED_FOR = "X-FORWARDED-FOR";

	@InjectMocks
	private TwoFactorAuthExecutor _twoFactorAuthExecutor =
		new TwoFactorAuthExecutor();

}