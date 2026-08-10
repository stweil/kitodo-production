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

package org.kitodo.queryurlimport;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.contentType;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.parameter;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.xebialabs.restito.server.StubServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kitodo.api.externaldatamanagement.DataImport;
import org.kitodo.api.externaldatamanagement.SearchInterfaceType;
import org.kitodo.api.schemaconverter.DataRecord;
import org.kitodo.api.schemaconverter.FileFormat;
import org.kitodo.api.schemaconverter.MetadataFormat;
import org.kitodo.exceptions.NoRecordFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class QueryURLImportTest {

    private static StubServer server;
    private static final String TEST_FILE_PATH = "src/test/resources/sruTestRecord.xml";
    private static final String OPAC_NAME = "Kalliope";
    private static final String RECORD_ID = "1";
    private static final String RECORD_IDENTIFIER = "recordIdentifier";
    private static final String RECORD_IDENTIFIER_VALUE = "12345";
    private static final String VALID_XML = "<root><child>text</child></root>";
    private static final String XML_WITH_DOCTYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>\n<root>&xxe;</root>";
    private static DataImport dataImport;
    private static final int PORT = 8888;
    private static final String SRU = "SRU";

    @BeforeAll
    public static void setup() throws IOException {
        server = new StubServer(PORT).run();
        try (InputStream inputStream = Files.newInputStream(Paths.get(TEST_FILE_PATH))) {
            setupServer(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
        dataImport = createNewDataImport();
    }

    @Test
    public void shouldGetFullRecordById() throws IOException, SAXException, ParserConfigurationException,
            NoRecordFoundException {
        QueryURLImport queryURLImport = new QueryURLImport();
        DataRecord importRecord = queryURLImport.getFullRecordById(dataImport, RECORD_ID);
        assertNotNull(importRecord);
        assertThat("Original data of data record has wrong class!", importRecord.getOriginalData(), instanceOf(String.class));
        Document xmlDocument = parseInputStreamToDocument((String) importRecord.getOriginalData());
        NodeList recordIdentifierNodeList = xmlDocument.getElementsByTagName(RECORD_IDENTIFIER);
        assertEquals(1, recordIdentifierNodeList.getLength(), "Wrong number of record identifiers found!");
        Element recordIdentifierElement = (Element) recordIdentifierNodeList.item(0);
        assertEquals(RECORD_IDENTIFIER_VALUE, recordIdentifierElement.getTextContent(), "Wrong record identifier found!");
    }

    @Test
    public void stringToDocumentShouldParseValidXml() throws Exception {
        QueryURLImport queryURLImport = new QueryURLImport();
        Method stringToDocument = QueryURLImport.class.getDeclaredMethod("stringToDocument", String.class);
        stringToDocument.setAccessible(true);
        Document document = (Document) stringToDocument.invoke(queryURLImport, VALID_XML);
        assertNotNull(document);
        assertEquals("root", document.getDocumentElement().getNodeName(), "Wrong root element found!");
    }

    @Test
    public void stringToDocumentShouldRejectDocTypeDeclaration() throws Exception {
        QueryURLImport queryURLImport = new QueryURLImport();
        Method stringToDocument = QueryURLImport.class.getDeclaredMethod("stringToDocument", String.class);
        stringToDocument.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> stringToDocument.invoke(queryURLImport, XML_WITH_DOCTYPE));
        assertInstanceOf(SAXException.class, exception.getCause(),
                "DOCTYPE declarations must be rejected when parsing XML");
    }

    @Test
    public void nodeToStringShouldSerializeNode() throws Exception {
        QueryURLImport queryURLImport = new QueryURLImport();
        Method stringToDocument = QueryURLImport.class.getDeclaredMethod("stringToDocument", String.class);
        stringToDocument.setAccessible(true);
        Method nodeToString = QueryURLImport.class.getDeclaredMethod("nodeToString", Node.class);
        nodeToString.setAccessible(true);
        Document document = (Document) stringToDocument.invoke(queryURLImport, VALID_XML);
        String result = (String) nodeToString.invoke(queryURLImport, document.getDocumentElement());
        assertNotNull(result);
        assertTrue(result.contains("root"), "Serialized XML must contain the root element");
    }

    private static void setupServer(String serverResponse) {
        // endpoint for importing record by id
        whenHttp(server)
                .match(get("/sru"),
                        parameter("version", "1.2"),
                        parameter("operation", "searchRetrieve"),
                        parameter("recordSchema", "mods"),
                        parameter("maximumRecords", "1"),
                        parameter("query", "ead.id=" + RECORD_ID))
                .then(ok(), contentType("text/xml"), stringContent(serverResponse));
    }

    private Document parseInputStreamToDocument(String inputString) throws ParserConfigurationException,
            IOException, SAXException {
        try (InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8))) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            return documentBuilderFactory.newDocumentBuilder().parse(inputStream);
        }
    }

    private static DataImport createNewDataImport() {
        DataImport dataImport = new DataImport();
        dataImport.setTitle(OPAC_NAME);
        dataImport.setSearchInterfaceType(SearchInterfaceType.valueOf(SRU));
        dataImport.setHost("localhost");
        dataImport.setScheme("http");
        dataImport.setPath("/sru");
        dataImport.setPort(8888);
        dataImport.setIdParameter("ead.id");
        dataImport.setReturnFormat(FileFormat.XML);
        dataImport.setMetadataFormat(MetadataFormat.MODS);
        dataImport.setSearchFields(Collections.singletonMap("Identifier", "ead.id"));
        HashMap<String, String> urlParameters = new HashMap<>();
        urlParameters.put("version", "1.2");
        urlParameters.put("operation", "searchRetrieve");
        urlParameters.put("recordSchema", "mods");
        dataImport.setUrlParameters(urlParameters);
        return dataImport;
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }

}
