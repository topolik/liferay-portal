/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Brian Wing Shun Chan
 */
public class Jdk14LogImpl extends AbstractSecureLog<Level> {

	public Jdk14LogImpl(Logger logger) {
		_logger = logger;
	}

	public Logger getWrappedLogger() {
		return _logger;
	}

	@Override
	protected void doLog(Level level, String msg, Throwable t) {
		_logger.log(level, msg, t);
	}

	@Override
	protected Level getDebugLevel() {
		return Level.FINE;
	}

	@Override
	protected Level getErrorLevel() {
		return Level.SEVERE;
	}

	@Override
	protected Level getFatalLevel() {
		return Level.SEVERE;
	}

	@Override
	protected Level getInfoLevel() {
		return Level.INFO;
	}

	@Override
	protected Level getTraceLevel() {
		return Level.FINEST;
	}

	@Override
	protected Level getWarnLevel() {
		return Level.WARNING;
	}

	@Override
	protected boolean isLevelEnabled(Level level) {
		return _logger.isLoggable(level);
	}

	private Logger _logger;

}