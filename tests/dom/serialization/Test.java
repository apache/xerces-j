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

package dom.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import org.apache.xerces.dom.DocumentImpl;

public class Test extends TestCase {

    public void testSerializationRoundTrip() throws Exception {
        DocumentImpl doc = new DocumentImpl();
        Element root = doc.createElementNS(null, "root");
        doc.appendChild(root);
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:foo", "boo");
        Text text = doc.createTextNode("hello");
        root.appendChild(text);

        File serFile = File.createTempFile("xerces", ".ser");
        serFile.deleteOnExit();
        try {
            serialize(doc, serFile.getAbsolutePath());
            Document newDocument = deserialize(serFile.getAbsolutePath());
            assertNotNull(newDocument);
            Element newRoot = newDocument.getDocumentElement();
            assertNotNull(newRoot);
            assertEquals("root", newRoot.getNodeName());
            assertEquals("boo", newRoot.getAttributeNS("http://www.w3.org/2000/xmlns/", "foo"));
            assertEquals("hello", newRoot.getFirstChild().getNodeValue());
        } finally {
            serFile.delete();
        }
    }

    public static void serialize(Document document, String filename) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(document);
        out.close();
    }

    public static Document deserialize(String filename) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        Document result = (Document) in.readObject();
        return result;
    }

}
