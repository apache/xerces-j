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

package org.apache.xerces.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests {@link DOMUtil#copyInto(Node, Node)}.
 */
public class DOMUtilTest extends TestCase {

    public void testCopyFirstChildWithoutChildren() throws Exception {
        Document document = parse("<a><b/><c><d/></c></a>");
        Node source = document.getDocumentElement().getFirstChild();
        Element dest = document.createElement("dest");

        DOMUtil.copyInto(source, dest);

        assertEquals(1, dest.getChildNodes().getLength());
        assertEquals("b", dest.getFirstChild().getNodeName());
    }

    public void testCopyLeadingTextDoesNotCopySiblings() throws Exception {
        Document document = parse("<a>\t<b><c/></b></a>");
        Node source = document.getDocumentElement().getFirstChild();
        Element dest = document.createElement("dest");

        DOMUtil.copyInto(source, dest);

        assertEquals(1, dest.getChildNodes().getLength());
        assertEquals(Node.TEXT_NODE, dest.getFirstChild().getNodeType());
        assertEquals("\t", dest.getFirstChild().getNodeValue());
    }

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
