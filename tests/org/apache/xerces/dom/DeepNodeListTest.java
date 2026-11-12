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

import junit.framework.TestCase;

import org.apache.html.dom.HTMLDocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DeepNodeListTest extends TestCase {

    public void testNonNameAttributeDoesNotInvalidateCache() {
        DocumentImpl document = new DocumentImpl();
        ElementImpl root = (ElementImpl) document.createElement("root");
        document.appendChild(root);
        ElementImpl child = (ElementImpl) document.createElement("child");
        root.appendChild(child);

        DeepNodeListImpl nodes = new DeepNodeListImpl(root, "*");
        assertSame(child, nodes.item(0));
        int changes = nodes.changes;

        root.setAttribute("class", "value");
        assertSame(child, nodes.item(0));
        assertEquals(changes, nodes.changes);
    }

    public void testNameAttributeInvalidatesCache() {
        DocumentImpl document = new DocumentImpl();
        ElementImpl root = (ElementImpl) document.createElement("root");
        document.appendChild(root);
        ElementImpl child = (ElementImpl) document.createElement("child");
        root.appendChild(child);

        DeepNodeListImpl nodes = new DeepNodeListImpl(root, "*");
        assertSame(child, nodes.item(0));
        int changes = nodes.changes;

        root.setAttribute("name", "value");
        nodes.item(0);
        assertFalse(changes == nodes.changes);
    }

    public void testGetElementsByNameUpdatesAfterNameChange() {
        HTMLDocumentImpl document = new HTMLDocumentImpl();
        Element element = document.createElement("div");
        document.appendChild(element);

        NodeList named = document.getElementsByName("first");
        assertEquals(0, named.getLength());

        element.setAttribute("name", "first");
        assertEquals(1, named.getLength());

        element.setAttribute("name", "second");
        assertEquals(0, named.getLength());
    }
}
