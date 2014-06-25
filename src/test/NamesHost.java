package test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rbs.cache.AccessLog;
import com.rbs.cache.varnish.Probe;
import com.rbs.cache.varnish.ProbeBuilder;

public class NamesHost {
	
	private static File webCacheXml = new File("/temp/webcache/webcache_PEDRITAVM.xml");
	
	public static void main(String[] args) throws Exception {
		//hosts(args);
		//sites(args);
		//names(args);
		etcHosts(args);
	}
	
	public static void names(String[] args) throws Exception {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webCacheXml);

		XPathExpression expr = xpath.compile("//SITE");
		
		NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		
		String[] ignored = new String[] {
			"www.comunidade.clicrbs.com.br",
			"minutoaminuto.clicesportes.com.br"
		};
		
		Arrays.sort(ignored);
		
		for (int x = 0; x < list.getLength(); x++) {
			Element e = (Element) list.item(x);
			String host = e.getAttribute("NAME");
			String port = e.getAttribute("PORT");
			String uri = e.getAttribute("URL");
			
			
			if(Arrays.binarySearch(ignored, 0, ignored.length, host) > -1) {
				continue;
			}
			
			if(uri != null && uri.indexOf("manifesto470") >= 0) {
				continue;
			}
			
			Probe probe = ProbeBuilder.getInstance().createProbe(host, port, uri);
			if(probe == null) {
				continue;
			}
			System.out.println(probe);
			System.out.println("-----------------------------------------------");
		}		
	}
	
	public static void etcHosts(String[] args) throws Exception {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webCacheXml);

		XPathExpression expr = xpath.compile("//SITE");
		XPathExpression alias = xpath.compile("ALIAS");
		
		XPathExpression accessLog = xpath.compile("ACCESSLOG");
		
		NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		
		List<AccessLog> logs = new LinkedList<AccessLog>();
		
		AccessLog defaultLog = new AccessLog();
		defaultLog.setName("access_log");
		logs.add(defaultLog);
		
		for (int x = 0; x < list.getLength(); x++) {
			Element e = (Element) list.item(x);
			
			if("443".equals(e.getAttribute("PORT"))) {
				continue;
			}
			
			Set<String> unique = new HashSet<String>();
			NodeList aliases = (NodeList) alias.evaluate(e, XPathConstants.NODESET);
			
			String target = "10.242.20.38\t";
			
			System.out.println(target + e.getAttribute("NAME"));
			
			if("it".equals(e.getAttribute("NAME"))) {
				for (int y = 0; y < aliases.getLength(); y++) {
					String host = ((Element) aliases.item(y)).getAttribute("NAME");
					if(unique.add(host)) {
						System.out.println(target + host);
					}				
				}
			}
			//System.out.println();
		}
	}
	
	public static void sites(String[] args) throws Exception {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webCacheXml);

		XPathExpression expr = xpath.compile("//SITE");
		XPathExpression alias = xpath.compile("ALIAS");
		
		XPathExpression accessLog = xpath.compile("ACCESSLOG");
		
		NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		
		List<AccessLog> logs = new LinkedList<AccessLog>();
		
		AccessLog defaultLog = new AccessLog();
		defaultLog.setName("access_log");
		logs.add(defaultLog);
		
		for (int x = 0; x < list.getLength(); x++) {
			Element e = (Element) list.item(x);
			
			if("443".equals(e.getAttribute("PORT"))) {
				continue;
			}
			
			//System.out.println(e.getAttribute("NAME"));
			
			//AccessLog log = new AccessLog();
			
			/*
			String name = e.getAttribute("NAME");
			if(name.equals("it")) {
				log.setName("access_log_it");
			} else {
				if(name.equals("www.clicrbs.com.br")) {
					log.setName("access_log_clic");
				} else {
					if(name.equals("zerohora.com")) {
						log.setName("access_log_zerohora_com");
					} else {
						if(name.indexOf("hagah.com.br") >= 0) {
							log.setName("access_log_hagah");
						} else {
							if(name.equals("painel.clicrbs.com.br")) {
								log.setName("access_log_painel");
							} else {
								if(name.indexOf("wp.") >= 0) {
									log.setName("access_log_wp");
								} else {
									if(name.equals("s1.cdnrbs.com.br")) {
										log.setName("access_log_s1cdn");
									} else {
										if(name.equals("m.zerohora.com.br")) {
											log.setName("access_log_mobile_zh");
										} else {
											if(name.equals("appfb.zerohora.com.br")) {
												log.setName("access_log_appfb");
											} else {
												if(name.equals("gruporbs.com.br")) {
													log.setName("access_log_gruporbs");
												} else {
													if(name.equals("atlantida.clicrbs.com.br")) {
														log.setName("access_log_atlantida");
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(log.getName() == null) {
				//no needed log
				//System.out.println("\tIgnored: " + e.getAttribute("NAME"));
				continue;
			}
			*/
			Node nodeAccessLog = (Node) accessLog.evaluate(e, XPathConstants.NODE);
			AccessLog log = null;
			if(nodeAccessLog != null) {
				log = new AccessLog();
				log.setName(((Element) nodeAccessLog).getAttribute("FILENAME"));
				logs.add(log);
			} else {
				log = defaultLog;
			}
			
			NodeList aliases = (NodeList) alias.evaluate(e, XPathConstants.NODESET);
			
			System.out.println(e.getAttribute("NAME") + "\t[" + getHost(e.getAttribute("NAME")) + "]");
			
			for (int y = 0; y < aliases.getLength(); y++) {
				String host = ((Element) aliases.item(y)).getAttribute("NAME");
				log.putHost(host);				
				System.out.println("\t" + host + "\t[" + getHost(host) + "]");
			}
			System.out.println();
		}
	}
	
	private static final String getHost(final String host) {
		try {
			return InetAddress.getByName(host).getHostAddress();
		} catch (UnknownHostException e) {
			return "Unknown Host";
		}
	}

	public static void hosts(String[] args) throws Exception {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webCacheXml);

		XPathExpression expr = xpath.compile("//HOST");	
		
		NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		
		for (int x = 0; x < list.getLength(); x++) {
			Element e = (Element) list.item(x);
			
			System.out.println(e.getAttribute("NAME") + ", porta " + e.getAttribute("PORT") + " para HTTP");
		}
		
	}

}
