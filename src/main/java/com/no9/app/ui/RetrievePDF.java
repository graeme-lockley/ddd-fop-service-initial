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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"HelloWorld.pdf\"");

            StreamSource xmlSource = xmlAsStreamSource(getResourceFile("Hello.xml"));

            FOPUtils fopUtils = new FOPUtils(getServletContext());
            fopUtils.xmlToPDF(xmlSource, fopUtils.getXSLTemplate("HelloWorld.xsl"), resp.getOutputStream());
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