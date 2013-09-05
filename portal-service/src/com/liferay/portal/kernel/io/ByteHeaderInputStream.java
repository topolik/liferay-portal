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

package com.liferay.portal.kernel.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tomas Polesovsky
 */
public class ByteHeaderInputStream extends FilterInputStream {

	public ByteHeaderInputStream(InputStream in, byte[] header) {
		super(in);
		_header = header;
	}

	@Override
	public int read() throws IOException {
		if ((_headerPos + 1) < _header.length) {
			_headerPos++;
			return _header[_headerPos - 1];
		}

		return super.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (_headerPos < _header.length) {
			if (b == null) {
				throw new NullPointerException();
			}
			else if ((off < 0) || (len < 0) || (len > (b.length - off))) {
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return 0;
			}

			int remainingBytes = _header.length - _headerPos;
			int bytesToWrite = Math.min(remainingBytes, len);

			System.arraycopy(_header, _headerPos, b, off, bytesToWrite);

			_headerPos += bytesToWrite;

			return bytesToWrite;
		}

		return super.read(b, off, len);
	}

	private byte[] _header;
	private int _headerPos = 0;

}