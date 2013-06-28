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
public class Jdk14LogImpl implements Log {

	public Jdk14LogImpl(Logger logger) {
		_logger = logger;
	}

	@Override
	public void debug(Object msg) {
		_logger.log(Level.FINE, msg.toString());
	}

	@Override
	public void debug(Object msg, Throwable t) {
		_logger.log(Level.FINE, msg.toString(), t);
	}

	@Override
	public void debug(Throwable t) {
		_logger.log(Level.FINE, t.getMessage(), t);
	}

	@Override
	public void error(Object msg) {
		_logger.log(Level.SEVERE, msg.toString());
	}

	@Override
	public void error(Object msg, Throwable t) {
		_logger.log(Level.SEVERE, msg.toString(), t);
	}

	@Override
	public void error(Throwable t) {
		_logger.log(Level.SEVERE, t.getMessage(), t);
	}

	@Override
	public void fatal(Object msg) {
		_logger.log(Level.SEVERE, msg.toString());
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		_logger.log(Level.SEVERE, msg.toString(), t);
	}

	@Override
	public void fatal(Throwable t) {
		_logger.log(Level.SEVERE, t.getMessage(), t);
	}

	public Logger getWrappedLogger() {
		return _logger;
	}

	@Override
	public void info(Object msg) {
		_logger.log(Level.INFO, msg.toString());
	}

	@Override
	public void info(Object msg, Throwable t) {
		_logger.log(Level.INFO, msg.toString(), t);
	}

	@Override
	public void info(Throwable t) {
		_logger.log(Level.INFO, t.getMessage(), t);
	}

	@Override
	public boolean isDebugEnabled() {
		return _logger.isLoggable(Level.FINE);
	}

	@Override
	public boolean isErrorEnabled() {
		return _logger.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isFatalEnabled() {
		return _logger.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isInfoEnabled() {
		return _logger.isLoggable(Level.INFO);
	}

	@Override
	public boolean isTraceEnabled() {
		return _logger.isLoggable(Level.FINEST);
	}

	@Override
	public boolean isWarnEnabled() {
		return _logger.isLoggable(Level.WARNING);
	}

	@Override
	public void trace(Object msg) {
		_logger.log(Level.FINEST, msg.toString());
	}

	@Override
	public void trace(Object msg, Throwable t) {
		_logger.log(Level.FINEST, msg.toString(), t);
	}

	@Override
	public void trace(Throwable t) {
		_logger.log(Level.FINEST, t.getMessage(), t);
	}

	@Override
	public void warn(Object msg) {
		_logger.log(Level.WARNING, msg.toString());
	}

	@Override
	public void warn(Object msg, Throwable t) {
		_logger.log(Level.WARNING, msg.toString(), t);
	}

	@Override
	public void warn(Throwable t) {
		_logger.log(Level.WARNING, t.getMessage(), t);
	}

	private Logger _logger;

}