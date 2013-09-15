package com.no9.app.ui;

import com.no9.app.utils.FOPUtils;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RetrievePDFTest {
    private ServletTester servletTester;
    private HttpTester request;

    @Before
    public void setUp() throws Exception {
        servletTester = new ServletTester();
        servletTester.setContextPath("/");
        servletTester.setResourceBase("src/main/webapp");
        servletTester.addServlet(RetrievePDF.class, "/RetrievePDF");
        servletTester.addServlet(org.mortbay.jetty.servlet.DefaultServlet.class, "/*");
        servletTester.start();

        request = new HttpTester();
        request.setMethod("GET");
        request.setURI("/RetrievePDF");
        request.setVersion("HTTP/1.0");
    }

    @Test
    public void should_get_same_PDF_result_over_multiple_calls() throws Exception {
        HttpTester firstResponse = getPDFResponse();
        HttpTester secondResponse = getPDFResponse();

        assertEquals(firstResponse.getContent().length(), secondResponse.getContent().length());
    }

    @Test
    public void should_return_an_error_page() throws Exception {
        new MockUp<FOPUtils>() {
            @Mock
            public Templates getXSLTemplate(String xsltTemplateName) throws TransformerException {
                throw new TransformerException("Mock Exception");
            }
        };

        HttpTester response = getResponse();
        assertEquals(200, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertTrue(response.getContent().contains("Mock Exception"));
    }

    private HttpTester getPDFResponse() throws Exception {
        HttpTester response = getResponse();

        assertEquals(200, response.getStatus());
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getContent().startsWith("%PDF-1.4"));

        return response;
    }

    private HttpTester getResponse() throws Exception {
        HttpTester response = new HttpTester();
        response.parse(servletTester.getResponses(request.generate()));
        return response;
    }
}
