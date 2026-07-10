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

package dom.ids;

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dom.ParserWrapper;

/**
 * A simple program to test Document.getElementById() and the management
 * of ID attributes. Originally based on dom.Counter.
 * This test takes as input input.xml file
 *
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 *
 * @version $Id$
 */
public class Test extends TestCase {

    //
    // Constants
    //

    // feature ids

    protected static final String NAMESPACES_FEATURE_ID =
        "http://xml.org/sax/features/namespaces";

    protected static final String VALIDATION_FEATURE_ID =
        "http://xml.org/sax/features/validation";

    protected static final String SCHEMA_VALIDATION_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema";

    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema-full-checking";

    protected static final String DEFERRED_DOM_FEATURE_ID =
        "http://apache.org/xml/features/dom/defer-node-expansion";

    // default settings

    protected static final String DEFAULT_PARSER_NAME = "dom.wrappers.Xerces";

    protected static final boolean DEFAULT_NAMESPACES = true;

    protected static final boolean DEFAULT_VALIDATION = false;

    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;

    // Xerces specific feature
    protected static final boolean DEFAULT_DEFERRED_DOM = true;

    //
    // Public methods
    //

    public void testGetElementById() throws Exception {

        ParserWrapper parser = (ParserWrapper)
            Class.forName(DEFAULT_PARSER_NAME).newInstance();

        parser.setFeature(NAMESPACES_FEATURE_ID, DEFAULT_NAMESPACES);
        parser.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);
        parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, DEFAULT_SCHEMA_VALIDATION);
        parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, DEFAULT_SCHEMA_FULL_CHECKING);

        if (parser instanceof dom.wrappers.Xerces) {
            parser.setFeature(DEFERRED_DOM_FEATURE_ID, DEFAULT_DEFERRED_DOM);
        }

        Document doc = null;
        try {
            doc = parser.parse("tests/dom/ids/input.xml");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Element el = doc.getElementById("one.worker");
        assertNotNull("el != null", el);
        Element el2 = doc.getElementById("one.worker there");
        assertNull("el2 == null", el2);

        if (el != null) {
            assertEquals("el.getAttribute(\"id\")", "one.worker", el.getAttribute("id"));
            el.setAttribute("id", "my.worker");
            el2 = doc.getElementById("my.worker");
            assertSame("el2 == el", el2, el);

            el2 = doc.getElementById("one.worker");
            assertNull("el2 == null", el2);
            el.removeAttribute("id");
            el2 = doc.getElementById("my.worker");
            assertNull("el2 == null", el2);
        }

        // find default id attribute and check its value
        NodeList elementList = doc.getElementsByTagName("person");
        Element testEmployee = (Element)elementList.item(1);
        Attr id = testEmployee.getAttributeNode("id2");
        assertEquals("value == 'id02'", "id02", id.getNodeValue());

        Element elem = doc.getElementById("id02");
        assertEquals("return by id 'id02'", "person", elem.getNodeName());

        // remove default attribute and check on retrieval what its value
        Attr removedAttr = testEmployee.removeAttributeNode(id);
        String value = testEmployee.getAttribute("id2");
        assertEquals("value='default.id'", "default.id", value);

        elem = doc.getElementById("default.id");
        assertNotNull("elem by id 'default.id'", elem);

        elem = doc.getElementById("id02");
        assertNull("elem by id '02'", elem);

        Element person = (Element)doc.getElementsByTagNameNS(null, "person").item(0);
        person.removeAttribute("id");
        person.removeAttribute("id2");
        person.setAttributeNS(null, "idAttr", "eb0009");
        person.setIdAttribute("idAttr", true);

        elem = doc.getElementById("eb0009");
        assertNotNull("elem by id 'eb0009'", elem);

        doc.getDocumentElement().removeChild(person);
        elem = doc.getElementById("eb0009");
        assertNull("element with id 'eb0009 removed'", elem);

        doc.getDocumentElement().appendChild(person);
        elem = doc.getElementById("eb0009");
        assertNotNull("elem by id 'eb0009'", elem);
        Attr attr = (Attr)person.getAttributeNode("idAttr");
        assertTrue("attribute is id", attr.isId());

        person.setIdAttribute("idAttr", false);
        elem = doc.getElementById("eb0009");
        assertNull("element with id 'eb0009 removed'", elem);

        assertFalse("attribute is not id", attr.isId());

    }

} // class Test
