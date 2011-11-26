package com.samberan;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.response.XMLWriter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

@SuppressWarnings("unused")
public class XPathResponseWriter implements QueryResponseWriter {

    public void init(NamedList args) {   }

    public String getContentType(SolrQueryRequest request, SolrQueryResponse response) {
        return request.getParams().get("ct");
    }

    public void write(Writer writer, SolrQueryRequest request, SolrQueryResponse response) throws IOException {
        String xpath = request.getParams().get("xpath");
        if("/".equals(xpath)) {
            XMLWriter.writeResponse(writer, request, response);
            return;
        }
        StringWriter xmlResponse = new StringWriter();
        XMLWriter.writeResponse(xmlResponse, request, response);
        Document doc = parse(xmlResponse.toString());
        String result = evaluateXPath(doc, xpath);
        writer.write(result);
    }

    private Document parse(String xml) {
        try {
             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
             DocumentBuilder builder = factory.newDocumentBuilder();
             InputSource is = new InputSource(new StringReader(xml.toString()));
             return builder.parse(is);
        } catch(Exception ex) {
            throw new SolrException(SolrException.ErrorCode.UNKNOWN, ex);
        }
    }

    private String evaluateXPath(Document doc, String xpathString) {
        try {
            XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(xpathString);
            return xpath.evaluate(doc);
        } catch (XPathExpressionException e) {
            throw new SolrException(SolrException.ErrorCode.UNKNOWN, e);
        }
    }
}
