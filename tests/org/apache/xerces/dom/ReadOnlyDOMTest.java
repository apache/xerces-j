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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class ReadOnlyDOMTest extends TestCase {

    public void testDeepReadOnlyLocksAttributeText() {
        DocumentImpl document = new DocumentImpl();
        ElementImpl element = (ElementImpl) document.createElement("root");
        document.appendChild(element);
        element.setAttribute("value", "before");
        Attr attribute = element.getAttributeNode("value");

        document.setReadOnly(true, true);

        Node text = attribute.getFirstChild();
        try {
            text.setNodeValue("after");
            fail("Expected the attribute text to be read-only");
        } catch (DOMException expected) {
            assertEquals(DOMException.NO_MODIFICATION_ALLOWED_ERR, expected.code);
        }
        assertEquals("before", attribute.getValue());

        document.setReadOnly(false, true);
        text.setNodeValue("after");
        assertEquals("after", attribute.getValue());
    }

    public void testReadOnlyLocksInitiallyEmptyAttributeMap() {
        DocumentImpl document = new DocumentImpl();
        ElementImpl element = (ElementImpl) document.createElement("root");
        document.appendChild(element);
        Attr attribute = document.createAttribute("added");

        document.setReadOnly(true, true);

        try {
            element.getAttributes().setNamedItem(attribute);
            fail("Expected the attribute map to be read-only");
        } catch (DOMException expected) {
            assertEquals(DOMException.NO_MODIFICATION_ALLOWED_ERR, expected.code);
        }
        assertFalse(element.hasAttribute("added"));

        document.setReadOnly(false, true);
        element.getAttributes().setNamedItem(attribute);
        assertSame(attribute, element.getAttributeNode("added"));
    }

    public void testShallowReadOnlyLeavesAttributeTextWritable() {
        DocumentImpl document = new DocumentImpl();
        AttrImpl attribute = (AttrImpl) document.createAttribute("value");
        attribute.setValue("before");
        attribute.setReadOnly(true, false);

        attribute.getFirstChild().setNodeValue("after");
        assertEquals("after", attribute.getValue());
    }
}
