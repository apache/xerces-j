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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMOutputImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.DOMSerializerImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * Tests that original behavior of XMLSerializer is not broken.
 * The namespace fixup is only performed with DOMWriter.
 * 
 * @author Elena Litani, IBM
 * @version $Id$
 */
public class TestXmlns extends TestCase implements DOMErrorHandler{

      public void testXmlnsSerialization() throws Exception {
            DocumentImpl document = new DocumentImpl();
            document.setXmlEncoding("utf-8");
            Element outerNode = document.createElement("outer");
            outerNode.setAttribute("xmlns", "myuri:");
            document.appendChild(outerNode);
            Element innerNode = document.createElement("inner");
            outerNode.appendChild(innerNode);

            Writer writer = new StringWriter();
            OutputFormat format = new OutputFormat();
            format.setEncoding("utf-8");
            Serializer serializer = SerializerFactory.getSerializerFactory("xml").makeSerializer(writer, format);
            serializer.asDOMSerializer().serialize(document);
            String output = writer.toString();
            assertTrue("Should contain outer element", output.contains("outer"));
            assertTrue("Should contain inner element", output.contains("inner"));

            DOMSerializerImpl s = new DOMSerializerImpl();
            LSSerializer domWriter = ((DOMImplementationLS)DOMImplementationImpl.getDOMImplementation()).createLSSerializer();
            DOMConfiguration config = domWriter.getDomConfig();
            config.setParameter("error-handler", this);
            config.setParameter("namespaces", Boolean.FALSE);
            LSOutput dOut = new DOMOutputImpl();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dOut.setByteStream(baos);
            domWriter.write(document, dOut);
            output = baos.toString("utf-8");
            assertTrue("DOMWriter output should contain outer", output.contains("outer"));
      }
    /* (non-Javadoc)
     * @see org.apache.xerces.dom3.DOMErrorHandler#handleError(org.apache.xerces.dom3.DOMError)
     */
    public boolean handleError(DOMError error){
        short severity = error.getSeverity();
        if (severity == DOMError.SEVERITY_ERROR) {
            System.out.println("[dom3-error]: "+error.getMessage());
        }
        
        if (severity == DOMError.SEVERITY_FATAL_ERROR) {
                   System.out.println("[dom3-fatal-error]: "+error.getMessage());
               }

        if (severity == DOMError.SEVERITY_WARNING) {
            System.out.println("[dom3-warning]: "+error.getMessage());
        }
        return true;

    }


}
