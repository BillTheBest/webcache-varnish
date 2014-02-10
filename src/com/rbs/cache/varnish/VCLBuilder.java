package com.rbs.cache.varnish;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rbs.apache.VirtualHost;
import com.rbs.cache.HttpCheck;
import com.rbs.cache.RewriteRule;
import com.rbs.cache.Site;
import com.rbs.cache.varnish.vcl.VCL;
import com.rbs.cache.webcache.Calypso;

public class VCLBuilder {
	
	private XPathFactory xPathfactory = XPathFactory.newInstance();
	private XPath xpath = xPathfactory.newXPath();
	private Document document = null;
	
	private Set<Probe> probes = new TreeSet<Probe>();
	private Map<String, Backend> backends = new HashMap<String, Backend>();
	private Set<Director> directors = new HashSet<Director>();
	private Set<Pattern> ignoredHost = new HashSet<Pattern>();
	private Set<String> ignoredSites = new HashSet<String>();
	
	private final Map<String, String> groupByName = new HashMap<String, String>();
	
	private File baseFolder = null;
	
	private boolean handleSSL = false;
	private boolean validateHosts = false;
	
	private Calypso calypso = new Calypso();
	private Map<String, VirtualHost> virtualHosts = new HashMap<String, VirtualHost>();
	
	private Map<String, Site> sites = new HashMap<String, Site>();
	
	private Pattern replaceAll = Pattern.compile("[/-\\\\.]");

	public void putIgnoredSite(final String site) {
		ignoredSites.add(site);
	}
	
	public File getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
	}

	public boolean isValidateHosts() {
		return validateHosts;
	}

	public void setValidateHosts(final boolean validateHosts) {
		this.validateHosts = validateHosts;
	}

	public boolean isHandleSSL() {
		return handleSSL;
	}

	public void setHandleSSL(final boolean handleSSL) {
		this.handleSSL = handleSSL;
	}
	
	public void addIgnoreHost(final String expression) {
		ignoredHost.add(Pattern.compile(expression));
	}

	public VCLBuilder() {	
		groupByName.put("appfb.zerohora.com.br", "APPFB");
		groupByName.put("wp.clicrbs.com.br", "WP-CLIC");
		groupByName.put("wp.it.com", "WP-IT");
		groupByName.put("appfb.zerohora.com.br", "APPFB");
		groupByName.put("www.tvcom.com.br", "TVCOM");
		groupByName.put("s1.cdnrbs.com.br", "S1CDN");
		groupByName.put("painel.clicrbs.com.br", "PAINEL");
		groupByName.put("m.zerohora.com.br", "MOBZH");
	}
	
	public final VCL newVCL(final InputStream input) {		
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		VCL vcl = new VCL();

		loadExpirations();
		loadBackends();
		loadSites();
		loadDirectors();
		loadCookies();
				
		vcl.setDirectors(directors);
		vcl.setProbes(probes);
		vcl.setSites(sites);
				
		saveExpires();
		
		return vcl;
	}
	
	private PrintWriter createFileWriter(final File target) throws IOException {
		return new LinuxPrintWriter(new PrintWriter(target));
	}
	
	private final void saveExpires() {
		for(Map.Entry<String, VirtualHost> entry : virtualHosts.entrySet()) {
			
			File f = new File(baseFolder, "apache/" + entry.getKey().replaceAll("\\.", "_") + "_" + entry.getValue().getPort() + ".conf");
			f.getParentFile().mkdirs();
			
			PrintWriter out = null;
			
			try {
				out = createFileWriter(f);
			} catch(IOException e) {
				e.printStackTrace();
				continue;
			}
			
			for(RewriteRule rw : entry.getValue().getExpirationRules()) {
				if(rw.getCacheTime() != null) {
					out.println("<LocationMatch \"" +  rw.getPattern()+ "\">");
					out.println("ExpiresActive Off");
					int i = Integer.valueOf(rw.getCacheTime());
					if(i > 10800) {
						out.println("Header set Cache-Control \"public, max-age=" + rw.getCacheTime() + ", s-maxage=10800\"");
					} else {
						if(i < 0) {
							out.println("Header set Cache-Control \"private, no-cache\"");
							out.println("Header set Surrogate-Control \"no-store\"");
						} else {
							out.println("Header set Cache-Control \"public, max-age=" + rw.getCacheTime() + "\"");
						}
					}
					out.println("</LocationMatch>");
					out.println();
				}
			}
			out.close();
		
			if(f.length() == 0) {
				f.delete();
			}
		}
	}
	
	private final void loadBackends() {
		try {
			XPathExpression expr = xpath.compile("//HOST");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("HOST");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element host = (Element) list.item(x);
				
				if(!handleSSL && "443".equals(host.getAttribute("PORT"))) {
					continue;
				}
				
				Backend backend = new Backend();
				backend.setHost(host.getAttribute("NAME"));
				backend.setPort(host.getAttribute("PORT"));
				
				backends.put(host.getAttribute("ID"), backend);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isIgnoredDomain(final String domain) {
		if(ignoredHost != null) {
			for(Pattern $domain : ignoredHost) {
				if($domain.matcher(domain).matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isIgnoredSite(final Site site) {
		String siteName = site.getName();
		return ignoredSites.contains(siteName + site.getUrl());
	}
	
	private final void loadSites() {
		try {
			XPathExpression expr = xpath.compile("//SITE");			
			XPathExpression children = xpath.compile("ALIAS");
			XPathExpression cacheabilityRule = xpath.compile("CACHEABILITY/CACHEABILITYRULE");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("SITE");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element $site = (Element) list.item(x);
				
				NodeList mappings = (NodeList) children.evaluate($site, XPathConstants.NODESET);
				
				NodeList cacheability = (NodeList) cacheabilityRule.evaluate($site, XPathConstants.NODESET);
				
				if(isIgnoredDomain($site.getAttribute("NAME")) || (!handleSSL && "443".equals($site.getAttribute("PORT")))) {
					continue;
				}
				
				Site site = new Site();
				site.setName($site.getAttribute("NAME"));
				site.setPort($site.getAttribute("PORT"));
				site.setUrl($site.getAttribute("URL"));
				site.getLog().putHost($site.getAttribute("NAME"));
				
				if(isIgnoredSite(site)) {
					continue;
				}
				
				site.getLog().setName("access_log");
				
				if(site.getName().equals("it")) {
					site.getLog().setName("access_log_it");
				} else {
					if(site.getName().equals("www.hagah.com.br")) {
						site.getLog().setName("access_log_hagah");
					}
				}
				
				VirtualHost virtualHost = new VirtualHost();
				virtualHost.setHost(site.getName());
				virtualHost.setPort(site.getPort());
				virtualHosts.put(site.getName(), virtualHost);
				
				String host = $site.getAttribute("NAME");
				boolean skip = false;
				
				if("painel.clicrbs.com.br".equals(host)) {
					site = sites.get(normalize("www.clicrbs.com.br", "80", null));
					System.out.println("skipped for painel.clicrbs.com.br");
					site.getAliases().add(host);
					skip = true;
				}
				
				if("bonus.hagah.com.br".equals(host)) {
					site = sites.get(normalize("www.hagah.com.br", "80", null));
					System.out.println("skipped for bonus.hagah.com.br");
					site.getAliases().add(host);
					skip = true;
				}
				
				String key = normalize($site.getAttribute("NAME"), $site.getAttribute("PORT"), $site.getAttribute("URL"));
				
				//ALIASES
				for(int y=0; y<mappings.getLength(); y++) {
					Element vHost = (Element) mappings.item(y);
					
					if(isIgnoredDomain(vHost.getAttribute("NAME"))) {
						continue;
					}
					
					if(validateHosts && !HttpCheck.check(vHost.getAttribute("NAME"), 80)) {
						System.err.println(site.getName() + ":" + site.getPort() + ";" + vHost.getAttribute("NAME"));
						continue;
					}
					
					String url = vHost.getAttribute("URL");
					if(url != null && url.length() < 2) {
						url = "";
					}
					
					String name = vHost.getAttribute("NAME");
				
					site.getAliases().add(name + url);
					site.getLog().putHost(name);
					
					if($site.getAttribute("NAME").equals("it")) {
						//System.out.println("log.putHost(\"" + vHost.getAttribute("NAME") + "\");");
					}
				}
				
				//CACHEABILITY RULES
				for(int y=0; y<cacheability.getLength(); y++) {
					Element cacheRule = (Element) cacheability.item(y);
					boolean pass = "NO".equals(cacheRule.getAttribute("CACHE"));
					
					NodeList selectors = cacheRule.getElementsByTagName("SELECTORS");
					
					RewriteRule rw = new RewriteRule(((Element) ((Element) selectors.item(0)).getElementsByTagName("URLEXP").item(0)).getAttribute("EXP"));
					
					if(pass) {
						rw.setCacheTime("-1");
						site.getUrisForHitForPass().add(rw.getPattern());
					} else {
						NodeList expirationRef = cacheRule.getElementsByTagName("EXPIRATIONREF");
						if(expirationRef.getLength() > 0) {
							rw.setCacheTime(calypso.getExpirationRule(((Element) expirationRef.item(0)).getAttribute("EXPREF")).getSeconds());
						}
					}
					virtualHost.addExpirationRule(rw);
				}
				
				//site used as alias too
				if(site.getName().indexOf('.') > 0){
					String url = site.getUrl();
					if(url != null && url.length() < 2) {
						url = "";
					}
					site.getAliases().add(site.getName() + url);
				}
				
				if(!skip) {
					sites.put(key, site);
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private final void loadDirectors() {
		try {
			XPathExpression expr = xpath.compile("//VIRTUALHOSTMAP");
			
			XPathExpression children = xpath.compile("HOSTREF");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("VIRTUALHOSTMAP");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element vHostMap = (Element) list.item(x);
				
				NodeList mappings = (NodeList) children.evaluate(vHostMap, XPathConstants.NODESET);
				
				String key = normalize(vHostMap.getAttribute("NAME"), vHostMap.getAttribute("PORT"), vHostMap.getAttribute("URL"));
				
				Site site = sites.get(key);
				if(site == null) {
					//ignored sites will be skipped
					System.out.println("Skipping for " + key);
					continue;
				}

				Director director = new Director();
				director.setName(key);
				director.setBackends(new LinkedList<Backend>());

				Probe probe = ProbeBuilder.getInstance().getProbeFor(site.getName(), site.getPort());
				for(int y=0; y<mappings.getLength(); y++) {
					Element vHost = (Element) mappings.item(y);
					Backend backend = backends.get(vHost.getAttribute("HOSTID"));
					
					if(backend.getProbe() == null || backend.getProbe().getUrl() == "/") {
						backend.setProbe(probe);
						probes.add(probe);
					}
					director.getBackends().add(backend);
				}
				
				site.setDirector(director);
				
				fillSiteGroup(site);
				
				directors.add(director);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private void fillSiteGroup(final Site site) {
		site.setGroup(createGroup(site.getDirector()));		
		
		if(site.getGroup() == null) {
			if(site.getUrl().indexOf("temporeal") >= 0) {
				site.setGroup("IT");
			} else {
				site.setGroup(groupByName.get(site.getName()));
			}
		}
	}
	
	private String createGroup(final Director director) {
		int port = Integer.valueOf(director.getBackends().get(0).getPort());
		switch(port) {
		case 7778 : return "CLIC";
		case 7779 : return "IT";
		case 7786 : return "HAGAH";
		}
		return null;
	}
	
	private final void loadExpirations() {
		try {
			XPathExpression expr = xpath.compile("//EXPIRATIONRULE");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("EXPIRATIONRULE");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element expirationRule = (Element) list.item(x);				
				calypso.addExpirationRule(expirationRule.getAttribute("NAME"), expirationRule.getAttribute("EXPIRESECS"));				
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private final void loadCookies() {
		try {
			XPathExpression expr = xpath.compile("//MULTIVERSIONCOOKIESRULE");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("MULTIVERSIONCOOKIESRULE");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element expirationRule = (Element) list.item(x);				
				String name = expirationRule.getAttribute("COOKIE_NAME");	
				System.out.println(name);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private String normalize(final String name, final String port, final String url) {
		StringBuilder stb = new StringBuilder();
		stb.append(replaceAll.matcher(name).replaceAll("_"));
		stb.append('_').append(port);
		if(url != null && url.length() > 0 && !url.equals("/")) {
			stb.append(replaceAll.matcher(url.toLowerCase()).replaceAll("_"));
		}
		String key = stb.toString();
		while(key.charAt(key.length() - 1) == '_') {
			key = key.substring(0, key.lastIndexOf('_'));
		}
		return key;
	}
}
