/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParser;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class XMLSecurityTest {

    private static final String VALID_XML = "<root><child>text</child></root>";

    private static final String XML_WITH_DOCTYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>\n<root>&xxe;</root>";

    private static final String XML_WITH_NAMESPACE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<mets:mets xmlns:mets=\"http://www.loc.gov/METS/\"/>";

    @Test
    void newDocumentBuilderFactoryShouldParseValidXml() throws Exception {
        DocumentBuilder builder = XMLSecurity.newDocumentBuilderFactory().newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(VALID_XML)));
        assertEquals("root", document.getDocumentElement().getNodeName());
    }

    @Test
    void newDocumentBuilderFactoryShouldRejectDocTypeDeclaration() throws Exception {
        DocumentBuilder builder = XMLSecurity.newDocumentBuilderFactory().newDocumentBuilder();
        assertThrows(SAXException.class,
                () -> builder.parse(new InputSource(new StringReader(XML_WITH_DOCTYPE))));
    }

    @Test
    void newSaxParserFactoryShouldRejectDocTypeDeclaration() throws Exception {
        SAXParser parser = XMLSecurity.newSaxParserFactory().newSAXParser();
        assertThrows(SAXException.class,
                () -> parser.parse(new InputSource(new StringReader(XML_WITH_DOCTYPE)),
                        new org.xml.sax.helpers.DefaultHandler()));
    }

    @Test
    void newXmlInputFactoryShouldRejectDocTypeDeclaration() {
        assertThrows(XMLStreamException.class, () -> {
            XMLStreamReader reader = XMLSecurity.newXmlInputFactory()
                    .createXMLStreamReader(new StringReader(XML_WITH_DOCTYPE));
            while (reader.hasNext()) {
                reader.next();
            }
        });
    }

    @Test
    void newSecureSourceShouldRejectDocTypeDeclarationDuringTransformation() throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
                XML_WITH_DOCTYPE.getBytes(StandardCharsets.UTF_8))) {
            assertThrows(TransformerException.class, () -> transformer
                    .transform(XMLSecurity.newSecureSource(inputStream), new StreamResult(new StringWriter())));
        }
    }

    @Test
    void newSecureSourceShouldPreserveNamespacesDuringTransformation() throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
                XML_WITH_NAMESPACE.getBytes(StandardCharsets.UTF_8))) {
            StringWriter writer = new StringWriter();
            transformer.transform(XMLSecurity.newSecureSource(inputStream), new StreamResult(writer));
            String result = writer.toString();
            assertTrue(result.contains("http://www.loc.gov/METS/"),
                    "Namespaces must be preserved by the hardened SAX source");
            assertTrue(result.contains("mets:mets"), "Element prefix must be preserved by the hardened SAX source");
        }
    }

    @Test
    void newTransformerFactoryShouldRestrictExternalAccess() {
        TransformerFactory factory = XMLSecurity.newTransformerFactory();
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_DTD));
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
    }
}
