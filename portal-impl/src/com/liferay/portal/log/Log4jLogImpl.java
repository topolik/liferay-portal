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

package com.liferay.portal.log;

import com.liferay.portal.kernel.log.AbstractSecureLog;
import com.liferay.portal.kernel.log.LogWrapper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogImpl extends AbstractSecureLog<Level> {

	public Log4jLogImpl(Logger logger) {
		_logger = logger;
	}

	public Logger getWrappedLogger() {
		return _logger;
	}

	@Override
	protected void doLog(Level level, String msg, Throwable t) {
		_logger.log(_FQCN, level, msg, t);
	}

	@Override
	protected Level getDebugLevel() {
		return Level.DEBUG;
	}

	@Override
	protected Level getErrorLevel() {
		return Level.ERROR;
	}

	@Override
	protected Level getFatalLevel() {
		return Level.FATAL;
	}

	@Override
	protected Level getInfoLevel() {
		return Level.INFO;
	}

	@Override
	protected Level getTraceLevel() {
		return Level.TRACE;
	}

	@Override
	protected Level getWarnLevel() {
		return Level.WARN;
	}

	@Override
	protected boolean isLevelEnabled(Level level) {
		return _logger.isEnabledFor(level);
	}

	private static final String _FQCN = LogWrapper.class.getName();

	private Logger _logger;

}