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

package dom.serialize;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMOutputImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * This class is testing namespace algorithm during serialization.
 * Creates an in-memory DOM document, modifies the tree, and serializes
 * using LSSerializer, verifying that the output contains expected
 * namespace declarations.
 * 
 * @author Elena Litani, IBM
 * @version $Id$
 */
public class TestNS extends TestCase {

    public void testNamespaceSerialization() throws Exception {
        DocumentImpl doc = new DocumentImpl();
        Element root = doc.createElementNS("http://ns", "root");
        doc.appendChild(root);

        Element e1 = doc.createElementNS(null, "root");
        root.appendChild(e1);
        e1.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xx", "foo");
        e1.setAttributeNS("http://rsa2", "xx:attr", "value");

        root.appendChild(doc.createElementNS("urn:schemas-xmlsoap-org:soap.v1", "s:child1"));

        root.appendChild(doc.createElementNS("http://child2", "s:child2"));

        root.appendChild(doc.createElementNS("http://child3/default", "child3"));

        root.appendChild(doc.createElementNS(null, "child4"));

        e1 = doc.createElementNS("http://rsa", "m1:root");
        root.appendChild(e1);
        Element e2 = doc.createElementNS("http://rsa", "m1:e1");
        e2.setAttributeNS("http://rsa", "m1:a1", "v");
        e2.setAttributeNS("http://rsa2", "m2:a2", "v");
        e1.appendChild(e2);

        Element elm = doc.createElementNS("http://child7", "prefix:child7");
        Attr attr = doc.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:prefix");
        attr.setValue("http://child8");
        elm.setAttributeNode(attr);
        root.appendChild(elm);

        elm = doc.createElementNS(null, "child5");
        attr = doc.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p");
        attr.setValue(null);
        elm.setAttributeNode(attr);
        root.appendChild(elm);

        elm = doc.createElementNS("http://child6", "child6");
        attr = doc.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        attr.setValue("http://default");
        elm.setAttributeNode(attr);
        attr = doc.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        attr.setValue("http://default2");
        elm.setAttributeNode(attr);
        root.appendChild(elm);

        elm = doc.createElementNS("urn:schemas-xmlsoap-org:soap.v1", "s:testAttributes");
        attr = doc.createAttributeNS(null, "attr1");
        elm.setAttributeNode(attr);
        attr = doc.createAttributeNS("urn:schemas-xmlsoap-org:soap.v1", "s:attr2");
        elm.setAttributeNode(attr);
        attr = doc.createAttributeNS("http://attr3", "attr3");
        elm.setAttributeNode(attr);
        root.appendChild(elm);

        elm = doc.createElementNS("urn:schemas-xmlsoap-org:soap.v1", "s:testAttributes2");
        attr = doc.createAttributeNS("http://attr_A", "attr_A");
        elm.setAttributeNode(attr);
        attr = doc.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        attr.setValue("http://attr_A");
        elm.setAttributeNode(attr);
        attr = doc.createAttributeNS("http://attr_B", "attr_B");
        elm.setAttributeNode(attr);
        root.appendChild(elm);

        LSSerializer writer = ((DOMImplementationLS) DOMImplementationImpl.getDOMImplementation()).createLSSerializer();
        LSOutput dOut = new DOMOutputImpl();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dOut.setByteStream(baos);
        writer.write(doc, dOut);

        String output = baos.toString("UTF-8");
        assertTrue("Output should contain xmlns: declarations", output.contains("xmlns"));
        assertTrue("Output should contain xx:attr", output.contains("xx:attr"));
        assertTrue("Output should contain s:child1", output.contains("s:child1"));
        assertTrue("Output should contain s:child2", output.contains("s:child2"));
        assertTrue("Output should contain child3", output.contains("child3"));
        assertTrue("Output should contain child4", output.contains("child4"));
        assertTrue("Output should contain m1:root", output.contains("m1:root"));
        assertTrue("Output should contain m1:e1", output.contains("m1:e1"));
        assertTrue("Output should contain prefix:child7", output.contains("prefix:child7"));
        assertTrue("Output should contain xmlns:prefix", output.contains("xmlns:prefix"));
        assertTrue("Output should contain s:testAttributes", output.contains("s:testAttributes"));
        assertTrue("Output should contain s:testAttributes2", output.contains("s:testAttributes2"));
    }
}
