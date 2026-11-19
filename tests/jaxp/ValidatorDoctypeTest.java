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

import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.apache.xerces.parsers.SAXParser;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ValidatorDoctypeTest extends TestCase {
    private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String SCHEMA = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
            + "<xs:element name='root' type='xs:string'/></xs:schema>";
    private static final String DOCUMENT_WITHOUT_DOCTYPE = "<root/>";
    private static final String DOCUMENT_WITH_DOCTYPE = "<!DOCTYPE root>" + DOCUMENT_WITHOUT_DOCTYPE;

    private Validator validator;

    @Override
    protected void setUp() throws Exception {
        validator = new XMLSchemaFactory().newSchema(streamSource(SCHEMA)).newValidator();
    }

    public void testStreamSourceRejectsDoctypeOnFirstValidation() throws Exception {
        validator.setFeature(DISALLOW_DOCTYPE, true);

        assertDoctypeRejected(streamSource(DOCUMENT_WITH_DOCTYPE));
        validator.validate(streamSource(DOCUMENT_WITHOUT_DOCTYPE));
    }

    public void testStreamSourceTracksFeatureChangesAcrossSourceTypes() throws Exception {
        validator.validate(streamSource(DOCUMENT_WITH_DOCTYPE));

        validator.setFeature(DISALLOW_DOCTYPE, true);
        validator.validate(saxSource(DOCUMENT_WITHOUT_DOCTYPE));
        assertDoctypeRejected(streamSource(DOCUMENT_WITH_DOCTYPE));

        validator.setFeature(DISALLOW_DOCTYPE, false);
        validator.validate(saxSource(DOCUMENT_WITHOUT_DOCTYPE));
        validator.validate(streamSource(DOCUMENT_WITH_DOCTYPE));
    }

    public void testValidatorCreatedSaxReaderRejectsDoctype() throws Exception {
        validator.setFeature(DISALLOW_DOCTYPE, true);

        assertDoctypeRejected(saxSource(DOCUMENT_WITH_DOCTYPE));
        validator.validate(saxSource(DOCUMENT_WITHOUT_DOCTYPE));
    }

    public void testSuppliedSaxReaderRejectsDoctypeAndRestoresSetting() throws Exception {
        validator.setFeature(DISALLOW_DOCTYPE, true);
        XMLReader reader = new SAXParser();
        reader.setFeature(DISALLOW_DOCTYPE, false);

        assertDoctypeRejected(new SAXSource(reader, new InputSource(new StringReader(DOCUMENT_WITH_DOCTYPE))));
        assertFalse(reader.getFeature(DISALLOW_DOCTYPE));

        reader.setFeature(DISALLOW_DOCTYPE, true);
        validator.validate(new SAXSource(reader, new InputSource(new StringReader(DOCUMENT_WITHOUT_DOCTYPE))));
        assertTrue(reader.getFeature(DISALLOW_DOCTYPE));
    }

    public void testUnsupportedSaxReaderFailsBeforeParsing() throws Exception {
        validator.setFeature(DISALLOW_DOCTYPE, true);
        XMLReader reader = new XMLFilterImpl() {
            @Override
            public void parse(InputSource input) {
                fail("Reader must support DOCTYPE rejection before parsing");
            }
        };
        try {
            validator.validate(new SAXSource(reader, new InputSource(new StringReader(DOCUMENT_WITHOUT_DOCTYPE))));
            fail("Expected an exception for an unsupported SAX reader");
        }
        catch (SAXNotRecognizedException expected) {
            // The reader does not support the feature.
        }
    }

    public void testDomSourceRejectsDoctypeOnDocumentAndElement() throws Exception {
        DOMImplementation implementation = DOMImplementationImpl.getDOMImplementation();
        Document document = implementation.createDocument(null, "root",
                implementation.createDocumentType("root", null, null));
        validator.validate(new DOMSource(document));

        validator.setFeature(DISALLOW_DOCTYPE, true);
        assertDoctypeRejected(new DOMSource(document));
        assertDoctypeRejected(new DOMSource(document.getDocumentElement()));
        validator.validate(new DOMSource(implementation.createDocument(null, "root", null)));
    }

    public void testStaxSourceRejectsDeliveredDtdEvents() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        validator.validate(new StAXSource(factory.createXMLStreamReader(new StringReader(DOCUMENT_WITH_DOCTYPE))));
        validator.validate(new StAXSource(factory.createXMLEventReader(new StringReader(DOCUMENT_WITH_DOCTYPE))));

        validator.setFeature(DISALLOW_DOCTYPE, true);
        assertDoctypeRejected(new StAXSource(factory.createXMLStreamReader(new StringReader(DOCUMENT_WITH_DOCTYPE))));
        assertDoctypeRejected(new StAXSource(factory.createXMLEventReader(new StringReader(DOCUMENT_WITH_DOCTYPE))));
        validator.validate(new StAXSource(factory.createXMLStreamReader(new StringReader(DOCUMENT_WITHOUT_DOCTYPE))));
        validator.validate(new StAXSource(factory.createXMLEventReader(new StringReader(DOCUMENT_WITHOUT_DOCTYPE))));
    }

    private static StreamSource streamSource(String xml) {
        return new StreamSource(new StringReader(xml));
    }

    private static SAXSource saxSource(String xml) {
        return new SAXSource(new InputSource(new StringReader(xml)));
    }

    private void assertDoctypeRejected(Source source) throws Exception {
        try {
            validator.validate(source);
            fail("Expected the DOCTYPE declaration to be rejected");
        }
        catch (SAXParseException expected) {
            // The same document is valid when the DOCTYPE is allowed.
        }
    }
}
