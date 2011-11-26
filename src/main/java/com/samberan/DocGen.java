package com.samberan.solr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.fluttercode.datafactory.impl.DataFactory;


public class DocGen {
	public static void main(String[] args) throws IOException {
		DataFactory df = new DataFactory();

		UpdateRequest req = new UpdateRequest();
		for(int i =0; i < 100; i ++) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("firstName", df.getFirstName());
			doc.setField("lastName", df.getLastName());
			doc.setField("address", df.getAddress());
			doc.setField("city", df.getCity());
			doc.setField("grade", df.getNumberBetween(1, 12));
			doc.setField("email", df.getEmailAddress());
			doc.setField("type", "student");
			
			req.add(doc);
			
			
		}
		File file = new File("/home/berasa/Dropbox/Code/example/students.xml");
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileUtils.writeStringToFile(file, prettyFormat(req.getXML()));
	}
	
	public static String prettyFormat(String input, int indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

	public static String prettyFormat(String input) {
	    return prettyFormat(input, 2);
	}
}


