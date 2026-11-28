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

package org.apache.xerces.parsers;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.xerces.dom.CDATASectionImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class CDATABufferingTest extends TestCase {

    public void testCdataChunksAreBuffered() throws Exception {
        DOMParser parser = new DOMParser();
        parser.setProperty("http://apache.org/xml/properties/dom/document-class-name", CountingDocument.class.getName());
        parser.parse(new InputSource(new StringReader("<root><![CDATA[one\ntwo\nthree]]>\nsuffix</root>")));

        Document document = parser.getDocument();
        CDATASection section = (CDATASection) document.getDocumentElement().getFirstChild();
        assertEquals("one\ntwo\nthree", section.getData());
        assertEquals(0, ((CountingCDATASection) section).appendDataCalls);
        assertEquals("\nsuffix", section.getNextSibling().getNodeValue());
    }

    public static final class CountingDocument extends DocumentImpl {
        public CDATASection createCDATASection(String data) {
            return new CountingCDATASection(this, data);
        }
    }

    public static final class CountingCDATASection extends CDATASectionImpl {
        int appendDataCalls;

        CountingCDATASection(CoreDocumentImpl ownerDocument, String data) {
            super(ownerDocument, data);
        }

        public void appendData(String data) {
            appendDataCalls++;
            super.appendData(data);
        }
    }
}
