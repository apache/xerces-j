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

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Regression test for XERCESJ-793. When the serializer is used in content
 * handler mode, multiple prefixes bound to the same namespace URI must all be
 * serialized. Otherwise the output is not namespace well-formed.
 */
public class TestDuplicateNamespaceDeclarations extends TestCase {

    private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    private static final String URL_URI = "http://url";

    public void testNamespaceDeclarationsInAttributes() throws Exception {
        StringWriter writer = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(writer, null);
        serializer.setNamespaces(true);
        serializer.startDocument();

        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute(XMLNS_URI, "ns1", "xmlns:ns1", "CDATA", URL_URI);
        attrs.addAttribute(XMLNS_URI, "ns2", "xmlns:ns2", "CDATA", URL_URI);
        attrs.addAttribute(XMLNS_URI, "ns3", "xmlns:ns3", "CDATA", "http://otherurl");
        attrs.addAttribute(URL_URI, "attr1", "ns1:attr1", "CDATA", "value1");
        attrs.addAttribute(URL_URI, "attr2", "ns2:attr2", "CDATA", "value2");

        serializer.startElement(null, "root", "root", attrs);
        serializer.endElement(null, "root", "root");
        serializer.endDocument();

        checkResult(writer.toString());
    }

    public void testNamespaceDeclarationsFromPrefixMapping() throws Exception {
        StringWriter writer = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(writer, null);
        serializer.setNamespaces(true);
        serializer.startDocument();

        serializer.startPrefixMapping("ns1", URL_URI);
        serializer.startPrefixMapping("ns2", URL_URI);

        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute(URL_URI, "attr1", "ns1:attr1", "CDATA", "value1");
        attrs.addAttribute(URL_URI, "attr2", "ns2:attr2", "CDATA", "value2");

        serializer.startElement(null, "root", "root", attrs);
        serializer.endElement(null, "root", "root");
        serializer.endDocument();

        checkResult(writer.toString());
    }

    private void checkResult(String xml) throws Exception {
        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", true);
        parser.parse(new InputSource(new StringReader(xml)));
        Document document = parser.getDocument();

        Element root = document.getDocumentElement();
        Attr attr1 = root.getAttributeNodeNS(URL_URI, "attr1");
        Attr attr2 = root.getAttributeNodeNS(URL_URI, "attr2");
        assertNotNull("Missing ns1:attr1 in " + xml, attr1);
        assertNotNull("Missing ns2:attr2 in " + xml, attr2);
        assertEquals("ns1", attr1.getPrefix());
        assertEquals("ns2", attr2.getPrefix());
    }
}
