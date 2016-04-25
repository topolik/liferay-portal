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

package com.liferay.portal.security.service.access.quota.internal.persistence;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionPersistence;
import com.liferay.ticket.kernel.model.TicketConstants;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component
public class TicketBasedSAQImpressionPersistence
	implements SAQImpressionPersistence {

	public TicketBasedSAQImpressionPersistence() {
	}

	@Override
	public void createImpression(
		Class<?> serviceClazz, Properties callMetrics, long expiryMillis) {

		Date expirationDate = new Date(
			System.currentTimeMillis() + expiryMillis);

		StringWriter sw = new StringWriter();

		try {
			callMetrics.store(sw, null);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}

		_ticketService.addTicket(
			0, serviceClazz.getName(), 0, TicketConstants.TYPE_RATE_LIMITING,
			sw.toString(), expirationDate, null);
	}

	@Override
	public Iterator<SAQImpression> findImpressions(
		Class<?> serviceClazz, Properties callMetrics) {

		// No retrieval optimization can be made by knowing the call metrics
		// when persistence is implemented with TicketService

		List<Ticket> tickets = _ticketService.findTickets(
			serviceClazz.getName(), 0, TicketConstants.TYPE_RATE_LIMITING);

		_serviceClazz = serviceClazz;

		return new SAQImpressionIterator(tickets);
	}

	private Class<?> _serviceClazz;

	@Reference
	private volatile TicketLocalService _ticketService;

	private class SAQImpressionIterator implements Iterator<SAQImpression> {

		public SAQImpressionIterator(List<Ticket> tickets) {
			_ticketIterator = tickets.iterator();
			_adapter = new SAQImpressionImpl();
		}

		@Override
		public boolean hasNext() {
			if (_next != null) {
				return true;
			}

			while (_ticketIterator.hasNext()) {
				_next = _ticketIterator.next();

				if (!_next.isExpired()) {
					return true;
				}
				else {
					_ticketService.deleteTicket(_next);
				}
			}

			return false;
		}

		@Override
		public SAQImpression next() {
			if (!hasNext()) {
				return null;
			}

			_adapter.setTicket(_next);
			return _adapter;
		}

		@Override
		public void remove() {
			_ticketService.deleteTicket(_adapter._ticket);
		}

		private final SAQImpressionImpl _adapter;
		private Ticket _next;
		private final Iterator<Ticket> _ticketIterator;

		// Flyweight pattern object for performance and memory use optimization

		private class SAQImpressionImpl implements SAQImpression {

			public long getCreatedMillis() {
				return _ticket.getCreateDate().getTime();
			}

			public String getKey() {
				return _ticket.getKey();
			}

			@Override
			public Class<?> getServiceClass() {
				return _serviceClazz;
			}

			@Override
			public int getWeight() {
				return 1;
			}

			public void loadMetrics(Properties props) {
				String extraInfo = _ticket.getExtraInfo();
				props.clear();

				if (extraInfo != null) {
					try {
						props.load(new StringReader(extraInfo));
					}
					catch (IOException ioe) {
						throw new SystemException(
							"Failed to parse extra info of ticket " +
								_ticket.getKey());
					}
				}
			}

			public void setTicket(Ticket ticket) {
				_ticket = ticket;
			}

			private Ticket _ticket;

		}

	}

}