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

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class ValidatorDoctypeTest extends TestCase {
    private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String SCHEMA = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
            + "<xs:element name='root' type='xs:string'/></xs:schema>";
    private static final String DOCUMENT = "<root/>";
    private static final String DOCUMENT_WITH_DOCTYPE = "<!DOCTYPE root>" + DOCUMENT;

    public void testStreamSourceRejectsDoctypeOnFirstValidation() throws Exception {
        Validator validator = newValidator();
        validator.setFeature(DISALLOW_DOCTYPE, true);

        assertDoctypeRejected(validator, streamSource(DOCUMENT_WITH_DOCTYPE));
        validator.validate(streamSource(DOCUMENT));
    }

    public void testStreamSourceTracksFeatureChangesAcrossSourceTypes() throws Exception {
        Validator validator = newValidator();
        validator.validate(streamSource(DOCUMENT_WITH_DOCTYPE));

        validator.setFeature(DISALLOW_DOCTYPE, true);
        validator.validate(saxSource(DOCUMENT));
        assertDoctypeRejected(validator, streamSource(DOCUMENT_WITH_DOCTYPE));

        validator.setFeature(DISALLOW_DOCTYPE, false);
        validator.validate(saxSource(DOCUMENT));
        validator.validate(streamSource(DOCUMENT_WITH_DOCTYPE));
    }

    public void testValidatorCreatedSaxReaderRejectsDoctype() throws Exception {
        Validator validator = newValidator();
        validator.setFeature(DISALLOW_DOCTYPE, true);

        assertDoctypeRejected(validator, saxSource(DOCUMENT_WITH_DOCTYPE));
        validator.validate(saxSource(DOCUMENT));

        validator.reset();
        validator.validate(saxSource(DOCUMENT_WITH_DOCTYPE));
    }

    private static Validator newValidator() throws Exception {
        return new XMLSchemaFactory().newSchema(streamSource(SCHEMA)).newValidator();
    }

    private static StreamSource streamSource(String xml) {
        return new StreamSource(new StringReader(xml));
    }

    private static SAXSource saxSource(String xml) {
        return new SAXSource(new InputSource(new StringReader(xml)));
    }

    private static void assertDoctypeRejected(Validator validator, Source source) throws Exception {
        try {
            validator.validate(source);
            fail("Expected the DOCTYPE declaration to be rejected");
        }
        catch (SAXParseException expected) {
            // The same document is valid when the DOCTYPE is allowed.
        }
    }
}
