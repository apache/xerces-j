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

import junit.framework.TestCase;

import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xml.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSSerializer;

/**
 * Tests document type serialization with the built-in DOM Level 3 serializer.
 */
public class TestDocumentTypeSerialization extends TestCase {

    private DOMImplementation implementation;
    private LSSerializer serializer;

    @Override
    protected void setUp() {
        implementation = CoreDOMImplementationImpl.getDOMImplementation();
        serializer = new DOMSerializerImpl();
        serializer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
    }

    public void testDocumentTypeWithoutSubsetsIsSerialized() throws Exception {
        Document doc = implementation.createDocument(null, "html",
                implementation.createDocumentType("html", null, null));
        assertEquals("<!DOCTYPE html><html/>", serialize(doc));
    }

    public void testDocumentTypeWithSystemIdIsSerialized() throws Exception {
        Document doc = implementation.createDocument(null, "html",
                implementation.createDocumentType("html", null, "html.dtd"));
        assertEquals("<!DOCTYPE html SYSTEM \"html.dtd\"><html/>", serialize(doc));
    }

    public void testDocumentWithoutDocumentTypeIsSerialized() throws Exception {
        Document doc = implementation.createDocument(null, "html", null);
        assertEquals("<html/>", serialize(doc));
    }

    private String serialize(Document doc) throws Exception {
        return serializer.writeToString(doc).replaceAll("[\\n\\r]", "");
    }
}
