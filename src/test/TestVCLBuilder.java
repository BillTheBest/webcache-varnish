package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.rbs.cache.varnish.VCLBuilder;
import com.rbs.cache.varnish.VCLWriter;
import com.rbs.cache.varnish.vcl.VCL;

public class TestVCLBuilder {

	public static void main(final String[] args) throws IOException {
		VCLBuilder builder = new VCLBuilder();
		builder.setValidateHosts(true);
		builder.putIgnoredSite("www.clicrbs.com.br/manifesto470/");
		builder.putIgnoredSite("zerohora.com/transferencia");
		
		int ip = 100;
		
		//classificados.clicrbs.com.br
		
		//builder.addIgnoreHost("(.*hagah(?:guialocal)?\\.com\\.br|minutoaminuto\\.clicesportes\\.com\\.br|(?:^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$))");
		builder.addIgnoreHost("(classificados\\.clicrbs\\.com\\.br|www\\.comunidade\\.clicrbs\\.com\\.br|minutoaminuto\\.clicesportes\\.com\\.br|(?:^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$))");
				
		File wcFile = null;
		File vclFile = null;
		
		File target = new File("/temp/webcache/target_hlg");
		if(target.exists()) {
			wcFile = new File("/temp/webcache/webcache_hlg.xml");
			vclFile = new File("/temp/webcache/hlg/default.vcl");
		} else {
			target = new File("/temp/webcache/target_dsv");
			if(target.exists()) {
				wcFile = new File("/temp/webcache/webcache_dsv.xml");
				vclFile = new File("/temp/webcache/dsv/default.vcl");
			} else {
				wcFile = new File("/temp/webcache/webcache_prd_" + ip + ".xml");
				vclFile = new File("/temp/webcache/prd" + ip + "/default.vcl");
				vclFile.getParentFile().mkdirs();
			}
		}
		
		// /temp/webcache/webcache_hlg.xml /temp/webcache/prd/default.vcl
		
		File f = wcFile;
		if(!f.exists()) {
			System.err.println(args[0] + " not found");
			return;
		}
		
		File f2 = vclFile;
		if(f2.isDirectory()) {
			f2.mkdirs();
		} else {
			f2.getParentFile().mkdirs();
		}
		
		System.out.println(f);
		System.out.println(f2);
		
		builder.setBaseFolder(f2.getParentFile());
		
		FileInputStream fis = new FileInputStream(f);
		VCL vcl = builder.newVCL(fis);
		
		if(args.length > 2) {
			Pattern split = Pattern.compile("(.+)=(.*)");
			
			for(int x=2; x<args.length; x++) {
				Matcher m = split.matcher(args[x]);
				if(m.matches()) {
					String name = m.group(1);
					String value = m.group(2);
					
					int i = name.hashCode();
					switch(i) {
						case 926483871 : vcl.setPrintHitFlag(Boolean.valueOf(value)); break; 
						case 115180 : vcl.setDefaultTtl(value); break;
						case 98615224 : vcl.setDefaultGrace(value); break;
						case -1309235404 : vcl.setGenerateAutoExpires(Boolean.valueOf(value)); break;
					}
				}
			}
		}
		
		new VCLWriter(f2).write(vcl);
		doZip(f2.getParentFile());
		System.out.println("Done");
	}
	
	private static final void doZip(final File folder) throws IOException {
		File varnishZip = new File(folder, "varnish.zip");
		FileOutputStream fos = new FileOutputStream(varnishZip);
		ZipOutputStream zip = new ZipOutputStream(fos);
		
		File[] children = folder.listFiles();
		for(File child : children) {
			zip(zip, varnishZip, child);
		}
		
		fos.close();
	}
	
	private static final void zip(final ZipOutputStream zip, final File varnishZip, final File folder) throws IOException {
		ZipEntry entry = new ZipEntry(folder.getName());
		zip.putNextEntry(entry);
		
		if(folder.isDirectory()) {
			File[] children = folder.listFiles();
			for(File child : children) {
				zip(zip, varnishZip, child);
			}
		} else {
			byte[] b = new byte[(int) folder.length()];
			FileInputStream fis = new FileInputStream(folder);
			fis.read(b);
			fis.close();
			zip.write(b);
		}
		
		zip.closeEntry();
	}

}
