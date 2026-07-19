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

package schema.config;

import java.lang.reflect.Field;

import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;

/**
 * @version $Id$
 */
public class SurrogatePairLengthTest extends BaseTest {

    private static final String PROPERTY = "org.apache.xerces.impl.dv.xs.useCodePointCountForStringLength";
    private static final String LENGTH_ERROR = "cvc-length-valid";
    private static final Field codePointCountField;
    
    static {
        try {
            Field f = TypeValidator.class.getDeclaredField("USE_CODE_POINT_COUNT_FOR_STRING_LENGTH");
            f.setAccessible(true);
            codePointCountField = f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Tests run sequentially within a shared JVM, so setUp/tearDown
    // can safely set and reset the flag before each test.
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty(PROPERTY, "true");
        codePointCountField.set(null, true);
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty(PROPERTY);
        codePointCountField.set(null, false);
        super.tearDown();
    }
    
    protected String getXMLDocument() {
        return "surrogate.xml";
    }
    
    protected String getSchemaFile() {
        return "surrogate.xsd";
    }
    
    protected String[] getRelevantErrorIDs() {
        return new String[] { LENGTH_ERROR };
    }
    
    public SurrogatePairLengthTest(String name) {
        super(name);
    }
    
    public void testSetTrue() throws Exception {
        validateDocument();
        checkValidResult();
    }
    
    private void checkValidResult() {
        assertNoError(LENGTH_ERROR);
        
        assertValidity(ItemPSVI.VALIDITY_VALID, fRootNode.getValidity());
        assertValidationAttempted(ItemPSVI.VALIDATION_FULL, fRootNode
                .getValidationAttempted());
        assertElementName("root", fRootNode.getElementDeclaration().getName());
        
        ElementPSVI child = super.getChild(1);
        assertValidity(ItemPSVI.VALIDITY_VALID, child.getValidity());
        assertValidationAttempted(ItemPSVI.VALIDATION_FULL, child
                .getValidationAttempted());
        assertElementName("e1", child.getElementDeclaration().getName());
        assertTypeName("length", child.getTypeDefinition().getName());
        
        child = super.getChild(2);
        assertValidity(ItemPSVI.VALIDITY_VALID, child.getValidity());
        assertValidationAttempted(ItemPSVI.VALIDATION_FULL, child
                .getValidationAttempted());
        assertElementName("e2", child.getElementDeclaration().getName());
        assertTypeName("length", child.getTypeDefinition().getName());
    }
}
