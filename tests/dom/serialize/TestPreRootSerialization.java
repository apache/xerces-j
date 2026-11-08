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

import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * Tests serialization of comments and processing instructions which
 * appear before the document type declaration.
 */
public class TestPreRootSerialization extends TestCase {

    public void testProcessingInstructionBeforeDoctypeSAX() throws Exception {
        String output = serializeWithSAX(false);
        assertBefore(output, "<?pi before", "<!DOCTYPE");
    }

    public void testCommentBeforeDoctypeSAX() throws Exception {
        String output = serializeWithSAX(false);
        assertBefore(output, "<!--before-dtd-->", "<!DOCTYPE");
    }

    public void testProcessingInstructionAfterDoctypeSAX() throws Exception {
        String output = serializeWithSAX(false);
        assertBefore(output, "<!DOCTYPE", "<?pi after");
    }

    public void testCommentAfterDoctypeSAX() throws Exception {
        String output = serializeWithSAX(false);
        assertBefore(output, "<!DOCTYPE", "<!--after-dtd-->");
    }

    public void testProcessingInstructionBeforeDoctypeDOM() throws Exception {
        String output = serializeWithDOM();
        assertBefore(output, "<?pi before", "<!DOCTYPE");
    }

    public void testCommentBeforeDoctypeDOM() throws Exception {
        String output = serializeWithDOM();
        assertBefore(output, "<!--before-dtd-->", "<!DOCTYPE");
    }

    public void testCommentAfterDoctypeDOM() throws Exception {
        String output = serializeWithDOM();
        assertBefore(output, "<!DOCTYPE", "<!--after-dtd-->");
    }

    public void testNoDoctypeSAX() throws Exception {
        String output = serializeWithSAX(true);
        assertBefore(output, "<!--before-dtd-->", "<root");
        assertBefore(output, "<?pi before", "<root");
        assertEquals(-1, output.indexOf("<!DOCTYPE"));
    }

    private static String serializeWithSAX(boolean omitDoctype) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer();
        OutputFormat format = new OutputFormat();
        format.setOmitDocumentType(omitDoctype);
        serializer.setOutputFormat(format);
        serializer.setOutputByteStream(out);
        serializer.startDocument();
        serializer.comment("before-dtd");
        serializer.processingInstruction("pi", "before");
        if (!omitDoctype) {
            serializer.startDTD("root", "pub", "sys");
        }
        serializer.comment("after-dtd");
        serializer.processingInstruction("pi", "after");
        serializer.startElement("root", null, "root", null);
        serializer.endElement("root", null, "root");
        serializer.endDocument();
        return out.toString("UTF-8");
    }

    private static String serializeWithDOM() throws Exception {
        DOMImplementation implementation = CoreDOMImplementationImpl.getDOMImplementation();
        DocumentType doctype = implementation.createDocumentType("root", "pub", "sys");
        Document doc = implementation.createDocument(null, "root", doctype);
        doc.insertBefore(doc.createProcessingInstruction("pi", "before"), doctype);
        doc.insertBefore(doc.createComment("before-dtd"), doctype);
        doc.insertBefore(doc.createComment("after-dtd"), doc.getDocumentElement());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer();
        serializer.setOutputByteStream(out);
        serializer.serialize(doc);
        return out.toString("UTF-8");
    }

    private static void assertBefore(String output, String first, String second) {
        int firstIndex = output.indexOf(first);
        int secondIndex = output.indexOf(second);
        assertTrue("Expected '" + first + "' in: " + output, firstIndex >= 0);
        assertTrue("Expected '" + second + "' in: " + output, secondIndex >= 0);
        assertTrue("Expected '" + first + "' before '" + second + "' in: " + output,
                   firstIndex < secondIndex);
    }
}
