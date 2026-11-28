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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import util.FailureMesgFragments;
import util.XercesAbstractTestCase;

/**
 * @author <a href="mailto:mukulg@apache.org">Mukul Gandhi</a>
 * @version $Id$
 */
public class IDConstraintTest extends XercesAbstractTestCase {
    
    public IDConstraintTest(String name) {
        super(name);
    }
    
    public void testIndexedDuplicateTuples() throws Exception {
        checkDuplicateTuple("key", "cvc-identity-constraint.4.2.2");
        checkDuplicateTuple("unique", "cvc-identity-constraint.4.1");
    }

    private void checkDuplicateTuple(String constraint, String errorCode) throws Exception {
        String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                + "<xs:element name='root'><xs:complexType><xs:sequence>"
                + "<xs:element name='row' maxOccurs='unbounded'><xs:complexType>"
                + "<xs:attribute name='label' type='xs:string'/>"
                + "<xs:attribute name='number' type='xs:decimal'/>"
                + "</xs:complexType></xs:element></xs:sequence></xs:complexType>"
                + "<xs:" + constraint + " name='pair'><xs:selector xpath='row'/>"
                + "<xs:field xpath='@label'/><xs:field xpath='@number'/>"
                + "</xs:" + constraint + "></xs:element></xs:schema>";
        Validator validator = fSchemaFactory.newSchema(new StreamSource(new StringReader(schema))).newValidator();
        // Aa and BB have the same Java string hash.
        String rows = "<row label='Aa' number='1'/><row label='BB' number='1'/>"
                + "<row label='Aa' number='2'/>";
        validator.validate(new StreamSource(new StringReader("<root>" + rows + "</root>")));
        try {
            validator.validate(new StreamSource(new StringReader(
                    "<root>" + rows + "<row label='Aa' number='1.00'/></root>")));
            fail("Expected " + errorCode);
        }
        catch (SAXParseException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().startsWith(errorCode + ":"));
        }
    }

    public void testIDConstraint1() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_1_valid_1.xml";
        String schemaPath = "tests/idc/idc_1.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint2() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_1_invalid_1.xml";
        String schemaPath = "tests/idc/idc_1.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(3, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [nffg1] found for identity constraint \"nffgName_key\" of element \"root\"");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint3() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_1_invalid_2.xml";
        String schemaPath = "tests/idc/idc_1.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(3, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint4() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_1_invalid_3.xml";
        String schemaPath = "tests/idc/idc_1.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(4, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node1] found for identity constraint \"nodeName_key\" of element \"nffg\"");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'destNodeRef_key' with value 'node2' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [node2] found for identity constraint \"nodeName_key\" of element \"nffg\"");
        expectedMessages.add(messageFragments);
        messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'srcNodeRef_key' with value 'node1' not found for identity constraint of element 'nffg'");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint5() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_2_valid_1.xml";
        String schemaPath = "tests/idc/idc_2.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint6() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_2_valid_2.xml";
        String schemaPath = "tests/idc/idc_2.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint7() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_2_invalid_1.xml";
        String schemaPath = "tests/idc/idc_2.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint8() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_2_invalid_2.xml";
        String schemaPath = "tests/idc/idc_2.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint9() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_3_valid_1.xml";
        String schemaPath = "tests/idc/idc_3.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint10() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_3_invalid_1.xml";
        String schemaPath = "tests/idc/idc_3.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint11() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_3_invalid_2.xml";
        String schemaPath = "tests/idc/idc_3.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint12() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_4_valid_1.xml";
        String schemaPath = "tests/idc/idc_4.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint13() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_4_valid_2.xml";
        String schemaPath = "tests/idc/idc_4.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint14() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_4_invalid_1.xml";
        String schemaPath = "tests/idc/idc_4.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint15() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_4_invalid_2.xml";
        String schemaPath = "tests/idc/idc_4.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.1: Duplicate unique value [3] found for identity constraint \"unique_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint16() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_5_valid_1.xml";
        String schemaPath = "tests/idc/idc_5.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint17() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_5_invalid_1.xml";
        String schemaPath = "tests/idc/idc_5.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [3] found for identity constraint \"key_1\" of element \"root\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint18() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_5_invalid_2.xml";
        String schemaPath = "tests/idc/idc_5.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.1.a: Element \"root\" has no value for the key \"key_1\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint19() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_6_valid_1.xml";
        String schemaPath = "tests/idc/idc_6.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint20() throws SAXException, IOException {
        String xmlFile = "tests/idc/idc_6_invalid_1.xml";
        String schemaPath = "tests/idc/idc_6.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.2.2: Duplicate key value [7] found for identity constraint \"key_1\" of element \"X\"");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint21() throws SAXException, IOException {
        String xmlFile = "tests/idc/valid_1_1515.xml";
        String schemaPath = "tests/idc/jira_1515.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint22() throws SAXException, IOException {
        String xmlFile = "tests/idc/valid_2_1515.xml";
        String schemaPath = "tests/idc/jira_1515.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint23() throws SAXException, IOException {
        String xmlFile = "tests/idc/invalid_1_1515.xml";
        String schemaPath = "tests/idc/jira_1515.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("Identity Constraint error: the keyref identity constraint \"keyref\" refers to a key or unique that is out of scope");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint24() throws SAXException, IOException {
        String xmlFile = "tests/idc/jira_1585.xml";
        String schemaPath = "tests/idc/jira_1585.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint25() throws SAXException, IOException {
        String xmlFile = "tests/idc/jira_1594.xml";
        String schemaPath = "tests/idc/jira_1594.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'keyref' with value '1' not found for identity constraint of element 'personnel'");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }
    
    public void testIDConstraint26() throws SAXException, IOException {
        String xmlFile = "tests/idc/jira_1714_valid.xml";
        String schemaPath = "tests/idc/jira_1714.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertNull(fErrSysId);
        assertNull(fFatErrSysId);
    }
    
    public void testIDConstraint27() throws SAXException, IOException {
        String xmlFile = "tests/idc/jira_1714_invalid.xml";
        String schemaPath = "tests/idc/jira_1714.xsd";
        Schema schema = fSchemaFactory.newSchema(new StreamSource(schemaPath));
        Validator validator = schema.newValidator();
        validator.setErrorHandler(this);
        validator.validate(new StreamSource(xmlFile));
        assertEquals(1, failureList.size());
        // test expected error messages
        List expectedMessages = new ArrayList();
        FailureMesgFragments messageFragments = new FailureMesgFragments();
        messageFragments.setMessageFragment("cvc-identity-constraint.4.3: Key 'Collectiontype_defined' with value 'object1' not found for identity constraint of element 'Root'");
        expectedMessages.add(messageFragments);
        assertTrue(areErrorMessagesConsistent(expectedMessages));
    }

}
