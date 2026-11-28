/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.dom;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DOMNormalizerTest extends TestCase {
    private final List<String> errors = new ArrayList<String>();

    public void testValidatesNestedEntityElementsAgainstDtd() throws Exception {
        Document document = parse("<!DOCTYPE root [<!ELEMENT root (item)>"
                + "<!ELEMENT item (#PCDATA)><!ENTITY inner '<item>value</item>'>"
                + "<!ENTITY outer '&inner;'>]><root>&outer;</root>");
        Node outer = document.getDocumentElement().getFirstChild();
        Node inner = outer.getFirstChild();
        Node item = inner.getFirstChild();

        normalize(document, true);

        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(Node.ENTITY_REFERENCE_NODE, outer.getNodeType());
        assertEquals(Node.ENTITY_REFERENCE_NODE, inner.getNodeType());
        assertSame(outer, document.getDocumentElement().getFirstChild());
        assertSame(inner, outer.getFirstChild());
        assertSame(item, inner.getFirstChild());
        assertEquals("value", item.getTextContent());
    }

    public void testSchemaValidationKeepsReplacementContentReadOnly() throws Exception {
        String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                + "<xs:element name='root'><xs:complexType><xs:sequence>"
                + "<xs:element name='item' minOccurs='2' maxOccurs='2'><xs:complexType><xs:simpleContent>"
                + "<xs:extension base='xs:QName'><xs:attribute name='status' default='new'/>"
                + "</xs:extension></xs:simpleContent></xs:complexType></xs:element>"
                + "</xs:sequence></xs:complexType></xs:element></xs:schema>";
        Document document = parse("<!DOCTYPE root [<!ENTITY content "
                + "'<item xmlns:t=\"urn:test\"><![CDATA[t:value]]></item>'>]>"
                + "<root>&content;<item>plain</item></root>");
        Node reference = document.getDocumentElement().getFirstChild();
        Element retainedItem = (Element) reference.getFirstChild();
        Node cdata = retainedItem.getFirstChild();
        Element sibling = (Element) reference.getNextSibling();

        Path schemaFile = Files.createTempFile("dom-normalizer", ".xsd");
        try {
            Files.write(schemaFile, schema.getBytes(StandardCharsets.UTF_8));
            DOMConfiguration config = document.getDomConfig();
            config.setParameter("schema-type", XMLConstants.W3C_XML_SCHEMA_NS_URI);
            config.setParameter("schema-location", schemaFile.toUri().toString());
            config.setParameter("cdata-sections", Boolean.FALSE);
            normalize(document, true);
        }
        finally {
            Files.delete(schemaFile);
        }

        assertTrue(errors.toString(), errors.isEmpty());
        assertFalse(retainedItem.hasAttribute("status"));
        assertSame(cdata, retainedItem.getFirstChild());
        assertEquals(Node.CDATA_SECTION_NODE, cdata.getNodeType());
        assertEquals("new", sibling.getAttribute("status"));
    }

    public void testChecksEntityTextForWellFormednessWithoutValidation() throws Exception {
        Document document = parse("<!DOCTYPE root [<!ENTITY content 'initial'>]>"
                + "<root>&content;</root>");
        EntityReferenceImpl reference = (EntityReferenceImpl) document.getDocumentElement().getFirstChild();
        reference.setReadOnly(false, true);
        Node text = reference.getFirstChild();
        text.setNodeValue("invalid\u0000text");
        reference.setReadOnly(true, true);

        normalize(document, false);

        assertTrue(errors.toString(), errors.contains("wf-invalid-character"));
        assertSame(text, reference.getFirstChild());
    }

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactoryImpl factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private void normalize(Document document, boolean validate) {
        DOMConfiguration configuration = document.getDomConfig();
        configuration.setParameter("entities", Boolean.TRUE);
        configuration.setParameter("well-formed", Boolean.TRUE);
        configuration.setParameter("validate", Boolean.valueOf(validate));
        configuration.setParameter("error-handler", new DOMErrorHandler() {
            @Override
            public boolean handleError(DOMError error) {
                errors.add(error.getType());
                return true;
            }
        });
        document.normalizeDocument();
    }
}
