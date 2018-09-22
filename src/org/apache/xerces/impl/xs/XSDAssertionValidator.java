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

package org.apache.xerces.impl.xs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.assertion.XMLAssertHandler;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import org.apache.xerces.impl.xs.assertion.XSAssertConstants;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 * An XML Schema validator subcomponent handling assertions processing.
 * 
 * @xerces.internal
 * 
 * @author Mukul Gandhi IBM
 * 
 * @version $Id$
 */
public class XSDAssertionValidator {
    
    // the context XMLSchemaValidator instance
    XMLSchemaValidator fXmlSchemaValidator = null;
    
    // assertion processor object reference
    XMLAssertHandler fAssertionProcessor = null;
    
    // variable to track if attributes on current element, have assertions.
    boolean fAttributesHaveAsserts = false;
    
    
    /*
     * Class constructor.
     */
    public XSDAssertionValidator(XMLSchemaValidator xmlSchemaValidator) {
       this.fXmlSchemaValidator = xmlSchemaValidator; 
    }

    
    /*
     * Assertions processing interface during the XNI event, 'handleCharacters' in XMLSchemaValidator. 
     */
    public void characterDataHandler(XMLString text) {
        
        if (fAssertionProcessor != null) {
           fAssertionProcessor.characters(text);
        }
        
    } // characterDataHandler
    
    
    /*
     * Assertions processing interface during the XNI event, 'handleStartElement' in XMLSchemaValidator.
     */
    public void handleStartElement(QName element, XMLAttributes attributes) throws Exception {
       
        // get list of assertions for processing
        List assertionList = getAssertsForEvaluation(element, attributes);
        
        // instantiate the assertions processor
        if (assertionList != null && fAssertionProcessor == null) {
            // construct parameter values for the assertion processor
            NamespaceSupport xpathNamespaceContext = null;
            if (assertionList instanceof XSObjectList) {
                xpathNamespaceContext = ((XSAssertImpl)((XSObjectList) assertionList).item(0)).getXPath2NamespaceContext();    
            }
            else {
                Vector assertVector = (Vector) assertionList;
                xpathNamespaceContext = ((XSAssertImpl)assertVector.get(0)).getXPath2NamespaceContext();
            }

            Map assertProcessorParams = new HashMap();
            assertProcessorParams.put(Constants.XPATH2_NAMESPACE_CONTEXT, xpathNamespaceContext);
            // initialize the assertions processor
            initializeAssertProcessor(assertProcessorParams);
        }
       
        // invoke the assertions processor method
        if (fAssertionProcessor != null) {
            // construct the augmentations object, for assertions
            AugmentationsImpl assertAugs = new AugmentationsImpl();
            assertAugs.putItem(XSAssertConstants.assertList, assertionList);
            assertAugs.putItem(XSAssertConstants.isAttrHaveAsserts, Boolean.valueOf(fAttributesHaveAsserts));
            fAttributesHaveAsserts = false;
            fAssertionProcessor.startElement(element, attributes, assertAugs);
        }
        
    } // handleStartElement
    
    
    /*
     * Assertions processing interface during the XNI event, 'handleEndElement' in XMLSchemaValidator.
     */
    public void handleEndElement(QName element, Augmentations augs) {
        
        if (fAssertionProcessor != null) {
            try {
                fAssertionProcessor.endElement(element, augs);
            } catch (Exception ex) {
                throw new XNIException(ex.getMessage(), ex);
            }
        }
          
    } // handleEndElement
    
    
    /*
     * Assertions processing interface during the XNI event, 'comment' in XMLSchemaValidator. 
     */
    public void comment(XMLString text) {
        if (fAssertionProcessor != null) {
            fAssertionProcessor.comment(text);  
        }
    } // comment
    
    
    /*
     * Assertions processing interface during the XNI event, 'processingInstruction' in XMLSchemaValidator. 
     */
    public void processingInstruction(String target, XMLString data) {
        if (fAssertionProcessor != null) {
            fAssertionProcessor.processingInstruction(target, data);  
        }
    } // processingInstruction
    
    
    /*
     * Accumulate a list of assertions (get from the underlying XSModel instance) to be processed
     * for the current context. Return the assertions list.
     */
    private List getAssertsForEvaluation(QName element, XMLAttributes attributes) {
       
       List assertionList = null;
       
       XSTypeDefinition typeDefn = fXmlSchemaValidator.fCurrentPSVI.getTypeDefinition();       
       if (typeDefn != null) {
           if (typeDefn.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
               // if element's schema type is a "complex type"               
               XSObjectListImpl complexTypeAsserts = getAssertsFromComplexType((XSComplexTypeDefinition) typeDefn, attributes);
               if (complexTypeAsserts.size() > 0) {
                   assertionList = complexTypeAsserts;             
               }
           }
           else {
               // if element's schema type is a "simple type"
               assertionList = getAssertsFromSimpleType((XSSimpleTypeDefinition) typeDefn);            
           }
       }
               
       return assertionList;
         
    } // getAssertsForEvaluation


    /*
     * Accumulate assertions from a complex type.
     */
    private XSObjectListImpl getAssertsFromComplexType(XSComplexTypeDefinition complexTypeDef, XMLAttributes attributes) {
        
        XSObjectListImpl complexTypeAsserts = new XSObjectListImpl();

        XSObjectList primaryAssertions = complexTypeDef.getAssertions(); // assertions stored in the traverser layer

        if (primaryAssertions.getLength() > 0) {
            for (int assertIdx = 0; assertIdx < primaryAssertions.getLength(); assertIdx++) {
                complexTypeAsserts.addXSObject((XSAssert) primaryAssertions.get(assertIdx));
            }
        }

        // add assertion facets from complexType -> simpleContent. few assertions are already available in complexType model object,
        // and few others are retrieved here.
        XSSimpleTypeDefinition simpleContentType = complexTypeDef.getSimpleType();
        if (simpleContentType != null) {                    
            if (complexTypeDef.getDerivationMethod() == XSConstants.DERIVATION_RESTRICTION) {
                // add assertions for complexType -> simpleContent -> restriction cases 
                Vector simpleContentAsserts = XS11TypeHelper.getAssertsFromSimpleType(simpleContentType);
                for (int assertIdx = 0; assertIdx < simpleContentAsserts.size(); assertIdx++) {
                    complexTypeAsserts.addXSObject((XSAssert) simpleContentAsserts.get(assertIdx));
                }
            }
            else if (XS11TypeHelper.isComplexTypeDerivedFromSTList(complexTypeDef, XSConstants.DERIVATION_EXTENSION)) {
                // add assertions from the list->itemType of base schema simple type
                Vector baseItemTypeAsserts = XS11TypeHelper.getAssertsFromSimpleType(((XSSimpleTypeDefinition)complexTypeDef.getBaseType()).getItemType());
                for (int assertIdx = 0; assertIdx < baseItemTypeAsserts.size(); assertIdx++) {
                    complexTypeAsserts.addXSObject((XSAssert) baseItemTypeAsserts.get(assertIdx)); 
                }
            }
        }
        
        // find assertions from attributes of a complex type, and add them to the parent assertions list
        XSObjectListImpl attrAsserts = getAssertsFromAttributes(attributes);
        int attrAssertCount = attrAsserts.getLength(); 
        for (int attrAssertIdx = 0; attrAssertIdx < attrAssertCount; attrAssertIdx++) {
            complexTypeAsserts.addXSObject(attrAsserts.item(attrAssertIdx)); 
        }
        if (attrAssertCount > 0) {
           fAttributesHaveAsserts = true;  
        }

        return complexTypeAsserts;
            
    } // getAssertsFromComplexType


    /*
     * Get assertions from attributes of a complex type.
     */
    private XSObjectListImpl getAssertsFromAttributes(XMLAttributes attributes) {
        
        XSObjectListImpl attrAssertList = new XSObjectListImpl();
        
        List xsTypeList = new ArrayList();        
        for (int attrIndx = 0; attrIndx < attributes.getLength(); attrIndx++) {
            Augmentations attrAugs = attributes.getAugmentations(attrIndx);
            AttributePSVImpl attrPSVI = (AttributePSVImpl) attrAugs.getItem(Constants.ATTRIBUTE_PSVI);
            XSSimpleTypeDefinition attrType = (XSSimpleTypeDefinition) attrPSVI.getTypeDefinition();                                   
            if (attrType != null && !XS11TypeHelper.isListContainsType(xsTypeList, attrType)) {
                // since different attributes may be validated by the same type, so we deduplicate the type list here
                // to only store unique assertions.
                xsTypeList.add(attrType); 
                List attrAsserts = getAssertsFromSimpleType(attrType);
                // copy assertions to the parent list
                if (attrAsserts != null) {
                   for (Iterator iter = attrAsserts.iterator(); iter.hasNext();) {
                       attrAssertList.addXSObject((XSAssertImpl)iter.next());
                   }
                }                            
            }           
        }
        
        return attrAssertList;
        
    } // getAssertsFromAttributes
    
    
    /*
     * Get assertions from a simpleType.
     */
    public List getAssertsFromSimpleType(XSSimpleTypeDefinition simpleTypeDef) {
        
        List simpleTypeAsserts = null;
                   
        XSObjectListImpl facetList = (XSObjectListImpl) simpleTypeDef.getMultiValueFacets();
        if (facetList.getLength() == 0 && simpleTypeDef.getItemType() != null) {
            // facets for "simpleType -> list"
            facetList = (XSObjectListImpl) simpleTypeDef.getItemType().getMultiValueFacets();    
        }
        else if (simpleTypeDef.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
            // special handling for assertions on "simpleType -> union" cases. Adding an assertion here, for determining the NamespaceContext.
            XSAssertImpl assertImpl = getFirstAssertFromUnionMemberTypes(simpleTypeDef.getMemberTypes());
            if (assertImpl != null) {
                simpleTypeAsserts = new Vector();
                simpleTypeAsserts.add(assertImpl);
            }
        }

        // iterate all the schema facets having the simpleType variety "atomic | list", and accumulate assertions from them
        for (int facetIdx = 0; facetIdx < facetList.getLength(); facetIdx++) {
            XSMultiValueFacet facet = (XSMultiValueFacet) facetList.item(facetIdx);
            if (facet.getFacetKind() == XSSimpleTypeDefinition.FACET_ASSERT) {
                if (simpleTypeAsserts == null) {
                    simpleTypeAsserts = new Vector();   
                }                  
                simpleTypeAsserts.addAll(facet.getAsserts());                  
            }
        }

        return simpleTypeAsserts;
        
    } // getAssertsFromSimpleType
    
    
    /*
     * Get the 1st assertion from the member types of simpleType having variety union. Needed to get an schema
     * "namespace context" which is available for example, in the 1st assertion in the assertions list.
     */
    private XSAssertImpl getFirstAssertFromUnionMemberTypes(XSObjectList unionMemberTypes) {
        
         XSAssertImpl assertImpl = null;
        
         for (int memberTypeIdx = 0; memberTypeIdx < unionMemberTypes.getLength(); memberTypeIdx++) {
             XSSimpleTypeDefinition unionMemberType = (XSSimpleTypeDefinition) unionMemberTypes.item(memberTypeIdx);
             if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(unionMemberType.getNamespace())) {
                Vector memberTypeAsserts = XS11TypeHelper.getAssertsFromSimpleType(unionMemberType);                
                if (!memberTypeAsserts.isEmpty()) {
                   assertImpl = (XSAssertImpl) memberTypeAsserts.get(0);
                   break;
                }                
             }
         }
        
         return assertImpl;

    } // getFirstAssertFromUnionMemberTypes
    
    
    /*
     * Method to initialize the assertions processor.
     */
    private void initializeAssertProcessor(Map assertParams) {
             
        String assertProcessorProp;
        try {
            assertProcessorProp = SecuritySupport.getSystemProperty("org.apache.xerces.assertProcessor");
        }
        catch (SecurityException se) {
            assertProcessorProp = null;
        }
        
        if (assertProcessorProp == null || assertProcessorProp.length() == 0) {
            // if assertion processor is not specified via a system property, initialize it to the "PsychoPath XPath 2.0" processor.
            fAssertionProcessor = new XMLAssertPsychopathXPath2Impl(assertParams);
        } 
        else {
            try {
                ClassLoader cl = ObjectFactory.findClassLoader();
                Class assertClass = ObjectFactory.findProviderClass(assertProcessorProp, cl, true);
                fAssertionProcessor = (XMLAssertHandler) assertClass.newInstance();
            } 
            catch (ClassNotFoundException ex) {
                throw new XNIException(ex.getMessage(), ex);
            } 
            catch (InstantiationException ex) {
                throw new XNIException(ex.getMessage(), ex);
            } 
            catch (IllegalAccessException ex) {
                throw new XNIException(ex.getMessage(), ex);
            }
        }
        
        fAssertionProcessor.setProperty("http://apache.org/xml/properties/assert/validator", fXmlSchemaValidator);
        
    } // initializeAssertProcessor
    
    /*
     * Extra checks for assertion evaluations for simpleType definitions with variety union, for attributes of one element.
     */
    void extraCheckForSTUnionAssertsAttrs(XMLAttributes attributes) {
        
        XMLAttributes attrsImpl = (XMLAttributesImpl)attributes;
        
        for (int attrIdx = 0; attrIdx < attrsImpl.getLength(); attrIdx++) {
            Augmentations attrAugs = attrsImpl.getAugmentations(attrIdx);
            AttributePSVImpl attrPsvi = (AttributePSVImpl)attrAugs.getItem(Constants.ATTRIBUTE_PSVI);            
            XSSimpleTypeDefinition attrSimpleType = (XSSimpleTypeDefinition) attrPsvi.getTypeDefinition();
            List isAssertProcessingNeededForSTUnionAttrs = fXmlSchemaValidator.getIsAssertProcessingNeededForSTUnionAttrs();
            if (attrSimpleType != null && attrSimpleType.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION && ((XSSimpleType) attrSimpleType.getBaseType()).getVariety() != XSSimpleTypeDefinition.VARIETY_UNION) {
                if (XS11TypeHelper.isAtomicStrValueValidForSTUnion(attrSimpleType.getMemberTypes(), attrsImpl.getValue(attrIdx), attrPsvi.fValue, Constants.SCHEMA_VERSION_1_1)) {
                    isAssertProcessingNeededForSTUnionAttrs.add(Boolean.valueOf(false));  
                }
                else {
                    isAssertProcessingNeededForSTUnionAttrs.add(Boolean.valueOf(true)); 
                }
            }
            else {
                isAssertProcessingNeededForSTUnionAttrs.add(Boolean.valueOf(true)); 
            }
        }
        
    }  // extraCheckForSTUnionAssertsAttrs
    
    /*
     * Extra checks for assertion evaluations for simpleType definitions with variety union, for an element.
     */
    void extraCheckForSTUnionAssertsElem(XSSimpleType simpleTypeDv, String content, ValidatedInfo validatedInfo) {
        if (simpleTypeDv.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION && ((XSSimpleType) simpleTypeDv.getBaseType()).getVariety() != XSSimpleTypeDefinition.VARIETY_UNION) {
            if (XS11TypeHelper.isAtomicStrValueValidForSTUnion(simpleTypeDv.getMemberTypes(), content, validatedInfo, Constants.SCHEMA_VERSION_1_1)) {
                fXmlSchemaValidator.setIsAssertProcessingNeededForSTUnionElem(false);
            }
        }
    } // extraCheckForSTUnionAssertsElem
    
} // class XSDAssertionValidator
