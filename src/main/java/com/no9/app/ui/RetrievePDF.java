package com.no9.app.ui;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

public class RetrievePDF extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FopFactory fopFactory = FopFactory.newInstance();

        File xsltFile = getResourceFile("HelloWorld.xsl");
        StreamSource transformSource = new StreamSource(xsltFile);
        try {
            Transformer xslfoTransformer = getTransformer(transformSource);
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"HelloWorld.pdf\"");
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, resp.getOutputStream());
            Result res = new SAXResult(fop.getDefaultHandler());

            StreamSource source = new StreamSource(getResourceFile("Hello.xml"));
            xslfoTransformer.transform(source, res);
        } catch (Exception ex) {
            resp.setContentType("text/html");
            resp.getOutputStream().println("<h1>Exception</h1>");
            resp.getOutputStream().println(ex.getMessage());
        }
    }

    private static Transformer getTransformer(StreamSource streamSource) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        return factory.newTransformer(streamSource);
    }

    private File getResourceFile(String resourceName) {
        return new File(getClass().getClassLoader().getResource(resourceName).getFile());
    }
}