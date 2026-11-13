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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * This testcase tests the Java Serialization
 * of the DOM.
 * I wrote this to test this capability for
 * regresion
 * 
 * @author Jeffrey Rodriguez
 * @version $Id$
 * @see                      TestSerializeDOMIn
 */
public class TestSerializeDOMIn extends TestCase {

    public TestSerializeDOMIn() {
    }


    /**
     * Serializes Java DOM Object 
     * 
     * @param nameSerializeFile
     * @return 
     */
    public DocumentImpl deserializeDOM( String nameSerializedFile ){
        ObjectInputStream in   = null;
        DocumentImpl      doc  = null;
        try {

            FileInputStream fileIn = new FileInputStream( nameSerializedFile );
            in                     = new ObjectInputStream(fileIn);
            doc                    = (DocumentImpl) in.readObject();//Deserialize object
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return doc;
    }


    public void testDeserialize() throws Exception {
        DocumentImpl doc = new DocumentImpl();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Text text = doc.createTextNode("test");
        root.appendChild(text);
        File temp = File.createTempFile("xerces-serialize", ".ser");
        temp.deleteOnExit();
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp));
            out.writeObject(doc);
            out.close();
            TestSerializeDOMIn tst = new TestSerializeDOMIn();
            DocumentImpl result = tst.deserializeDOM(temp.getAbsolutePath());
            assertNotNull("Deserialized document should not be null", result);
            assertEquals("root element", "root", result.getDocumentElement().getNodeName());
        } finally {
            temp.delete();
        }
    }
}
