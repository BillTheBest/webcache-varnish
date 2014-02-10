package com.rbs.cache.varnish;

import java.io.PrintWriter;
import java.io.Writer;

public class LinuxPrintWriter extends PrintWriter {

	public LinuxPrintWriter(final Writer out) {
		super(out);
	}
	
	public void println() {
		newLine();
	}
	
	public void newLine() {
		print('\n');
	}
	
	public void println(final String text) {
		print(text);
		newLine();
	}

}
