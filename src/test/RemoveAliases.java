package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RemoveAliases {

	public static void main(String[] args) throws Exception {
		
		//String[] token = "it:80;ahora.clicrbs.com.br".split(";");
		
		BufferedReader reader = new BufferedReader(new FileReader("/temp/webcache/hosts_removidos_webcache_velma.txt"));
		String line = null;
		String[] token = null;
		
		File f = new File("/temp/webcache/webcache.xml");
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
		
		while((line = reader.readLine()) != null) {
			token = line.split(";");
			
			if(token == null || token.length < 2) {
				continue;
			}
			
			String alias = token[1];
			
			System.out.println("Removing " + alias);
			
			NodeList list = document.getElementsByTagName("ALIAS");
			
			for(int x=0;x<list.getLength(); x++) {
				Element r = (Element) list.item(x);
				
				if(alias.equals(r.getAttribute("NAME"))) {
					r.getParentNode().removeChild(r);
					//reset
					list = document.getElementsByTagName("ALIAS");
					x = 0;
				}
				
			}
		
		}
		
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.transform(new DOMSource(document), new StreamResult(new FileOutputStream(f)));
		
		reader.close();
	}

}
