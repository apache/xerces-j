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

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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
