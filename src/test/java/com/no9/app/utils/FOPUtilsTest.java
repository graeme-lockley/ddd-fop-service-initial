package com.no9.app.utils;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.Assert.*;

public class FOPUtilsTest {
    private ServletContext servletContext;
    private FOPUtils fopUtils;
    private int numberOfCallsToGetResourceAsStream;

    @Before
    public void setUp() throws Exception {
        servletContext = new MockUp<ServletContext>() {
            @Mock
            public InputStream getResourceAsStream(String resourceName) {
                numberOfCallsToGetResourceAsStream += 1;
                try {
                    return new FileInputStream("src/main/webapp" + resourceName);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        }.getMockInstance();
        FOPUtils.resetCache();
        fopUtils = new FOPUtils(servletContext);
        numberOfCallsToGetResourceAsStream = 0;
    }

    @Test
    public void should_get_an_XML_template() throws Exception {
        Templates template = fopUtils.getXSLTemplate("/HelloWorld.xsl");

        assertNotNull(template);
        assertEquals(1, numberOfCallsToGetResourceAsStream);
    }

    @Test
    public void should_show_that_the_template_is_pulled_out_of_the_cache() throws Exception {
        Templates firstTemplate = fopUtils.getXSLTemplate("/HelloWorld.xsl");
        Templates secondTemplate = fopUtils.getXSLTemplate("/HelloWorld.xsl");

        assertEquals(firstTemplate, secondTemplate);
        assertEquals(1, numberOfCallsToGetResourceAsStream);
    }

    @Test(expected = javax.xml.transform.TransformerException.class)
    public void should_raise_an_exception_for_an_unknown_template() throws Exception {
        fopUtils.getXSLTemplate("/Bob.xsl");
    }

    @Test
    public void should_render_the_XML_content_using_the_XSLT() throws Exception {
        Templates template = fopUtils.getXSLTemplate("/HelloWorld.xsl");
        StreamSource xmlSource = xmlAsStreamSource(getResourceFile("Hello.xml"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fopUtils.xmlToPDF(xmlSource, template, outputStream);

        String pdfOutput = outputStream.toString();
        assertTrue(pdfOutput.startsWith("%PDF-1.4"));
    }

    private StreamSource xmlAsStreamSource(File xmlFile) {
        return new StreamSource(xmlFile);
    }

    private File getResourceFile(String resourceName) {
        return new File(FOPUtilsTest.class.getClassLoader().getResource(resourceName).getFile());
    }
}

