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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
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
 * @see                      TestSerializeDOMOut
 */


public class TestSerializeDOMOut extends TestCase
{          

    public TestSerializeDOMOut(){
    }

      /**
     * Deserializes Java DOM Object 
     * 
     * @param nameSerializeFile
     * @return 
     */
    public void serializeDOM( Document doc, String nameSerializedFile ){
        try {
            ObjectOutputStream out               =
                              new ObjectOutputStream( new FileOutputStream( nameSerializedFile ) );
            out.writeObject(doc);
            out.close();

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void testSerializeRoundTrip() throws Exception {
        DocumentImpl doc = new DocumentImpl();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        root.setAttribute("attr", "value");
        Text text = doc.createTextNode("hello");
        root.appendChild(text);

        File temp = File.createTempFile("xerces-serialize", ".ser");
        temp.deleteOnExit();
        try {
            TestSerializeDOMOut tstOut = new TestSerializeDOMOut();
            tstOut.serializeDOM(doc, temp.getAbsolutePath());
            TestSerializeDOMIn tstIn = new TestSerializeDOMIn();
            DocumentImpl result = tstIn.deserializeDOM(temp.getAbsolutePath());
            assertNotNull("Result should not be null", result);
            Element resultRoot = result.getDocumentElement();
            assertNotNull("Root element should not be null", resultRoot);
            assertEquals("root", resultRoot.getNodeName());
            assertEquals("value", resultRoot.getAttribute("attr"));
            assertEquals("hello", resultRoot.getFirstChild().getNodeValue());
        } finally {
            temp.delete();
        }
    }
}

