package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rbs.cache.AccessLog;

public class HostReverse {
	
	private static Pattern hostPattern = Pattern.compile("(?:([^\\.]+)\\.)?([a-z\\-A-Z0-9]+)(\\.com(?:\\.br)?)$");

	public static void main(String[] args) throws Exception {
		AccessLog log = new AccessLog();
		log.setName("access_log_hagah");
		
		log.putHost("1aga.com.br");
		log.putHost("1agah.com.br");
		log.putHost("hsaopaulo.com.br");
		log.putHost("hguialocal.com.br");
		log.putHost("www.hagah.com.br");
		log.putHost("saude.hagah.com.br");
		log.putHost("www.ruralbr.com.br");
		
		for(String host : log.getHosts()) {
			Matcher m = hostPattern.matcher(host);
			
			if(m.matches()) {
				System.out.println(m.group(1));
				System.out.println(m.group(2));
				System.out.println(m.group(3));
			}
			System.out.println("---------------------------------");
		}
	}

}
