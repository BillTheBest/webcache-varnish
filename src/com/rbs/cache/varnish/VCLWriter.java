package com.rbs.cache.varnish;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.rbs.cache.Pattern;
import com.rbs.cache.Site;
import com.rbs.cache.varnish.vcl.VCL;
import com.rbs.cache.varnish.vcl.VCLMethod;

public class VCLWriter {

	private final PrintWriter out;
	private final File root;
	
	public VCLWriter(final File file) throws IOException {
		this.out = new LinuxPrintWriter(new FileWriter(file));
		this.root = file.getParentFile();
	}
	
	public void write(final VCL vcl) {
		out.println("import std;");
		
		writeACL();
		writeProbes(vcl);		
		writeMobileDetect();
		
		writeDirectors(vcl);
		
		writeRecv(vcl);
		out.println();
		
		out.println(VCLMethod.getTemplate("hash.vcl"));
		out.println();
		
		out.println(VCLMethod.getTemplate("fetch.vcl"));
		out.println();
		
		out.println(VCLMethod.getTemplate("pipe.vcl"));
		out.println();
		
		out.println(VCLMethod.getTemplate("hit.vcl"));
		out.println();
		
		out.println(VCLMethod.getTemplate("miss.vcl"));
		out.println();
		
		out.println(VCLMethod.getTemplate("error.vcl"));
		out.println();

		if(vcl.isPrintHitFlag()) {
			out.println(VCLMethod.getTemplate("deliver_hitflag.vcl"));
		} else {
			out.println(VCLMethod.getTemplate("deliver.vcl"));
		}
		
		out.close();
	}
	
	private void writeRecv(final VCL vcl) {
		out.println("sub vcl_recv {");
		
		out.println(VCLMethod.getTemplate("recv_header.vcl"));
		
		for(Site site : vcl.getSites().values()) {
			if(site.getDirector() == null) {
				System.err.println("skipped site \"" + site + "\" due no director available to handle it");
				continue;
			}
			
			String descriptor = "sites/" + site.getName() + (site.getUrl() != null && !"/".equals(site.getUrl()) ? site.getUrl().replaceAll("/", "_") : "") + ".vcl";
			PrintWriter output = getWriter(new File(root, descriptor));
			
			output.print("\tif(!req.http.X-Backend-Found && (");
			if(site.getAliases().isEmpty()) {
				site.getAliases().add(site.getName() + (site.getUrl() == null || site.getUrl().length() == 1 ? "" : site.getUrl()));
			}
			output.print(new Pattern(site.getAliases()).compile());
				
			output.println(") ) {");
			output.println();
			
			output.println("\t\tset req.backend = " + site.getDirector().getName() + ";");
			output.println("\t\tset req.http.X-Group = \"" + site.getGroup() + "\";");
			output.println("\t\tset req.http.X-Backend-Found = \"true\";");
			
			if(!site.getUrisForHitForPass().isEmpty()) {
				output.println();
				output.println("\t\t#Custom URIs for Hit for Pass");
				for(String uri : site.getUrisForHitForPass()) {
					output.println("\t\tif(req.url ~ \"" + uri + "\") {");
					output.println("\t\t\tset req.hash_always_miss = true;");
					output.println("\t\t}");
				}
			}
			
			String customSettings = VCLMethod.getTemplate(site.getName() + "_recv.vcl");
			if(customSettings.length() > 0) {
				output.println();
				output.println(customSettings);
			}
			output.println("\t}");
			output.println();
			
			out.println("\tinclude \"" + descriptor + "\";");
			output.close();
		}
		
		out.println(" ");
		out.println("\tif (req.request == \"GET\" || req.request == \"HEAD\") {");			
		out.println("\t\treturn(lookup);");
		out.println("\t} else {");
		out.println("\t\treturn(pipe); //HTTP post method"); 
		out.println("\t}");
		
		out.println("}");
		
	}
	
	private final void writeLogFiles(final Site site) {
		//site.getLog().
	}
	
	private PrintWriter getWriter(final File output) {
		output.getParentFile().mkdirs();
		PrintWriter out = null;
		try {
			out = new LinuxPrintWriter(new FileWriter(output));
		} catch(IOException e) {
			throw new IllegalStateException(e.getLocalizedMessage(), e);
		}
		return out;
	}
	
	private void writeMobileDetect() {
		PrintWriter writer = getWriter(new File(root, "mobile/mobile.vcl"));
		writer.println(VCLMethod.getTemplate("mobile.vcl"));
		writer.close();
		
		out.println("include \"mobile/mobile.vcl\";");
		out.println();
	}
	
	private void writeACL() {
		String name = "acl.vcl";
		PrintWriter writer = getWriter(new File(root, "acl/" + name));
		writer.println(VCLMethod.getTemplate(name));
		writer.close();
		
		out.println("include \"acl/acl.vcl\";");
	}
	
	private void writeProbes(final VCL vcl) {
		if(vcl.isUsesProbe()) {			
			PrintWriter out = getWriter(new File(root, "probes/probe.vcl"));
			for (Probe probe : vcl.getProbes()) {
				out.println(probe);
			}
			out.close();
			
			this.out.println("include \"probes/probe.vcl\";");
		}
	}
	
	private void writeDirectors(final VCL vcl) {		
		for (Director director : vcl.getDirectors()) {			
			if(!vcl.isUsesProbe()) {
				for(Backend b : director.getBackends()) {
					b.setProbe(null);
				}
			}
			PrintWriter pout = getWriter(new File(root, "backends/" + director.getName() + ".vcl"));
			pout.println(director);
			pout.close();
			out.println("include \"backends/" + director.getName() + ".vcl\";");
		}
		out.println(" ");
		out.flush();
	}
}
