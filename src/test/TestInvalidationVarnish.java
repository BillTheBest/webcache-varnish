package test;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestInvalidationVarnish {
	private static PrintStream out = System.out;
	
	public static final void main(final String[] args) throws Exception {
		Socket socket = new Socket();
		socket.setKeepAlive(false);
		socket.setTcpNoDelay(true);
		socket.connect(new InetSocketAddress(InetAddress.getByName("10.202.125.3"), 80), 500); //85 149
		PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
		
		pout.println("PURGE / HTTP/1.1");
		pout.println("Connection: close");
		pout.println("User-Agent: XParserAdmin-Aget/1.0 (Varnish invalidation tool; Ban)");
		pout.println("Host: justforhttp11compatibility");
		pout.println("X-Pattern: ^/css/estilos-capa.css");
		pout.println("X-Group: IT");
		
		pout.println();
		pout.flush();
		
		int c = 0;
		InputStream in = socket.getInputStream();
		while((c = in.read()) != -1) {
			out.print((char) c);
		}
		socket.close();
	}
}
