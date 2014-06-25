package test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws Exception {

		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection) new URL("http://www.clicrbs.com.br/jsp/index.jsp"/*"http://www.clicrbs.com.br/jsp/redirect.jsp?tab=00001&newsID=0&subTab=00000" *//*"http://clicrbs.com.br/"*/).openConnection();
		//con.setRequestMethod("DELETE");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Lincolm Testando Cache)");
		con.setRequestProperty("Cookie", "clicRBSv2.prefs=local=(18):uf=(2):usuario=-none");
		
		int c = 0;
		InputStream in = con.getInputStream();
		
		for(Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		
		System.out.println("-----------------------------------");
		while((c = in.read()) != -1) {
			System.out.print((char) c);
		}
		
		System.out.println();
		System.out.println("------------------");
	}

}
