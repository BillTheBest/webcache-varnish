package com.rbs.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HttpCheck {
	
	public static boolean check(final String host, final int port) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(false);
			socket.setReuseAddress(false);
			socket.setSoTimeout(50);
			long t1 = System.currentTimeMillis();
			socket.connect(new InetSocketAddress(host, port), 10);
			System.err.println("Conected to " + host + ":" + port + " in " + ((System.currentTimeMillis() - t1) / 1000D) + " seconds");
			return true;
		} catch (NullPointerException e) {
		} catch (IOException e) {
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
