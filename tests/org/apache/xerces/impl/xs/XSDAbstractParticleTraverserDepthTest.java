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

package org.apache.xerces.impl.xs;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

/**
 * Tests that deeply nested schema particles do not exhaust the stack.
 */
public class XSDAbstractParticleTraverserDepthTest extends TestCase {

    private static final int DEPTH = 5000;

    public void testDeeplyNestedSequencesDoNotOverflowStack()
            throws Exception {
        assertNotNull(createSchema("sequence", DEPTH));
    }

    public void testDeeplyNestedChoicesDoNotOverflowStack()
            throws Exception {
        assertNotNull(createSchema("choice", DEPTH));
    }

    private Schema createSchema(String modelGroup, int depth)
            throws Exception {
        StringBuilder schema = new StringBuilder();
        schema.append("<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>");
        schema.append("<xs:element name='root'><xs:complexType>");
        for (int i = 0; i < depth; i++) {
            schema.append("<xs:").append(modelGroup).append('>');
        }
        schema.append("<xs:element name='leaf' type='xs:string'/>");
        for (int i = 0; i < depth; i++) {
            schema.append("</xs:").append(modelGroup).append('>');
        }
        schema.append("</xs:complexType></xs:element></xs:schema>");
        SchemaFactory factory = SchemaFactory.newInstance(
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return factory.newSchema(new StreamSource(new StringReader(
                schema.toString())));
    }
}
