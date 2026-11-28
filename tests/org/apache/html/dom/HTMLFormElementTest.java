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

import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLFormElementTest extends TestCase {
    private HTMLFormElement form;
    private HTMLCollection controls;
    private Element first;
    private Element nested;
    private Element secondNested;
    private Element last;

    @Override
    protected void setUp() {
        HTMLDocument document = HTMLDOMImplementationImpl.getHTMLDOMImplementation()
                .createHTMLDocument("Form controls");
        form = (HTMLFormElement) document.createElement("form");
        document.getBody().appendChild(form);
        first = appendElement(form, "input", "first");
        Element fieldset = appendElement(form, "fieldset", "group");
        Element label = appendElement(fieldset, "label", "caption");
        nested = appendElement(label, "input", "nested");
        secondNested = appendElement(fieldset, "select", "second-nested");
        last = appendElement(form, "button", "last");
        controls = form.getElements();
    }

    public void testLengthCountsControlsInsideFieldsetAndLabel() {
        assertEquals(4, controls.getLength());
        assertEquals(4, form.getLength());
    }

    public void testItemReturnsControlsInTreeOrder() {
        assertSame(first, controls.item(0));
        assertSame(nested, controls.item(1));
        assertSame(secondNested, controls.item(2));
        assertSame(last, controls.item(3));
        assertNull(controls.item(4));
    }

    public void testNamedItemFindsNestedControlButExcludesItsContainers() {
        assertSame(nested, controls.namedItem("nested"));
        assertNull(controls.namedItem("group"));
        assertNull(controls.namedItem("caption"));
    }

    private Element appendElement(Element parent, String tagName, String id) {
        Element element = parent.getOwnerDocument().createElement(tagName);
        element.setAttribute("id", id);
        parent.appendChild(element);
        return element;
    }
}
