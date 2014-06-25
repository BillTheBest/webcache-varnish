package test;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class TestDevSatiro {

	public static void main(String[] args) throws Exception {
		revalidate(args);
	}
	
	private static void revalidate(String[] args) throws Exception {
		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader("/response_dev.txt"));
		
		HttpURLConnection.setFollowRedirects(false);
		Pattern split = Pattern.compile(";");
		String[] tokens = null;
		
		while((line = reader.readLine()) != null) {
			tokens = split.split(line);
			
			String expected = tokens[0];
			String test = Integer.toString(doTest(tokens[1]));
			
			if(!expected.equals(test)) {
				System.err.println("Fail: " + tokens[1] + " with [" + test + "]. Expected [" + expected + "]");
			}
		}
		
		reader.close();
	}
	
	private static void createTest(String[] args) throws Exception {
		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader("/temp/test_dev_satiro.txt"));
		
		HttpURLConnection.setFollowRedirects(false);
		
		System.setOut(new PrintStream(new FileOutputStream("/response_dev.txt")));
		
		while((line = reader.readLine()) != null) {
			String url = "http://" + line + "/";
			System.out.println(doTest(url) + ";" + url);
		}
		
		reader.close();
	}
	
	private static final int doTest(final String url) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Lincolm Aguiar");
		con.connect();
		
		int c = 0;
		InputStream in = null;
		
		try {
			in = con.getInputStream();
		} catch(IOException e) {
			in = con.getErrorStream();
		}
		
		int response = con.getResponseCode();
		
		in.close();
		con.disconnect();
		
		return response;
	}

}
