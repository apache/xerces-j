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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXParseException;

/**
 * @xerces.internal
 * 
 * @author: Mukul Gandhi IBM
 * @version $Id: IDConstraintTests.java 1871917 2019-12-23 06:57:01Z mukulg $
 */
public class IDConstraintTests extends XercesAbstractTestCase {

	public IDConstraintTests(String name) {
		super(name);
	}
	
	public void testIDConstraint1() {
		String xmlfile = fDataDir+"/idconstraints/test1_1.xml";
		String schemapath = fDataDir+"/idconstraints/test1.xsd";	
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
	
	public void testIDConstraint2() {
		String xmlfile = fDataDir+"/idconstraints/test1_2.xml";
		String schemapath = fDataDir+"/idconstraints/test1.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [a2] found for identity constraint \"key_a\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint3() {
		String xmlfile = fDataDir+"/idconstraints/test2_1.xml";
		String schemapath = fDataDir+"/idconstraints/test2.xsd";	
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
	
	public void testIDConstraint4() {
		String xmlfile = fDataDir+"/idconstraints/test2_2.xml";
		String schemapath = fDataDir+"/idconstraints/test2.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [a2] found for identity constraint \"key_a\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint5() {
		String xmlfile = fDataDir+"/idconstraints/test3_1.xml";
		String schemapath = fDataDir+"/idconstraints/test3.xsd";	
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
	
	public void testIDConstraint6() {
		String xmlfile = fDataDir+"/idconstraints/test3_2.xml";
		String schemapath = fDataDir+"/idconstraints/test3.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [a2] found for identity constraint \"key_a\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint7() {
		String xmlfile = fDataDir+"/idconstraints/test3_1.xml";
		String schemapath = fDataDir+"/idconstraints/test4.xsd";	
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
	
	public void testIDConstraint8() {
		String xmlfile = fDataDir+"/idconstraints/test3_2.xml";
		String schemapath = fDataDir+"/idconstraints/test4.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [a2] found for identity constraint \"key_a\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint9() {
		String xmlfile = fDataDir+"/idconstraints/test4_1.xml";
		String schemapath = fDataDir+"/idconstraints/test5.xsd";	
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
	
	public void testIDConstraint10() {
		String xmlfile = fDataDir+"/idconstraints/test4_2.xml";
		String schemapath = fDataDir+"/idconstraints/test5.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'keyref_data' with value 'a4' not found for identity constraint of element 'X'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint11() {
		String xmlfile = fDataDir+"/idconstraints/test4_3.xml";
		String schemapath = fDataDir+"/idconstraints/test6.xsd";	
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
	
	public void testIDConstraint12() {
		String xmlfile = fDataDir+"/idconstraints/test4_4.xml";
		String schemapath = fDataDir+"/idconstraints/test6.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'keyref_data' with value 'a4' not found for identity constraint of element 'X'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint13() {
		String xmlfile = fDataDir+"/idconstraints/test3_1.xml";
		String schemapath = fDataDir+"/idconstraints/test7.xsd";	
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
	
	public void testIDConstraint14() {
		String xmlfile = fDataDir+"/idconstraints/test3_2.xml";
		String schemapath = fDataDir+"/idconstraints/test7.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [a2] found for identity constraint \"key_a\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint15() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/jira_1585.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1585.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint16() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/jira_1594.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1594.xsd";
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'keyref' with value '1' not found for identity constraint of element 'personnel'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'keyref' with value '1' not found for identity constraint of element 'personnel'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint17() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_1_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint18() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_1_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 3);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [nffg1] found for identity constraint \"nffgName_key\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 3);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [nffg1] found for identity constraint \"nffgName_key\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint19() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_1_invalid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 3);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 3);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint20() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_1_invalid_3.xml";
		String schemapath = fDataDir+"/idconstraints/idc_1.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 4);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node2] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 4);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node2] found for identity constraint \"nodeName_key\" of element \"nffg\"");
            expectedMsgList.add(mesgFragments);
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint21() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_2_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_2.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint22() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_2_valid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_2.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint23() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_2_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_2.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint24() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_2_invalid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_2.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint25() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_3_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_3.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint26() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_3_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_3.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint27() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_3_invalid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_3.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint28() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_4_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_4.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint29() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_4_valid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_4.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint30() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_4_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_4.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint31() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_4_invalid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_4.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint32() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_5_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_5.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint33() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_5_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_5.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint34() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_5_invalid_2.xml";
		String schemapath = fDataDir+"/idconstraints/idc_5.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint35() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_ref_attr_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_ref_attr_1.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   // schema is incorrect. an exception indicates, that this test has 'passed'.
		   assertEquals(ex instanceof SAXParseException, true);		   
		   assertEquals("s4s-att-not-allowed: Attribute 'ref' cannot appear in element 'key'.", ex.getMessage());
		}
	}
	
	public void testIDConstraint36() {
		String xmlfile = fDataDir+"/idconstraints/idc_ref_attr_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_ref_attr_1.xsd";	
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
	
	public void testIDConstraint37() {		
		String xmlfile = fDataDir+"/idconstraints/idc_ref_attr_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_ref_attr_1.xsd";			
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            List expectedMsgList = new ArrayList();
            FailureMesgFragments mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [2] found for identity constraint \"a_key\" of element \"Y\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint38() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_6_valid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_6.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint39() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/idc_6_invalid_1.xml";
		String schemapath = fDataDir+"/idconstraints/idc_6.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [7] found for identity constraint \"key_1\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [7] found for identity constraint \"key_1\" of element \"X\"");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint40() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/valid_1_1515.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1515.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint41() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/valid_2_1515.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1515.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint42() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/invalid_1_1515.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1515.xsd";	
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
            mesgFragments.setMessageFragment("Identity Constraint error: the keyref identity constraint \"keyref\" refers to a key or unique that is out of scope");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("Identity Constraint error: the keyref identity constraint \"keyref\" refers to a key or unique that is out of scope");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint43() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/jira_1714_valid.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1714.xsd";	
		try {
			fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);            
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}
	
	public void testIDConstraint44() {
		// run validation in XSD 1.0 mode
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		String xmlfile = fDataDir+"/idconstraints/jira_1714_invalid.xml";
		String schemapath = fDataDir+"/idconstraints/jira_1714.xsd";	
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
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'Collectiontype_defined' with value 'object1' not found for identity constraint of element 'Root'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
            
            // run validation again with same input files, now in XSD 1.1 mode
            tearDown();
            setUp();
            s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertTrue(failureList.size() == 1);
            // test expected error messages
            expectedMsgList = new ArrayList();
            mesgFragments = new FailureMesgFragments();
            mesgFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'Collectiontype_defined' with value 'object1' not found for identity constraint of element 'Root'");
            expectedMsgList.add(mesgFragments);
            assertTrue(areErrorMessagesConsistent(expectedMsgList));
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}

}