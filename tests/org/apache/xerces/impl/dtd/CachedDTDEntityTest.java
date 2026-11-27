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

package org.apache.xerces.impl.dtd;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CachedDTDEntityTest extends TestCase {

    private static final String DTD = "<!ELEMENT root (#PCDATA)><!ENTITY sample \"123\">";
    private static final String XML =
        "<?xml version=\"1.0\"?><!DOCTYPE root SYSTEM \"root.dtd\"><root>&sample;</root>";

    public void testInternalEntityResolvedWithCachedDTD() throws Exception {
        assertEntityValues(XML, "123", XML, "123");
    }

    public void testDocumentInternalSubsetTakesPrecedence() throws Exception {
        String xmlWithInternalSubset =
            "<!DOCTYPE root SYSTEM \"root.dtd\" [<!ENTITY sample \"local\">]><root>&sample;</root>";
        assertEntityValues(XML, "123", xmlWithInternalSubset, "local");
        assertEntityValues(xmlWithInternalSubset, "local", XML, "123");
    }

    public void testCachedDTDRespectsDisabledExternalGeneralEntities() throws Exception {
        final String dtd = "<!ELEMENT root (#PCDATA)><!ENTITY sample SYSTEM 'sample.txt'>";
        final int[] requests = new int[2];
        EntityResolver resolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
                if (systemId.endsWith("/root.dtd")) {
                    requests[0]++;
                    return source(dtd, systemId);
                }
                if (systemId.endsWith("/sample.txt")) {
                    requests[1]++;
                    return source("external text", systemId);
                }
                throw new SAXException("Unexpected system ID: " + systemId);
            }
        };

        XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
        SAXParser first = validatingParser(grammarPool, resolver);
        assertEquals("external text", parseText(first));
        assertEquals(1, requests[0]);
        assertEquals(1, requests[1]);

        SAXParser second = validatingParser(grammarPool, resolver);
        second.setFeature("http://xml.org/sax/features/external-general-entities", false);
        assertEquals("", parseText(second));
        assertEquals("The second parse should reuse the DTD", 1, requests[0]);
        assertEquals("The second parse should skip the external entity", 1, requests[1]);

        second.setFeature("http://xml.org/sax/features/external-general-entities", true);
        assertEquals("external text", parseText(second));
        assertEquals("Re-enabling external entities should still reuse the DTD", 1, requests[0]);
        assertEquals(2, requests[1]);
    }

    public void testCachedDTDRestoresParameterEntityReferenceCount() throws Exception {
        final String dtd = "<!ELEMENT root (#PCDATA)><!ENTITY % value 'text'>"
                + "<!ENTITY % stored '%value;'><!ENTITY sample '%stored;'>";
        final int[] dtdRequests = new int[1];
        EntityResolver resolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
                if (!systemId.endsWith("/root.dtd")) {
                    throw new SAXException("Unexpected system ID: " + systemId);
                }
                dtdRequests[0]++;
                return source(dtd, systemId);
            }
        };

        XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
        SAXParser first = validatingParser(grammarPool, resolver);
        assertEquals("text", parseText(first));
        assertEquals(1, entityManager(first).getParamEntityRefCount("%stored"));

        SAXParser second = validatingParser(grammarPool, resolver);
        assertEquals("text", parseText(second));
        assertEquals("The second parse should reuse the DTD", 1, dtdRequests[0]);
        assertEquals(1, entityManager(second).getParamEntityRefCount("%stored"));
    }

    private static SAXParser validatingParser(XMLGrammarPoolImpl grammarPool, EntityResolver resolver)
            throws SAXException {
        SAXParser parser = new SAXParser(new SymbolTable(), grammarPool);
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setEntityResolver(resolver);
        return parser;
    }

    private static InputSource source(String text, String systemId) {
        InputSource input = new InputSource(new StringReader(text));
        input.setSystemId(systemId);
        return input;
    }

    private static String parseText(SAXParser parser) throws Exception {
        final StringBuilder text = new StringBuilder();
        parser.setContentHandler(new DefaultHandler() {
            @Override
            public void characters(char[] characters, int start, int length) {
                text.append(characters, start, length);
            }
        });
        parser.parse(source(XML, "file:/cached-dtd/input.xml"));
        return text.toString();
    }

    private static XMLEntityManager entityManager(SAXParser parser) throws SAXException {
        return (XMLEntityManager) parser.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    }

    private void assertEntityValues(String firstXml, String firstValue,
            String secondXml, String secondValue) throws Exception {
        SAXParser parser = new SAXParser();
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool",
                new XMLGrammarPoolImpl());
        parser.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                if (systemId != null && systemId.endsWith("root.dtd")) {
                    InputSource is = new InputSource(new StringReader(DTD));
                    is.setSystemId(systemId);
                    return is;
                }
                return null;
            }
        });
        final StringBuffer text = new StringBuffer();
        parser.setContentHandler(new DefaultHandler() {
            public void characters(char[] ch, int start, int length) {
                text.append(ch, start, length);
            }
        });

        parser.parse(new InputSource(new StringReader(firstXml)));
        assertEquals(firstValue, text.toString());

        text.setLength(0);
        parser.parse(new InputSource(new StringReader(secondXml)));
        assertEquals(secondValue, text.toString());
    }
}
