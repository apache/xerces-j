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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/** 
 * This testcase tests for following scenarios as per JAXP 1.2 specification.
 *
 * 1.Parser(SAX and DOM) should ignore SchemaLanguage property when validation
 *  feature is set to false.
 * 2.SAXParser should throw SAXNotSupportedException when SchemaSource property is
 *  set without setting SchemaLanguage property.
 * 3.DOMParser should throw IllegalArgumentException when SchemaSource property is
 *  set without setting SchemaLanguage property.
 *
 * @author k.venugopal@sun.com
 * 
 * @version $Id$
 */
public class JAXPSpecTest extends TestCase {
    
    /** 
     * Schema Language property should be ignored if
     * validation feature is set to false
     * @throws Exception
     */
    
    public void testSchemaLanguageSAX() throws Exception{
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser saxParser = spf.newSAXParser();
        saxParser.setProperty(
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
        "http://www.w3.org/2001/XMLSchema");
        saxParser.parse("tests/jaxp/data/personal-schema.xml", new DefaultHandler());
    }
    
    /** SAXParser should throw SAXNotSupportedException when SchemaSource property is
     * set without setting SchemaLanguage property
     * @throws Exception
     */
    
    public void testSchemaSourceSAX() throws Exception{
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(true);
            SAXParser saxParser = spf.newSAXParser();
            saxParser.setProperty(
            "http://java.sun.com/xml/jaxp/properties/schemaSource",
            "tests/jaxp/data/personal-schema.xsd");
            saxParser.parse("tests/jaxp/data/personal-schema.xml", new DefaultHandler());
            fail("Should have thrown SAXNotSupportedException");
        } catch (SAXNotSupportedException e) {
            // expected
        }
    }
    
    /** Schema Language property should be ignored if
     * validation feature is set to false
     * @throws Exception  */
    
    public void testSchemaLanguageDOM() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setAttribute(
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
        "http://www.w3.org/2001/XMLSchema");
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(new DefaultHandler());
        Document document = docBuilder.parse(
        new File("tests/jaxp/data/personal-schema.xml"));
    }
    
    /** DOMParser should throw IllegalArgumentException when SchemaSource property is
     * set without setting SchemaLanguage property.
     * @throws Exception
     */
    
    public void testSchemaSourceDOM() throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            dbf.setAttribute(
            "http://java.sun.com/xml/jaxp/properties/schemaSource",
            "tests/jaxp/data/personal-schema.xsd");
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            docBuilder.setErrorHandler(new DefaultHandler());
            Document document = docBuilder.parse(
            "tests/jaxp/data/personal-schema.xml");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}

