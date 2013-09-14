package com.no9.app.ui;

import com.no9.app.utils.FOPUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

public class RetrievePDF extends HttpServlet {
    private static final String XML_SOURCE_RESOURCE_NAME = "Hello.xml";
    private static final String XSLT_TEMPLATE_RESOURCE_NAME = "/HelloWorld.xsl";
    private static final String RESULT_FILE_NAME = "HelloWorld.pdf";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + RESULT_FILE_NAME + "\"");

            StreamSource xmlSource = xmlAsStreamSource(getResourceFile(XML_SOURCE_RESOURCE_NAME));

            FOPUtils fopUtils = new FOPUtils(getServletContext());
            fopUtils.xmlToPDF(xmlSource, fopUtils.getXSLTemplate(XSLT_TEMPLATE_RESOURCE_NAME), resp.getOutputStream());
        } catch (Exception ex) {
            resp.setContentType("text/html");
            resp.getOutputStream().println("<h1>Exception</h1>");
            resp.getOutputStream().println(ex.getMessage());
        }
    }

    private StreamSource xmlAsStreamSource(File xmlFile) {
        return new StreamSource(xmlFile);
    }

    private File getResourceFile(String resourceName) {
        return new File(RetrievePDF.class.getClassLoader().getResource(resourceName).getFile());
    }
}