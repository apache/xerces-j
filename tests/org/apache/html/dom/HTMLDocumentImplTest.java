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

package org.apache.html.dom;

import junit.framework.TestCase;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class HTMLDocumentImplTest extends TestCase {

    public void testDocumentTypeStaysOutsideGeneratedHtmlElement() {
        HTMLDocumentImpl document = new HTMLDocumentImpl();
        DocumentType doctype = DOMImplementationImpl.getDOMImplementation().createDocumentType("html", null, null);
        document.appendChild(doctype);
        Element namespacedHtml = document.createElementNS("http://www.w3.org/1999/xhtml", "html");
        document.appendChild(namespacedHtml);

        Element html = document.getDocumentElement();

        assertEquals(2, document.getChildNodes().getLength());
        assertSame(doctype, document.getFirstChild());
        assertSame(doctype, document.getDoctype());
        assertSame(document, doctype.getParentNode());
        assertSame(html, doctype.getNextSibling());
        assertSame(namespacedHtml, html.getFirstChild());
    }
}
