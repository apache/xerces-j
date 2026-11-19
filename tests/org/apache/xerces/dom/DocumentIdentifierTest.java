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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class DocumentIdentifierTest extends TestCase {

    public void testDetachedElementIsWeaklyReferencedAndCanBeReattached() {
        CoreDocumentImpl document = new DocumentImpl();
        Element element = appendIdentifiedElement(document);
        document.removeChild(element);

        assertNull(document.getElementById("item"));
        Object entry = document.identifiers.get("item");
        assertTrue("Detached element must be held weakly", entry instanceof WeakReference);
        assertSame(element, ((WeakReference<?>) entry).get());

        document.appendChild(element);
        assertSame(element, document.getElementById("item"));
        element.removeAttribute("id");
        assertNull(document.getElementById("item"));
    }

    public void testClearedReferencesArePurgedWithoutRemovingReplacement() {
        CoreDocumentImpl document = new DocumentImpl();
        Element element = appendIdentifiedElement(document);
        document.putIdentifier("stale", element);
        WeakReference<?> stale = (WeakReference<?>) document.identifiers.get("stale");
        WeakReference<?> old = (WeakReference<?>) document.identifiers.get("item");
        document.putIdentifier("item", element);

        // Simulate the GC's reference processing without relying on GC timing.
        stale.clear();
        stale.enqueue();
        old.clear();
        old.enqueue();

        assertEquals("item", document.getIdentifiers().nextElement());
        assertFalse(document.identifiers.containsKey("stale"));
        assertSame(element, document.getElementById("item"));
    }

    public void testDeferredIdLookup() throws Exception {
        DOMParser parser = new DOMParser();
        parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
        parser.parse(new InputSource(new StringReader(
                "<!DOCTYPE root [<!ELEMENT root (child)><!ELEMENT child EMPTY>"
                + "<!ATTLIST child id ID #REQUIRED>]><root><child id='item'/></root>")));

        CoreDocumentImpl document = (DeferredDocumentImpl) parser.getDocument();
        Element child = document.getElementById("item");
        assertNotNull(child);
        assertSame(document.getDocumentElement().getFirstChild(), child);
        assertTrue(document.identifiers.get("item") instanceof WeakReference);
    }

    public void testSerializedDocumentPreservesIdLookup() throws Exception {
        CoreDocumentImpl document = new DocumentImpl();
        Element element = appendIdentifiedElement(document);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(document);
        }

        assertSame(element, document.getElementById("item"));
        assertTrue(document.identifiers.get("item") instanceof WeakReference);
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            CoreDocumentImpl restored = (CoreDocumentImpl) input.readObject();
            assertSame(restored.getDocumentElement(), restored.getElementById("item"));
            assertTrue(restored.identifiers.get("item") instanceof WeakReference);
        }
    }

    private Element appendIdentifiedElement(CoreDocumentImpl document) {
        Element element = document.createElement("root");
        document.appendChild(element);
        element.setAttribute("id", "item");
        element.setIdAttribute("id", true);
        return element;
    }
}
