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

package org.apache.xerces.impl;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests external entity resolution.
 */
public class XMLEntityManagerTest extends TestCase {

    private static final String DOCUMENT_SYSTEM_ID = "file:/tmp/example/doc.xml";
    private static final String REFDOC_SYSTEM_ID = "urn:example:dtd/refdoc.ent";
    private static final String ENTITIES_SYSTEM_ID = "urn:example:dtd/entities.ent";

    private static final String DOCUMENT =
        "<!DOCTYPE doc [\n" +
        "<!ENTITY % refdoc SYSTEM \"" + REFDOC_SYSTEM_ID + "\">\n" +
        "%refdoc;\n" +
        "]>\n" +
        "<doc>&greeting;</doc>";

    private static final String REFDOC =
        "<!ENTITY % entities SYSTEM \"entities.ent\">\n" +
        "%entities;";

    private static final String ENTITIES = "<!ENTITY greeting \"Hello\">";

    public void testChainedParameterEntityUsesDeclaredSystemId() throws Exception {
        XMLReader parser = new SAXParser();
        final StringBuffer content = new StringBuffer();
        parser.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                if (REFDOC_SYSTEM_ID.equals(systemId)) {
                    return new InputSource(new StringReader(REFDOC));
                }
                if (ENTITIES_SYSTEM_ID.equals(systemId)) {
                    return new InputSource(new StringReader(ENTITIES));
                }
                return null;
            }
        });
        parser.setContentHandler(new DefaultHandler() {
            public void characters(char[] ch, int start, int length) {
                content.append(ch, start, length);
            }
        });
        InputSource source = new InputSource(new StringReader(DOCUMENT));
        source.setSystemId(DOCUMENT_SYSTEM_ID);
        parser.parse(source);
        assertEquals("Hello", content.toString());
    }
}
