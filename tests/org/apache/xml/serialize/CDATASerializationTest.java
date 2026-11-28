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

package org.apache.xml.serialize;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * Tests serialization of CDATA sections at the end of a document fragment.
 */
public class CDATASerializationTest extends TestCase {

    public void testCDATAAtEndOfDocumentFragmentIsClosed() throws Exception {
        Document document = new DocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
        fragment.appendChild(document.createTextNode(".txt."));
        fragment.appendChild(document.createCDATASection(".cdt."));
        fragment.appendChild(document.createTextNode(".txt."));
        fragment.appendChild(document.createCDATASection(".cdt."));

        StringWriter writer = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(writer, new OutputFormat());
        serializer.serialize(fragment);

        assertEquals(".txt.<![CDATA[.cdt..txt..cdt.]]>", writer.toString());
    }
}
