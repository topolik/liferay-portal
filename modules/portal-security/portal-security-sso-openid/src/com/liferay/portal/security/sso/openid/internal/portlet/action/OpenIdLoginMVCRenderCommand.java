package com.liferay.portal.security.sso.openid.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.security.sso.openid.constants.OpenIdWebKeys;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + OpenIdWebKeys.OPEN_ID,
			"mvc.command.name=/login/openid"
		},
		service = MVCRenderCommand.class
	)
public class OpenIdLoginMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(RenderRequest renderReq, RenderResponse renderResp)
			throws PortletException {

		return "/html/portlet/login/open_id.jsp";
	}
}
