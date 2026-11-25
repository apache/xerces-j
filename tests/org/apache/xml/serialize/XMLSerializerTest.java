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

package org.apache.xml.serialize;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

public class XMLSerializerTest extends TestCase {

    public void testEmptyAttributeQNameUsesNonemptyPrefixForElementNamespace() throws Exception {
        Element root = serializeAttribute(true);
        assertEquals("urn:test", root.getNamespaceURI());
        assertEquals("v", root.getAttributeNS("urn:test", "id"));
        assertEquals("p", root.getAttributeNodeNS("urn:test", "id").getPrefix());
        assertEquals("plain", root.getAttributeNS(null, "unqualified"));
    }

    public void testEmptyAttributeQNameGetsPrefixWithOnlyDefaultNamespace() throws Exception {
        Element root = serializeAttribute(false);
        assertEquals("urn:test", root.getNamespaceURI());
        assertEquals("v", root.getAttributeNS("urn:test", "id"));
        assertNotNull(root.getAttributeNodeNS("urn:test", "id").getPrefix());
    }

    private Element serializeAttribute(boolean declarePrefix) throws Exception {
        StringWriter writer = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(writer, new OutputFormat("xml", "UTF-8", false));
        serializer.startDocument();
        serializer.startPrefixMapping("", "urn:test");
        if (declarePrefix) {
            serializer.startPrefixMapping("p", "urn:test");
        }
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("urn:test", "id", "", "CDATA", "v");
        attributes.addAttribute("", "unqualified", "", "CDATA", "plain");
        serializer.startElement("urn:test", "root", "root", attributes);
        serializer.endElement("urn:test", "root", "root");
        serializer.endDocument();

        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(writer.toString())))
                .getDocumentElement();
    }
}
