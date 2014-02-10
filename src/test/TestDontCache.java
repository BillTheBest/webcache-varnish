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

public class TestDontCache {
	
	private final XPathFactory xPathfactory;
	private final Map<String, String> cookies = new HashMap<String, String>();
	private final Document document;
	
	public TestDontCache(final File file) {		
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
			XPathExpression expr = xpath.compile("//CACHEABILITYRULE[@CACHE='NO']/SELECTORS/URLEXP");
			
			NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			if(list.getLength() == 0) {
				 list = document.getDocumentElement().getElementsByTagName("CACHEABILITYRULE");
			}
			
			for(int x=0; x<list.getLength(); x++) {
				Element elem = (Element) list.item(x);
				
				System.out.println(((Element) elem.getParentNode().getParentNode().getParentNode().getParentNode()).getAttribute("NAME"));
				System.out.println(elem.getAttribute("EXP"));
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestDontCache t = new TestDontCache(new File("/temp/webcache/webcache_prd.xml"));
		t.loadCookies();
	}

}
