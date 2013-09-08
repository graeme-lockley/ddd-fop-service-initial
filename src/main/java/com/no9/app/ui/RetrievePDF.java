package com.no9.app.ui;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.servlet.ServletContextURIResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RetrievePDF extends HttpServlet {
    private static FopFactory fopFactory = FopFactory.newInstance();
    private static Map<String, Templates> xslTemplatesCache = Collections.synchronizedMap(new HashMap<String, Templates>());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"HelloWorld.pdf\"");

            StreamSource xmlSource = xmlAsStreamSource(getResourceFile("Hello.xml"));
            xmlToPDF(xmlSource, getXSLTemplate("HelloWorld.xsl"), resp.getOutputStream());
        } catch (Exception ex) {
            resp.setContentType("text/html");
            resp.getOutputStream().println("<h1>Exception</h1>");
            resp.getOutputStream().println(ex.getMessage());
        }
    }

    private void xmlToPDF(StreamSource xmlSource, Templates templates, OutputStream outputStream) throws FOPException, IOException, TransformerException {
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);
        Result res = new SAXResult(fop.getDefaultHandler());
        templates.newTransformer().transform(xmlSource, res);
    }

    private StreamSource xmlAsStreamSource(File xmlFile) {
        return new StreamSource(xmlFile);
    }

    private Templates getXSLTemplate(String xsltTemplateName) throws TransformerException {
        Templates templates = xslTemplatesCache.get(xsltTemplateName);
        if (templates == null) {
            templates = loadTemplate(xsltTemplateName);
            xslTemplatesCache.put(xsltTemplateName, templates);
        }
        return templates;
    }

    private Templates loadTemplate(String xsltTemplateName) throws TransformerException {
        URIResolver uriResolver = new ServletContextURIResolver(getServletContext());
        Source xsltSource = uriResolver.resolve("servlet-context:" + xsltTemplateName, null);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(uriResolver);
        return transformerFactory.newTemplates(xsltSource);
    }

    private File getResourceFile(String resourceName) {
        return new File(RetrievePDF.class.getClassLoader().getResource(resourceName).getFile());
    }
}