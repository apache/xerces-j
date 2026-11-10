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

import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests HTML and XHTML serialization of empty non-void elements.
 */
public class TestHTMLSerializer extends TestCase {

    public void testIframeIsExplicitlyClosedInHTML() throws Exception {
        String output = serialize(false);
        assertTrue(output, output.contains("<iframe src=\"http://example.com/\"></iframe>"));
    }

    public void testIframeIsExplicitlyClosedInXHTML() throws Exception {
        String output = serialize(true);
        assertTrue(output, output.contains("<iframe src=\"http://example.com/\"></iframe>"));
        assertFalse(output, output.contains("<iframe src=\"http://example.com/\" />"));
    }

    public void testEmptyVoidElementIsSelfClosedInXHTML() throws Exception {
        Document document = createDocument("br");
        String output = serialize(document, true);
        assertTrue(output, output.contains("<br />"));
    }

    private String serialize(boolean xhtml) throws Exception {
        return serialize(createDocument("iframe"), xhtml);
    }

    private String serialize(Document document, boolean xhtml) throws Exception {
        StringWriter writer = new StringWriter();
        OutputFormat format = new OutputFormat("html", "UTF-8", false);
        format.setOmitXMLDeclaration(true);
        format.setOmitDocumentType(true);
        HTMLSerializer serializer = xhtml
                ? new XHTMLSerializer(writer, format)
                : new HTMLSerializer(writer, format);
        serializer.serialize(document);
        return writer.toString();
    }

    private Document createDocument(String tagName) {
        Document document = new DocumentImpl();
        Element html = document.createElement("html");
        document.appendChild(html);
        Element body = document.createElement("body");
        html.appendChild(body);
        Element element = document.createElement(tagName);
        if ("iframe".equals(tagName)) {
            element.setAttribute("src", "http://example.com/");
        }
        body.appendChild(element);
        return document;
    }
}
