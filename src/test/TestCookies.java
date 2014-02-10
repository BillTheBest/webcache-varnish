package test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestCookies {
	
	private final XPathFactory xPathfactory;
	private final Map<String, String> cookies = new HashMap<String, String>();
	private final Document document;
	
	public TestCookies(final File file) {		
		try {
			 document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		} catch(Exception e) {
			throw new IllegalStateException(e.getLocalizedMessage(), e);
		}
		xPathfactory = XPathFactory.newInstance();
	}
	
	private void loadCookies() throws Exception {		
		XPath xpath = xPathfactory.newXPath();
		
		try {
			XPathExpression expr = xpath.compile("//MULTIVERSIONCOOKIESRULE");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("MULTIVERSIONCOOKIESRULE");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element expirationRule = (Element) list.item(x);				
				cookies.put(expirationRule.getAttribute("NAME"), expirationRule.getAttribute("COOKIE_NAME"));
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	private void assignCookies() throws Exception {
		XPath xpath = xPathfactory.newXPath();
		
		try {
			XPathExpression expr = xpath.compile("//MVCREF");
			XPathExpression nodeUrl = xpath.compile("parent::*/SELECTORS/URLEXP/@EXP");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("MVCREF"); //MVCREF
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element mvcRef = (Element) list.item(x);				
				System.out.println(cookies.get(mvcRef.getAttribute("MVCREF")) + "\t" + nodeUrl.evaluate(mvcRef, XPathConstants.STRING));			
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestCookies t = new TestCookies(new File("/temp/webcache/webcache_prd.xml"));
		t.loadCookies();
		t.assignCookies();
	}

}
