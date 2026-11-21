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

package org.apache.xerces.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @xerces.internal
 * 
 * @author: Mukul Gandhi IBM
 * @version $Id: JiraBugsTests.java 1900013 2022-04-19 06:14:42Z mukulg $
 */
public class JiraBugsTests extends XercesAbstractTestCase {

	public JiraBugsTests(String name) {
		super(name);
	}
	
	public void testJira_1578_1() {
		String schemapath = fDataDir+"/jira_bugs/1578_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1578_2() {
		String schemapath = fDataDir+"/jira_bugs/1578_2.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1578_3() {
		String schemapath = fDataDir+"/jira_bugs/1578_3.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1578_4() {
		String schemapath = fDataDir+"/jira_bugs/1578_4.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1577_1() {
		String schemapath = fDataDir+"/jira_bugs/1577_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1577_2() {
		String schemapath = fDataDir+"/jira_bugs/1577_2.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1584_1() {
		String xmlfile = fDataDir+"/jira_bugs/3.xml";	
		String schemapath = fDataDir+"/jira_bugs/3.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("Attribute 'attr' must appear on element 'el'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1584_2() {
		String xmlfile = fDataDir+"/jira_bugs/3_1.xml";	
		String schemapath = fDataDir+"/jira_bugs/3_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 2);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-maxInclusive-valid: Value '60' is not facet-valid with respect to maxInclusive '50' for type 'st'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-type.3.1.3: The value '60' of element 'el' is not valid");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1584_3() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/jira_bugs/3.xml";	
		String schemapath = fDataDir+"/jira_bugs/3.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("Attribute 'attr' must appear on element 'el'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1584_4() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/jira_bugs/3_1.xml";	
		String schemapath = fDataDir+"/jira_bugs/3_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 2);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-maxInclusive-valid: Value '60' is not facet-valid with respect to maxInclusive '50' for type 'st'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-type.3.1.3: The value '60' of element 'el' is not valid");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1591_1() {
		String xmlfile = fDataDir+"/jira_bugs/ag.xml";	
		String schemapath = fDataDir+"/jira_bugs/ag3.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("Attribute 'id' belonging to namespace 'tns', must appear on element 'el'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1591_2() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/jira_bugs/ag.xml";	
		String schemapath = fDataDir+"/jira_bugs/ag3.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("Attribute 'id' belonging to namespace 'tns', must appear on element 'el'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1591_3() {
		String xmlfile = fDataDir+"/jira_bugs/gr.xml";	
		String schemapath = fDataDir+"/jira_bugs/gr3.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("The content of element 'el' is not complete. One of '{\"tns\":x}' is expected");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1591_4() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/jira_bugs/gr.xml";	
		String schemapath = fDataDir+"/jira_bugs/gr3.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("The content of element 'el' is not complete. One of '{\"tns\":x}' is expected");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1599_1() {
		String schemapath = fDataDir+"/jira_bugs/1599_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1599_2() {
		String schemapath = fDataDir+"/jira_bugs/1599_2.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1599_3() {
		String schemapath = fDataDir+"/jira_bugs/1599_3.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1603() {
		String schemapath = fDataDir+"/jira_bugs/1603_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
		} catch(SAXException ex) {
			// test expected error messages
			assertEquals("src-ct.5: Complex Type Definition Representation Error for type 'bCT'. If <openContent> is present and the actual value of its mode [attribute] is not none, then there must be an <any> among the [children] of <openContent>.", ex.getMessage());
		}
	}
	
	public void testJira_1605() {
		String schemapath = fDataDir+"/jira_bugs/1605_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
		} catch(SAXException ex) {
			// test expected error messages
            assertEquals("cos-nonambig: Element1 and Element1 (or elements from their substitution group) violate \"Unique Particle Attribution\". During validation against this schema, ambiguity would be created for those two particles.", ex.getMessage());
		}
	}
	
	public void testJira_1608_1() {
		String xmlfile = fDataDir+"/jira_bugs/1608_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1608_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1608_2() {
		String xmlfile = fDataDir+"/jira_bugs/1608_2.xml";
		String schemapath = fDataDir+"/jira_bugs/1608_1.xsd";		
		try {
			Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 2);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_1() {
		String xmlfile = fDataDir+"/jira_bugs/1674_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1674_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_2() {
		String xmlfile = fDataDir+"/jira_bugs/1674_2.xml";	
		String schemapath = fDataDir+"/jira_bugs/1674_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-assertion: Assertion evaluation ('y mod 2 = 0') for element 'X' on schema type 'Y2' did not succeed");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_3() {
		String xmlfile = fDataDir+"/jira_bugs/1674_3.xml";
		String schemapath = fDataDir+"/jira_bugs/1674_2.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_4() {
		String xmlfile = fDataDir+"/jira_bugs/1674_4.xml";	
		String schemapath = fDataDir+"/jira_bugs/1674_2.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 3);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-maxInclusive-valid: Value '1003' is not facet-valid with respect to maxInclusive '1000' for type 'y_INT'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-type.3.1.3: The value '1003' of element 'y' is not valid");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-assertion: Assertion evaluation ('y mod 2 = 0') for element 'X' on schema type 'Y2' did not succeed");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_5() {
		String xmlfile = fDataDir+"/jira_bugs/1674_5.xml";
		String schemapath = fDataDir+"/jira_bugs/1674_5.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1674_6() {
		String xmlfile = fDataDir+"/jira_bugs/1674_5.xml";	
		String schemapath = fDataDir+"/jira_bugs/1674_6.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-assertion: Assertion evaluation ('ns0:y mod 2 = 0') for element 'X' on schema type 'Y2' did not succeed. XPST0081 : Unknown prefix: ns0");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1698_1() {
		// process the schema document in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String schemapath = fDataDir+"/jira_bugs/1698_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		} catch(Exception ex) {		   
		   assertTrue(false);
		}
		
		try {
			// process the same, schema document in XSD 1.1 mode
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);			
            tearDown();
            setUp();
            Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
		} catch(Exception ex) {		   
		   // test expected error messages
           assertEquals("src-attribute.5: The property 'fixed' is present in attribute 'att1', so the value of 'use' must not be 'prohibited'.", ex.getMessage());
		}
	}
	
	public void testJira_1732_1() {
		String xmlfile = fDataDir+"/jira_bugs/1732_fn_matches.xml";
		String schemapath = fDataDir+"/jira_bugs/1732_fn_matches.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1732_2() {
		String xmlfile = fDataDir+"/jira_bugs/1732_fn_replace.xml";
		String schemapath = fDataDir+"/jira_bugs/1732_fn_replace.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1732_3() {
		String xmlfile = fDataDir+"/jira_bugs/1732_fn_tokenize.xml";
		String schemapath = fDataDir+"/jira_bugs/1732_fn_tokenize.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1726_1() {
		String xmlfile = fDataDir+"/jira_bugs/1726_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1726_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1726_2() {
		String xmlfile = fDataDir+"/jira_bugs/1726_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1726_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new SAXSource(new InputSource(new 
            		                           java.io.FileInputStream(xmlfile))));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1726_3() {
		String xmlfile = fDataDir+"/jira_bugs/1726_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1726_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1743_1() {
		String schemapath = fDataDir+"/jira_bugs/1743_1.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("enumeration-valid-restriction: Enumeration value 'a' is not in "
            		                          + "the value space of the base type, Color");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            fErrSysId.endsWith("1743_1.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_2() {
		String xmlfile = fDataDir+"/jira_bugs/1743_2.xml";
		String schemapath = fDataDir+"/jira_bugs/1743_2.xsd";		
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("enumeration-valid-restriction: Enumeration value '2 3' is not "
            		                         + "in the value space of the base type, ST1");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            fErrSysId.endsWith("1743_2.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testJira_1743_3() {
		String schemapath = fDataDir+"/jira_bugs/1743_3.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("enumeration-valid-restriction: Enumeration value '2 7 4' "
            		                          + "is not in the value space of the base type, ListType1");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            fErrSysId.endsWith("1743_3.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_4() {
		String schemapath = fDataDir+"/jira_bugs/1743_4.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("enumeration-valid-restriction: Enumeration value '2021-04-09' is not "
            		                         + "in the value space of the base type, UnionType1");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            fErrSysId.endsWith("1743_4.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_5() {
		String schemapath = fDataDir+"/jira_bugs/1743_5.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("enumeration-valid-restriction: Enumeration value '3d' is not "
            		                         + "in the value space of the base type, UnionType1");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            fErrSysId.endsWith("1743_5.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_6() {
		String schemapath = fDataDir+"/jira_bugs/1743_6.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_7() {
		String schemapath = fDataDir+"/jira_bugs/1743_7.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 2);		    
            fErrSysId.endsWith("1743_7.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1743_8() {
		String schemapath = fDataDir+"/jira_bugs/1743_8.xsd";	
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));		    
		    assertTrue(failureList.size() == 2);		    
            fErrSysId.endsWith("1743_8.xsd");
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);           		   		                       
		}
	}
	
	public void testJira_1744_1() {
		String xmlfile = fDataDir+"/jira_bugs/1744_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_1.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1744_2() {
		String xmlfile = fDataDir+"/jira_bugs/1744_2.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_1.xsd";		
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-datatype-valid.4.1.4: The value '3 2012-08-07' of element 'X' is not valid "
            		                          + "with the required simple type. Value '3' is not valid with any member of union type 'INT_AND_DATE'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testJira_1744_3() {
		String xmlfile = fDataDir+"/jira_bugs/1744_3.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_2.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1744_4() {
		String xmlfile = fDataDir+"/jira_bugs/1744_4.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_3.xsd";		
		try {
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testJira_1744_5() {
		String xmlfile = fDataDir+"/jira_bugs/1744_4.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_4.xsd";		
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertTrue(failureList.size() == 1);
		    // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-datatype-valid.4.1.4: The value '2012-10-07 7756-10-05' of attribute "
            		                         + "'attr1' on element 'X' is not valid with the required simple type. Value '7756-10-05' "
            		                         + "is not valid with any member of union type 'DATE_TYPE'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testJira_1744_6() {
		String xmlfile = fDataDir+"/jira_bugs/1744_5.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_5.xsd";		
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertTrue(failureList.size() == 3);
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testJira_1744_7() {
		String xmlfile = fDataDir+"/jira_bugs/1744_6.xml";
		String schemapath = fDataDir+"/jira_bugs/1744_6.xsd";		
		try {
			fSchemaFactory.setErrorHandler(this);
		    Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new DOMSource(getDomDocument(xmlfile)));
            assertTrue(failureList.size() == 3);
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testJira_1765_1() {
		String xmlfile = fDataDir+"/jira_bugs/1765_1.xml";
		String schemapath = fDataDir+"/jira_bugs/1765.xsd";		
		try {
			Schema s = fSchemaFactory.newSchema(new File(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
		
}