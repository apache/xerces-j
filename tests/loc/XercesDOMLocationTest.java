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
package loc;

import junit.framework.TestCase;
import org.apache.xerces.parsers.DOMParser;

import static org.apache.xerces.parsers.XML11Configuration.LOCATION_INFO_FEATURE;

import org.apache.xerces.dom.NodeImpl;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XercesDOMLocationTest extends TestCase {

    private static final String TEST_XML_PATH = "tests/loc/test.xml";

    private static List<Element> getElementChildren(Element parent) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                result.add((Element) node);
            }
        }
        return result;
    }

    private Document parseXML(Boolean enableLocationFeature) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        if (enableLocationFeature != null)
            parser.setFeature(LOCATION_INFO_FEATURE, enableLocationFeature);
        parser.parse(new File(TEST_XML_PATH).toURI().toString());
        Document doc = parser.getDocument();
        return doc;
    }

    private DOMLocator getLocator(Node n) {
        return ((NodeImpl) n).getLocator();
    }

    /**
     * This test verifies that the XML tree we get from the parse is
     * the expected one.
     *
     * The other tests focus on the location info such as line numbers
     * and they do not have to repeat this verification.
     */
    public void testXMLIsExpectedTree() throws IOException, SAXException {
        Document doc = parseXML(null); // Feature defaults
        Element root = doc.getDocumentElement();
        String name = root.getTagName();
        assertEquals("root", name);
        List<Element> ec = getElementChildren(root);
        assertEquals(3, ec.size());
        Element n1 = ec.get(0);
        Element n2 = ec.get(1);
        Element n3 = ec.get(2);
        assertEquals("child", n1.getTagName());
        assertEquals("selfClosing", n2.getTagName());
        assertEquals("another", n3.getTagName());
    }

    public void testFeatureDisabledByDefault() throws IOException, SAXException {
        Document doc = parseXML(null); // Feature defaults
        Element root = doc.getDocumentElement();
        DOMLocator locationData = getLocator(root);
        assertNull("Location data should not be present when feature is disabled.", locationData);
    }

    public void testFeatureEnabledLocationPresent() throws IOException, SAXException {
        Document doc = parseXML(true); // Feature enabled

        Element root = doc.getDocumentElement();
        DOMLocator locationData = getLocator(root);

        assertNotNull("Location data should be present when feature is enabled.", locationData);
    }

    public void testRootElementLocation() throws IOException, SAXException {
        Document doc = parseXML(true);
        Element root = doc.getDocumentElement();

        DOMLocator loc = getLocator(root);
        assertNotNull("Root element should have location data.", loc);
        assertEquals("Root element should be on line 2.", 2, loc.getLineNumber());
    }

    public void testChildElementLocation() throws IOException, SAXException {
        Document doc = parseXML(true);
        Element child = (Element) doc.getDocumentElement().getElementsByTagName("child").item(0);

        DOMLocator location = getLocator(child);
        assertNotNull("Child element should have location data.", location);
        assertEquals("Child element should be on line 3.", 3,location.getLineNumber());
    }

    public void testSelfClosingElementLocation() throws IOException, SAXException {
        Document doc = parseXML(true);
        Element selfClosing = (Element) doc.getDocumentElement().getElementsByTagName("selfClosing").item(0);

        DOMLocator location = getLocator(selfClosing);
        assertNotNull("Self-closing element should have location data.", location);
        assertEquals("Self-closing element should be on line 4.", 4, location.getLineNumber());
    }

    public void testAnotherElementLocation() throws IOException, SAXException {
        Document doc = parseXML(true);
        Element another = (Element) doc.getDocumentElement().getElementsByTagName("another").item(0);

        DOMLocator location = getLocator(another);
        assertNotNull("Another element should have location data.", location);
        assertEquals("Another element should be on line 8.", 8, location.getLineNumber());
    }

    public void testFeatureDisabledNoLocationStored() throws IOException, SAXException {
        Document doc = parseXML(false); // feature explicitly disabled
        Element child = (Element) doc.getDocumentElement().getElementsByTagName("child").item(0);

        DOMLocator locationData = getLocator(child);
        assertNull("Location data should not be present when feature is disabled.", locationData);
    }
}
