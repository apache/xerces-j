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

package idc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import util.FailureMesgFragments;
import util.XercesAbstractTestCase;

/**
 * @author Mukul Gandhi <mukulg@apache.org>
 * @version $Id$
 */
public class IDConstraintTests extends XercesAbstractTestCase {
    
    public IDConstraintTests(String name) {
        super(name);
    }
    
    public void testIDConstraint1() {
        String xmlfile = "tests/idc/idc_1_valid_1.xml";
        String schemapath = "tests/idc/idc_1.xsd";    
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
        String xmlfile = "tests/idc/idc_1_invalid_1.xml";
        String schemapath = "tests/idc/idc_1.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint3() {        
        String xmlfile = "tests/idc/idc_1_invalid_2.xml";
        String schemapath = "tests/idc/idc_1.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint4() {        
        String xmlfile = "tests/idc/idc_1_invalid_3.xml";
        String schemapath = "tests/idc/idc_1.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint5() {
        String xmlfile = "tests/idc/idc_2_valid_1.xml";
        String schemapath = "tests/idc/idc_2.xsd";    
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
        String xmlfile = "tests/idc/idc_2_valid_2.xml";
        String schemapath = "tests/idc/idc_2.xsd";    
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
    
    public void testIDConstraint7() {        
        String xmlfile = "tests/idc/idc_2_invalid_1.xml";
        String schemapath = "tests/idc/idc_2.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint8() {        
        String xmlfile = "tests/idc/idc_2_invalid_2.xml";
        String schemapath = "tests/idc/idc_2.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint9() {
        String xmlfile = "tests/idc/idc_3_valid_1.xml";
        String schemapath = "tests/idc/idc_3.xsd";    
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
        String xmlfile = "tests/idc/idc_3_invalid_1.xml";
        String schemapath = "tests/idc/idc_3.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint11() {        
        String xmlfile = "tests/idc/idc_3_invalid_2.xml";
        String schemapath = "tests/idc/idc_3.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint12() {
        String xmlfile = "tests/idc/idc_4_valid_1.xml";
        String schemapath = "tests/idc/idc_4.xsd";    
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
    
    public void testIDConstraint13() {
        String xmlfile = "tests/idc/idc_4_valid_2.xml";
        String schemapath = "tests/idc/idc_4.xsd";    
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
        String xmlfile = "tests/idc/idc_4_invalid_1.xml";
        String schemapath = "tests/idc/idc_4.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint15() {        
        String xmlfile = "tests/idc/idc_4_invalid_2.xml";
        String schemapath = "tests/idc/idc_4.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint16() {
        String xmlfile = "tests/idc/idc_5_valid_1.xml";
        String schemapath = "tests/idc/idc_5.xsd";    
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
    
    public void testIDConstraint17() {        
        String xmlfile = "tests/idc/idc_5_invalid_1.xml";
        String schemapath = "tests/idc/idc_5.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint18() {        
        String xmlfile = "tests/idc/idc_5_invalid_2.xml";
        String schemapath = "tests/idc/idc_5.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }
    
    public void testIDConstraint19() {
        String xmlfile = "tests/idc/idc_6_valid_1.xml";
        String schemapath = "tests/idc/idc_6.xsd";    
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
    
    public void testIDConstraint20() {        
        String xmlfile = "tests/idc/idc_6_invalid_1.xml";
        String schemapath = "tests/idc/idc_6.xsd";    
        try {
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
        } catch(Exception ex) {
           ex.printStackTrace();
           assertTrue(false);
        }
    }

}
