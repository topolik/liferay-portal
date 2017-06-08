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

package com.liferay.login.web.internal.portlet.action;

import com.liferay.login.web.internal.constants.LoginPortletKeys;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/forgot_password"
	},
	service = MVCRenderCommand.class
)
public class ForgotPasswordMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isSendPassword() && !company.isSendPasswordResetLink()) {
			return "/login.jsp";
		}

		Ticket ticket = getTicket(renderRequest);

		if (ticket != null) {
			User user = UserLocalServiceUtil.getUser(ticket.getClassPK());

			PortletSession portletSession = renderRequest.getPortletSession();

			portletSession.setAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_USER_EMAIL_ADDRESS,
				user.getEmailAddress());
			portletSession.setAttribute(WebKeys.TICKET, ticket);

			renderRequest.setAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_USER, user);
		}

		renderResponse.setTitle(themeDisplay.translate("forgot-password"));

		return "/forgot_password.jsp";
	}

	protected Ticket getTicket(PortletRequest portletRequest) {
		PortletSession portletSession = portletRequest.getPortletSession();

		Ticket ticket = (Ticket)portletSession.getAttribute(WebKeys.TICKET);

		if (ticket != null) {
			return ticket;
		}

		String ticketKey = ParamUtil.getString(portletRequest, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return null;
		}

		try {
			ticket = TicketLocalServiceUtil.fetchTicket(ticketKey);

			if ((ticket == null) ||
				(ticket.getType() != TicketConstants.TYPE_PASSWORD)) {

				return null;
			}

			if (!ticket.isExpired()) {
				return ticket;
			}

			TicketLocalServiceUtil.deleteTicket(ticket);
		}
		catch (Exception e) {
		}

		return null;
	}

}