package com.rbs.cache.varnish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Descriptor {
	
	private final File root;
	
	public Descriptor(final File root) {
		this.root = root;
	}
	
	private final PrintStream getPrintStream(final String name) {
		PrintStream pout = null;
		try {
			pout = new PrintStream(new FileOutputStream(new File(root, name)));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pout;
	}

	public final void write(final AbstractVCLObject object) {		
		getPrintStream(object.getName()).println(object);
	}
}
