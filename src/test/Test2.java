package test;

import java.net.InetAddress;

public class Test2 {

	public static void main(String[] args) throws Exception {
		String[] parts = "10.155.0.0/16".split("/");
	    String ip = parts[0];
	    int prefix;
	    if (parts.length < 2) {
	        prefix = 0;
	    } else {
	        prefix = Integer.parseInt(parts[1]);
	    }
	    int mask = 0xffffffff << (32 - prefix);
	    System.out.println("Prefix=" + prefix);
	    System.out.println("Address=" + ip);

	    int value = mask;
	    byte[] bytes = new byte[]{ 
	            (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };

	    InetAddress netAddr = InetAddress.getByAddress(bytes);
	    System.out.println("Mask=" + netAddr.getHostAddress());
	}

}
