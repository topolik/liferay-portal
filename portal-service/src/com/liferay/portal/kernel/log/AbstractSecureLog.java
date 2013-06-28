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

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;

/**
 * @author László Csontos
 */
public abstract class AbstractSecureLog<L> implements Log {

	@Override
	public void debug(Object msg) {
		log(getDebugLevel(), msg);
	}

	@Override
	public void debug(Object msg, Throwable t) {
		log(getDebugLevel(), msg, t);
	}

	@Override
	public void debug(Throwable t) {
		log(getDebugLevel(), t.getMessage(), t);
	}

	@Override
	public void error(Object msg) {
		log(getErrorLevel(), msg);
	}

	@Override
	public void error(Object msg, Throwable t) {
		log(getErrorLevel(), msg, t);
	}

	@Override
	public void error(Throwable t) {
		log(getErrorLevel(), t.getMessage(), t);
	}

	@Override
	public void fatal(Object msg) {
		log(getFatalLevel(), msg);
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		log(getFatalLevel(), msg, t);
	}

	@Override
	public void fatal(Throwable t) {
		log(getFatalLevel(), t.getMessage(), t);
	}

	@Override
	public void info(Object msg) {
		log(getInfoLevel(), msg);
	}

	@Override
	public void info(Object msg, Throwable t) {
		log(getInfoLevel(), msg, t);
	}

	@Override
	public void info(Throwable t) {
		log(getInfoLevel(), t.getMessage(), t);
	}

	@Override
	public boolean isDebugEnabled() {
		return isLevelEnabled(getDebugLevel());
	}

	@Override
	public boolean isErrorEnabled() {
		return isLevelEnabled(getErrorLevel());
	}

	@Override
	public boolean isFatalEnabled() {
		return isLevelEnabled(getFatalLevel());
	}

	@Override
	public boolean isInfoEnabled() {
		return isLevelEnabled(getInfoLevel());
	}

	@Override
	public boolean isTraceEnabled() {
		return isLevelEnabled(getTraceLevel());
	}

	@Override
	public boolean isWarnEnabled() {
		return isLevelEnabled(getWarnLevel());
	}

	@Override
	public void trace(Object msg) {
		log(getTraceLevel(), msg);
	}

	@Override
	public void trace(Object msg, Throwable t) {
		log(getTraceLevel(), msg, t);
	}

	@Override
	public void trace(Throwable t) {
		log(getTraceLevel(), t.getMessage(), t);
	}

	@Override
	public void warn(Object msg) {
		log(getWarnLevel(), msg);
	}

	@Override
	public void warn(Object msg, Throwable t) {
		log(getWarnLevel(), msg, t);
	}

	@Override
	public void warn(Throwable t) {
		log(getWarnLevel(), t.getMessage(), t);
	}

	protected abstract void doLog(L level, String msg, Throwable t);

	protected abstract L getDebugLevel();

	protected abstract L getErrorLevel();

	protected abstract L getFatalLevel();

	protected abstract L getInfoLevel();

	protected abstract L getTraceLevel();

	protected abstract L getWarnLevel();

	protected abstract boolean isLevelEnabled(L level);

	protected void log(L level, Object msg) {
		log(level, msg, null);
	}

	protected void log(L level, Object msg, Throwable t) {
		doLog(level, GetterUtil.getString(msg, StringPool.BLANK), t);
	}

	protected void log(L level, Throwable t) {
		log(level, null, t);
	}

}