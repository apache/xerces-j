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

package jaxp;

import java.io.StringReader;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

public class ValidatorNamespaceTest extends TestCase {
    private static final String SCHEMA =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'"
            + " targetNamespace='urn:inventory' elementFormDefault='qualified'>"
            + "<xs:element name='inventory'><xs:complexType><xs:sequence>"
            + "<xs:element name='item' type='xs:string'/>"
            + "</xs:sequence></xs:complexType></xs:element></xs:schema>";

    public void testAcceptsEqualNamespaceFromSaxFilter() throws Exception {
        validate("<inventory xmlns='urn:inventory'><item>book</item></inventory>");
    }

    public void testRejectsDifferentNamespaceFromSaxFilter() throws Exception {
        try {
            validate("<inventory xmlns='urn:inventory'><item xmlns='urn:other'>book</item></inventory>");
            fail("Accepted a child element in the wrong namespace");
        } catch (SAXParseException expected) {
            assertTrue(expected.getMessage().contains("cvc-complex-type.2.4.a"));
        }
    }

    private void validate(String document) throws Exception {
        Validator validator = new XMLSchemaFactory().newSchema(
                new StreamSource(new StringReader(SCHEMA))).newValidator();
        XMLFilterImpl filter = new XMLFilterImpl(new SAXParser()) {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                super.startElement(new String(uri), localName, qName, attributes);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(new String(uri), localName, qName);
            }
        };
        validator.validate(new SAXSource(filter, new InputSource(new StringReader(document))));
    }
}
