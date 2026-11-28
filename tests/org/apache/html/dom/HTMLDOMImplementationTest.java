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
import org.w3c.dom.html.HTMLDOMImplementation;

public class HTMLDOMImplementationTest extends TestCase {

    private final HTMLDOMImplementation implementation =
            HTMLDOMImplementationImpl.getHTMLDOMImplementation();

    public void testReportsHtmlLevelOneSupport() {
        assertTrue(implementation.hasFeature("HTML", "1.0"));
        assertTrue(implementation.hasFeature("hTmL", null));
        assertTrue(implementation.hasFeature("HTML", ""));
        assertTrue(implementation.hasFeature("+html", "1.0"));
        assertFalse(implementation.hasFeature("HTML", "2.0"));
        assertFalse(DOMImplementationImpl.getDOMImplementation().hasFeature("HTML", "1.0"));
    }

    public void testPreservesInheritedFeatures() {
        assertTrue(implementation.hasFeature("XML", "1.0"));
        assertTrue(implementation.hasFeature("Events", "2.0"));
        assertFalse(implementation.hasFeature("unknown", null));
    }
}
