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

package org.apache.xerces.impl.xs.identity;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.xml.sax.SAXParseException;

public class IdentityConstraintRestrictionTest extends TestCase {
    private static final String SCHEMA_START =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>";
    private static final String KEY =
            "<xs:key name='baseKey'><xs:selector xpath='.'/><xs:field xpath='.'/></xs:key>";
    private static final String UNIQUE =
            "<xs:unique name='newUnique'><xs:selector xpath='.'/><xs:field xpath='.'/></xs:unique>";

    public void testRejectsAddedIdentityConstraint() throws Exception {
        assertInvalidRestriction(elementDeclaration(""), elementDeclaration(UNIQUE));
    }

    public void testRejectsDifferentIdentityConstraint() throws Exception {
        assertInvalidRestriction(elementDeclaration(KEY), elementDeclaration(UNIQUE));
    }

    public void testAcceptsEmptySubset() throws Exception {
        compile(restriction(elementDeclaration(KEY), elementDeclaration("")));
    }

    public void testAcceptsSameGlobalElementWithIdentityConstraint() throws Exception {
        String reference = "<xs:element ref='item'/>";
        compile(elementDeclaration(KEY) + restriction(reference, reference));
    }

    private void assertInvalidRestriction(String base, String derived) throws Exception {
        try {
            compile(restriction(base, derived));
            fail("Accepted a restriction with an identity constraint absent from the base");
        } catch (SAXParseException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().startsWith("rcase-NameAndTypeOK.5:"));
        }
    }

    private void compile(String declarations) throws Exception {
        new XMLSchemaFactory().newSchema(new StreamSource(
                new StringReader(SCHEMA_START + declarations + "</xs:schema>")));
    }

    private String elementDeclaration(String constraints) {
        return "<xs:element name='item' type='xs:integer'>" + constraints + "</xs:element>";
    }

    private String restriction(String base, String derived) {
        return "<xs:complexType name='Base'><xs:sequence>" + base
                + "</xs:sequence></xs:complexType>"
                + "<xs:complexType name='Derived'><xs:complexContent>"
                + "<xs:restriction base='Base'><xs:sequence>" + derived
                + "</xs:sequence></xs:restriction></xs:complexContent></xs:complexType>";
    }
}
