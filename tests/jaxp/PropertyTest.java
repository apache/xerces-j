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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import junit.framework.TestCase;

public class PropertyTest extends TestCase {

    public void testSchemaProperties() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(true);
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            "http://www.w3.org/2001/XMLSchema");
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaSource",
            new String[] { "personal.xsd", "ipo.xsd" });
        parser.parse("tests/jaxp/data/personal-schema.xml", new DefaultHandler());

        // Second parse should fail
        parser = spf.newSAXParser();
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            "http://www.w3.org/2001/XMLSchema");
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaSource",
            new String[] { "address.xsd", "ipo.xsd" });
        try {
            parser.parse("tests/jaxp/data/personal-schema.xml", new DefaultHandler());
            fail("Should have thrown exception for address.xsd, ipo.xsd");
        } catch (Exception e) {
            // expected
        }

        // Third parse should fail
        parser = spf.newSAXParser();
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            "http://www.w3.org/2001/XMLSchema");
        parser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaSource",
            new String[] { "personal.xsd", "ipo.xsd", "a.xsd" });
        try {
            parser.parse("tests/jaxp/data/personal-schema.xml", new DefaultHandler());
            fail("Should have thrown exception for personal.xsd, ipo.xsd, a.xsd");
        } catch (Exception e) {
            // expected
        }
    }
}
