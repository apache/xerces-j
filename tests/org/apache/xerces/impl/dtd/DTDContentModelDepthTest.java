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

package org.apache.xerces.impl.dtd;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests that deeply nested DTD content models do not exhaust the stack.
 */
public class DTDContentModelDepthTest extends TestCase {

    private static final int DEPTH = 5000;

    public void testDeeplyNestedContentModelDoesNotOverflowStack()
            throws Exception {
        SAXParser parser = new SAXParser();
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
        parser.setContentHandler(new DefaultHandler());
        parser.parse(new InputSource(new StringReader(createDocument(DEPTH))));
    }

    private String createDocument(int depth) {
        StringBuilder document = new StringBuilder("<!DOCTYPE a [<!ELEMENT a ");
        for (int i = 0; i < depth; i++) {
            document.append("(e").append(i).append(',');
        }
        document.append('e').append(depth);
        for (int i = 0; i < depth; i++) {
            document.append(')');
        }
        document.append('>');
        for (int i = 0; i <= depth; i++) {
            document.append("<!ELEMENT e").append(i).append(" EMPTY>");
        }
        document.append("]><a>");
        for (int i = 0; i <= depth; i++) {
            document.append("<e").append(i).append("/>");
        }
        document.append("</a>");
        return document.toString();
    }
}
