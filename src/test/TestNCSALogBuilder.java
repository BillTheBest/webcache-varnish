package test;

import com.rbs.cache.AccessLog;
import com.rbs.cache.NCSALogBuilder;

public class TestNCSALogBuilder {

	public static void main(String[] args) {
		
		AccessLog log = new AccessLog();
		log.setName("access_log_hagah");
		
		log.putHost("1aga.com.br");
		log.putHost("1agah.com.br");
		log.putHost("hsaopaulo.com.br");
		log.putHost("hguialocal.com.br");
		log.putHost("www.hagah.com.br");
		log.putHost("saude.hagah.com.br");
		log.putHost("www.ruralbr.com.br");
		
		System.out.println(new NCSALogBuilder().getNCSALog(log));

	}

}
