package com.no9.app.utils;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.servlet.ServletContextURIResolver;
import org.apache.xmlgraphics.util.MimeConstants;

import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FOPUtils implements Serializable {
    private static FopFactory fopFactory = FopFactory.newInstance();
    private static Map<String, Templates> xslTemplatesCache;
    private ServletContext servletContext;

    static {
        resetCache();
    }

    public FOPUtils(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void xmlToPDF(StreamSource xmlSource, Templates template, OutputStream outputStream) throws FOPException, IOException, TransformerException {
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);
        Result res = new SAXResult(fop.getDefaultHandler());
        template.newTransformer().transform(xmlSource, res);
    }

    public Templates getXSLTemplate(String xsltTemplateName) throws TransformerException {
        Templates templates = xslTemplatesCache.get(xsltTemplateName);
        if (templates == null) {
            templates = loadTemplate(xsltTemplateName);
            xslTemplatesCache.put(xsltTemplateName, templates);
        }
        return templates;
    }

    private Templates loadTemplate(String xsltTemplateName) throws TransformerException {
        URIResolver uriResolver = new ServletContextURIResolver(servletContext);
        Source xsltSource = uriResolver.resolve("servlet-context:" + xsltTemplateName, null);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(uriResolver);
        return transformerFactory.newTemplates(xsltSource);
    }

    public static void resetCache() {
        xslTemplatesCache = Collections.synchronizedMap(new HashMap<String, Templates>());
    }
}