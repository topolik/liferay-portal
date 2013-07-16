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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;

import java.io.PrintWriter;

/**
 * @author Brian Wing Shun Chan
 * @author László Csontos
 */
public class StackTraceUtil {

	public static String getStackTrace(Throwable t) {
		return getStackTrace(t, false);
	}

	public static String getStackTrace(Throwable t, boolean sanitize) {
		String stackTrace = null;

		PrintWriter printWriter = null;

		try {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			printWriter = UnsyncPrintWriterPool.borrow(unsyncStringWriter);

			if (sanitize) {
				t = SanitizingThrowableWrapper.wrap(t);
			}

			t.printStackTrace(printWriter);

			printWriter.flush();

			stackTrace = unsyncStringWriter.toString();
		}
		finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}

		return stackTrace;
	}

	private static class SanitizingThrowableWrapper extends Throwable {

		@Override
		public Throwable getCause() {
			SanitizingThrowableWrapper wrapper =
				(SanitizingThrowableWrapper)super.getCause();

			if (wrapper._throwable == null) {
				return null;
			}

			return wrapper;
		}

		@Override
		public String getLocalizedMessage() {
			if (_throwable == null) {
				return StringPool.BLANK;
			}

			String localizedMessage = _throwable.getLocalizedMessage();

			return SanitizerUtil.sanitizeCrlf(localizedMessage);
		}

		@Override
		public String toString() {
			if (_throwable == null) {
				return StringPool.BLANK;
			}

			String className = _throwable.getClass().getName();

			String localizedMessage = getLocalizedMessage();

			if (Validator.isBlank(localizedMessage)) {
				return className;
			}

			localizedMessage = (className + ": " + localizedMessage);

			return localizedMessage;
		}

		public static Throwable wrap(Throwable throwable) {
			return new SanitizingThrowableWrapper(throwable);
		}

		protected static SanitizingThrowableWrapper doWrap(
			Throwable throwable) {

			if (throwable == null) {
				return null;
			}

			return new SanitizingThrowableWrapper(throwable.getCause());
		}

		protected SanitizingThrowableWrapper(Throwable throwable) {
			super(doWrap(throwable));

			_throwable = throwable;

			if (throwable != null) {
				setStackTrace(throwable.getStackTrace());
			}
		}

		private Throwable _throwable;

	}

}