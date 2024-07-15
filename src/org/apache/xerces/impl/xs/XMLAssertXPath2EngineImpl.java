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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.assertion.XMLAssertAdapter;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import org.apache.xerces.impl.xs.assertion.XSAssertConstants;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A class implementing an XPath interface for XML Schema 1.1 "assertions" evaluation. This class interfaces with 
 * the "XPath 2.0" engine (https://www.eclipse.org/webtools) for XPath expression evaluations for XSD assertions.
 * 
 * An instance of this class constructs Xerces PSVI enabled DOM trees (which are in-memory XDM representation for 
 * XPath 2.0 engine) from XNI event calls. XSD assertions are evaluated on these PSVI XDM instances in a bottom 
 * up fashion.
 * 
 * @xerces.internal
 * 
 * @author Mukul Gandhi, IBM
 * @author Ken Cai, IBM
 * 
 * @version $Id$
 */
public class XMLAssertXPath2EngineImpl extends XMLAssertAdapter {

    // class fields declarations

    // XSModel instance representing the schema information needed by XPath 2.0 engine 
    private XSModel fSchemaXSmodel = null;
    
    // XPath 2.0 dynamic context reference
    private DynamicContext fXpath2DynamicContext;
    
    // reference to the Eclipse XPath evaluator
    private AbstractXPath2EngineImpl fAbstrXpathEngineImpl = null;
    
    // the DOM root of assertions tree
    private Document fAssertDocument = null;

    // an DOM element object to track construction of assertion DOM tree. Value of this object changes as per the XNI document events.
    private Element fCurrentAssertDomNode = null;

    // a stack holding the DOM roots for assertions evaluation
    private Stack fAssertRootStack = null;

    // a stack parallel to 'fAssertRootStack' storing all assertions for a single XDM tree
    private Stack fAssertListStack = null;

    // XMLSchemaValidator reference. set from the XMLSchemaValidator object itself.
    private XMLSchemaValidatorImpl fXmlSchemaValidator = null;
    
    // parameters to pass to XPath engine (for e.g, the XML namespace bindings)
    private Map fAssertParams = null;
    
    // failed assertions for an "element information item"
    private XSAssert[] fFailedAssertions = null;
    
    // state to save the <assert> root type information, of the current assertion evaluation 
    private XSTypeDefinition fAssertRootTypeDef = null;
    
    // for some cases, consecutive calls to method 'characters(XMLString text)' of this 
    // class may be received. we need to, concatenate the character buffers of such consecutive 
    // method calls, and create only one XDM text node for these consecutive calls. the 
    // following two variables handle that.
    private boolean fIsTextNodeCall = false;
    
    private StringBuffer fTextNodeStrBuff = new StringBuffer();
    
    
    /*
     * Class constructor.
     */
    public XMLAssertXPath2EngineImpl(Map assertParams) {        
        // initializing the class variables
        this.fAssertDocument = new PSVIDocumentImpl();        
        this.fAssertRootStack = new Stack();
        this.fAssertListStack = new Stack();
        this.fAssertParams = assertParams;
    }
    

    /*
     * Initialize the Eclipse XPath processor.
     */
    public void initXPathProcessor() throws Exception {        
        fXmlSchemaValidator = (XMLSchemaValidatorImpl) getProperty("http://apache.org/xml/properties/assert/validator");        
        fAbstrXpathEngineImpl = new AbstractXPath2EngineImpl();
        fXpath2DynamicContext = fAbstrXpathEngineImpl.initXPath2DynamicContext(fSchemaXSmodel, fAssertDocument, fAssertParams);        
    } // initXPathProcessor
    

    /*
     * (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLAssertAdapter#startElement
     *      (org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, 
     *       org.apache.xerces.xni.Augmentations)
     */
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws Exception {
        
        if (fIsTextNodeCall) {
            if (fCurrentAssertDomNode != null) {                
                // add a child text node to the assertions, DOM tree
                fCurrentAssertDomNode.appendChild(fAssertDocument.createTextNode(fTextNodeStrBuff.toString()));                
            }
            fIsTextNodeCall = false;
            fTextNodeStrBuff = new StringBuffer();
        }
        
        if (fCurrentAssertDomNode == null) {
            fCurrentAssertDomNode = new PSVIElementNSImpl((CoreDocumentImpl) fAssertDocument, element.uri, element.rawname);
            Object xmlBaseUri = fAssertParams.get(Constants.XML_BASE_URI);
            if (xmlBaseUri != null) {
               PSVIAttrNSImpl attrNode = new PSVIAttrNSImpl((PSVIDocumentImpl)fAssertDocument, "http://www.w3.org/XML/1998/namespace", "xml:base", "base");
               attrNode.setNodeValue((fAssertParams.get(Constants.XML_BASE_URI)).toString());
               fCurrentAssertDomNode.setAttributeNode(attrNode);
            }
            fAssertDocument.appendChild(fCurrentAssertDomNode);
        } else {
            Element elem = new PSVIElementNSImpl((CoreDocumentImpl) fAssertDocument, element.uri, element.rawname);
            fCurrentAssertDomNode.appendChild(elem);
            fCurrentAssertDomNode = elem;
        }

        // add attribute nodes to DOM element node
        for (int attIndex = 0; attIndex < attributes.getLength(); attIndex++) {
            PSVIAttrNSImpl attrNode = new PSVIAttrNSImpl((PSVIDocumentImpl)fAssertDocument, attributes.getURI(attIndex), attributes.getQName(attIndex), attributes.getLocalName(attIndex));
            attrNode.setNodeValue(attributes.getValue(attIndex));
            // set PSVI information for the attribute
            AttributePSVImpl attrPSVI = (AttributePSVImpl) (attributes.getAugmentations(attIndex)).getItem(Constants.ATTRIBUTE_PSVI);
            if (attrPSVI != null) {
               attrNode.setPSVI(attrPSVI);
            }
            fCurrentAssertDomNode.setAttributeNode(attrNode);
        }                

        if (augs != null) {
            // if we have assertions applicable to this element, store the element reference and the assertions 
            // on it on the runtime stacks
            List assertionList = (List) augs.getItem(XSAssertConstants.assertList);
            if (assertionList != null) {
                fAssertRootStack.push(fCurrentAssertDomNode);
                fAssertListStack.push(assertionList);
                initXPathProcessor();
            }
    
            // evaluate assertions from attributes. evaluation of assertions on attributes in startElement call, 
            // helps us setting the PSVI results for attributes appropriately.
            if (((Boolean)augs.getItem(XSAssertConstants.isAttrHaveAsserts)).booleanValue()) {
                evaluateAssertsFromAttributes(element, attributes);
            }
        }
        else {
            // handling, assertions on attribute simpleType definition, with variety list whose itemType is union
            for (int attrIdx = 0; attrIdx < attributes.getLength(); attrIdx++) {
                QName attrQname = new QName();
                attributes.getName(attrIdx, attrQname);            
                String attrValue = attributes.getValue(attrIdx);
                Augmentations attrAugs = attributes.getAugmentations(attrIdx);
                AttributePSVImpl attrPsvi = (AttributePSVImpl)attrAugs.getItem(Constants.ATTRIBUTE_PSVI);
                if (attrPsvi != null) {
                    XSSimpleTypeDefinition attrSimpleType = (XSSimpleTypeDefinition) attrPsvi.getTypeDefinition();
                    if (attrSimpleType != null) {                                           
                       if (attrSimpleType.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
                          XSSimpleTypeDefinition xsItemTypeDefn = attrSimpleType.getItemType();
                          if (xsItemTypeDefn.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
                             XSObjectList memberTypes = xsItemTypeDefn.getMemberTypes();
                             int unionTypeNoOfMembers = memberTypes.getLength(); 
                             int noOfUnionAtomicTypes = 0;
                             List memberTypeList = new Vector();
                             for (int idx = 0; idx < memberTypes.getLength(); idx++) {                           
                                XSSimpleTypeDefinition memberType = (XSSimpleTypeDefinition)memberTypes.get(idx);
                                if (memberType.getVariety() == XSSimpleTypeDefinition.VARIETY_ATOMIC) {
                                   noOfUnionAtomicTypes++;
                                   XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)memberType;
                                   memberTypeList.add(xsSimpleTypeDecl);
                                }
                             }
                             if (unionTypeNoOfMembers == noOfUnionAtomicTypes) {
                                 // tokenize the list value by a sequence of white spaces
                                 StringTokenizer listStrTokens = new StringTokenizer(attrValue, " \n\t\r");
                                 while (listStrTokens.hasMoreTokens()) {
                                    String listItemStrValue = listStrTokens.nextToken();
                                    // iterate over all the member types of union
                                    boolean isValidationForListItemSuccessful = false;
                                    for (int idx = 0; idx < memberTypeList.size(); idx++) {
                                        XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)memberTypeList.get(idx);                  
                                        if (XS11TypeHelper.isStrValueValidForASimpleType(listItemStrValue, xsSimpleTypeDecl, Constants.SCHEMA_VERSION_1_1)) {
                                            Vector assertVector = xsSimpleTypeDecl.getAssertions();
                                            if (assertVector != null) {
                                               int noOfAsserts = assertVector.size();
                                               int noOfAssertSuccesses = 0;
                                               for (int idx1 = 0; idx1 < assertVector.size(); idx1++) {
                                                   fAssertParams.put(Constants.XPATH2_NAMESPACE_CONTEXT, ((XSAssertImpl)assertVector.get(idx1)).getXPath2NamespaceContext());
                                                   initXPathProcessor();
                                                   setXDMTypedValueOf$valueForSTVarietyAtomic(listItemStrValue, getXercesXSDTypeCodeFor$value((XSTypeDefinition)xsSimpleTypeDecl), 
                                                                                                                                 fXpath2DynamicContext);                                               
                                                   AssertionError assertError = evaluateOneAssertion(element, (XSAssertImpl)assertVector.get(idx1), listItemStrValue, false, true);
                                                   if (assertError == null) {
                                                      noOfAssertSuccesses++;
                                                   }
                                               }
                                               if (noOfAsserts == noOfAssertSuccesses) {
                                                  isValidationForListItemSuccessful = true;
                                                  break;
                                               }
                                            }
                                            else {
                                               isValidationForListItemSuccessful = true;  
                                            }
                                        }
                                        
                                        if (isValidationForListItemSuccessful) {                      
                                           break;
                                        }
                                    }
                                    
                                    if (!isValidationForListItemSuccessful) {                                     
                                        fXmlSchemaValidator.reportSchemaError("cvc-datatype-valid_attr.4.1.4", new Object[] {attrValue, element.rawname, attrQname.rawname, 
                                                                                                                             listItemStrValue, XS11TypeHelper.getSchemaTypeName(
                                                                                                                             (XSTypeDefinition) attrSimpleType.getItemType())}); 
                                    }
                                 } 
                             }
                          }
                       }                                        
                    }
                }
            }
        }
        
    } // startElement
    
    
    /*
     * Evaluate assertions from attributes.
     */
    private void evaluateAssertsFromAttributes(QName element, XMLAttributes attributes) throws Exception {
        
        for (int attrIdx = 0; attrIdx < attributes.getLength(); attrIdx++) {
            QName attrQname = new QName();
            attributes.getName(attrIdx, attrQname);            
            String attrValue = attributes.getValue(attrIdx);
            Augmentations attrAugs = attributes.getAugmentations(attrIdx);
            AttributePSVImpl attrPsvi = (AttributePSVImpl)attrAugs.getItem(Constants.ATTRIBUTE_PSVI);
            XSSimpleTypeDefinition attrSimpleType = (XSSimpleTypeDefinition) attrPsvi.getTypeDefinition();
            if (attrSimpleType != null) {
                List attrAssertList = fXmlSchemaValidator.getAssertionValidator().getAssertsFromSimpleType(attrSimpleType);
                if (attrAssertList != null) {
                    boolean isTypeDerivedFromList = isTypeDerivedFromSTList(attrSimpleType);
                    boolean isTypeDerivedFromUnion = isTypeDerivedFromSTUnion(attrSimpleType);                
                    for (int assertIdx = 0; assertIdx < attrAssertList.size(); assertIdx++) {
                        XSAssertImpl assertImpl = (XSAssertImpl)attrAssertList.get(assertIdx);
                        assertImpl.setAttrName(attrQname.localpart);
                        evaluateOneAssertionFromSimpleType(element, attrValue, attrAugs, attrSimpleType, isTypeDerivedFromList, 
                                                           isTypeDerivedFromUnion, assertImpl, true, attrQname, false);
                        // evaluate assertions on itemType of xs:list
                        XSSimpleTypeDefinition attrItemType = attrSimpleType.getItemType();
                        if (isTypeDerivedFromList && attrItemType != null) {
                            evaluateAssertsFromItemTypeOfSTList(element, attrItemType, attrValue, false);
                        }
                    }                
                }
            }
        }
        
    } // evaluateAssertsFromAttributes
    
    
    /*
     * (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLAssertAdapter#endElement(org.apache.xerces.xni.QName, 
     *      org.apache.xerces.xni.Augmentations)
     */
    public void endElement(QName element, Augmentations augs) throws Exception {
        
        if (fCurrentAssertDomNode != null) {
            if (fIsTextNodeCall) {                                
               // add a child text node to the assertions, DOM tree
               fCurrentAssertDomNode.appendChild(fAssertDocument.createTextNode(fTextNodeStrBuff.toString()));
               fIsTextNodeCall = false;
               fTextNodeStrBuff = new StringBuffer();                
            }
            
            // set PSVI information on the element
            ElementPSVI elemPSVI = (ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI);
            ((PSVIElementNSImpl) fCurrentAssertDomNode).setPSVI(elemPSVI);
            
            // handling default values of elements (adding them as 'text' node within the assertion XDM tree)
            XSElementDecl elemDecl = (XSElementDecl) elemPSVI.getElementDeclaration();
            if (elemDecl != null && elemDecl.fDefault != null && !fCurrentAssertDomNode.hasChildNodes()) {
                fCurrentAssertDomNode.appendChild(fAssertDocument.createTextNode(elemDecl.fDefault.normalizedValue));
            }               
            
            if (!fAssertRootStack.empty() && (fCurrentAssertDomNode == fAssertRootStack.peek())) {               
                 // get XSModel instance                
                 fSchemaXSmodel =  ((PSVIElementNSImpl) fCurrentAssertDomNode).getSchemaInformation();                 
                 // pop the assertion root stack to go one level up
                 fAssertRootStack.pop();
                 // get assertions from the stack, and pass on to the assertions evaluator
                 processAllAssertionsOnElement(element, (List) fAssertListStack.pop(), augs);
                 // set value of [failed assertions] PSVI property
                 if (fFailedAssertions != null && elemPSVI != null) {
                     setFailedAssertionsPSVIResult(elemPSVI);
                     fFailedAssertions = null;
                 }                 
            }

            if (fCurrentAssertDomNode.getParentNode() instanceof Element) {
                fCurrentAssertDomNode = (Element) fCurrentAssertDomNode.getParentNode();
            }
        }
        
    } // endElement
    
    
    /* (non-Javadoc)
     * @see org.apache.xerces.impl.xs.assertion.XMLAssertAdapter#comment(org.apache.xerces.xni.XMLString)
     */
    public void comment(XMLString text) {        
        if (fCurrentAssertDomNode != null) {
            if (fIsTextNodeCall) {                                
                // add a child text node to the assertions, DOM tree
                fCurrentAssertDomNode.appendChild(fAssertDocument.createTextNode(fTextNodeStrBuff.toString()));
                fIsTextNodeCall = false;
                fTextNodeStrBuff = new StringBuffer();                
            }
            
            // add a comment node to the assertions, DOM tree
            fCurrentAssertDomNode.appendChild(fAssertDocument.createComment(new String(text.ch, text.offset, text.length)));
        } 
    } // comment
    
    
    /* (non-Javadoc)
     * @see org.apache.xerces.impl.xs.assertion.XMLAssertAdapter#processingInstruction(java.lang.String, org.apache.xerces.xni.XMLString)
     */
    public void processingInstruction(String target, XMLString data) {        
        if (fCurrentAssertDomNode != null) {
            if (fIsTextNodeCall) {                                
               // add a child text node to the assertions, DOM tree
               fCurrentAssertDomNode.appendChild(fAssertDocument.createTextNode(fTextNodeStrBuff.toString()));
               fIsTextNodeCall = false;
               fTextNodeStrBuff = new StringBuffer();                
            }
            
            // add a PI node to the assertions, DOM tree
            fCurrentAssertDomNode.appendChild(fAssertDocument.createProcessingInstruction(target, new String(data.ch, data.offset, data.length)));
        } 
    } // processingInstruction
    
    
    /*
     * set value of [failed assertions] PSVI property.
     */
    private void setFailedAssertionsPSVIResult(ElementPSVI elemPSVI) {
        XSAssert[] tempArray = new XSAssert[fFailedAssertions.length];
        System.arraycopy(fFailedAssertions, 0, tempArray, 0, fFailedAssertions.length);
        ((ElementPSVImpl)elemPSVI).fFailedAssertions = new ObjectListImpl(tempArray, tempArray.length);
    } // setFailedAssertionsPSVIResult
    

    /*
     * Method to evaluate all relevant XML Schema 1.1 assertions, for an XML document instance 
     * element tree.
     */
    private void processAllAssertionsOnElement(QName element, List assertions, Augmentations augs) throws Exception {
        
        // initialize the XPath engine
        initXPathProcessor();

        // determine "string value" of XPath2 context variable $value
        ElementPSVI elemPsvi = (ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI);
        String value = getStringValueOf$value(fCurrentAssertDomNode, elemPsvi);

        // evaluate assertions
        if (assertions instanceof XSObjectList) {
            // assertions from a "complex type" definition
            ElementPSVImpl modifiedRootNodePsvi = savePsviInfoWithUntypingOfAssertRoot(elemPsvi, true);
            evaluateAssertionsFromComplexType(element, assertions, value, augs);
            restorePsviInfoForXPathContext(modifiedRootNodePsvi);
        }
        else if (assertions instanceof Vector) {            
            // assertions from a "simple type" definition
            evaluateAssertionsFromSimpleType(element, assertions, value, augs);            
        }
         
    } // processAllAssertionsOnElement
    

    /*
     * Save PSVI information of <assert> root node, and possibly make root node of <assert> in an untyped state.
     */
    private ElementPSVImpl savePsviInfoWithUntypingOfAssertRoot(ElementPSVI elemPsvi, boolean isSetXsAny) {
        ElementPSVImpl assertRootPsvi = new ElementPSVImpl(true, elemPsvi);
        fAssertRootTypeDef = assertRootPsvi.getTypeDefinition();
        if (isSetXsAny) {           
           assertRootPsvi.fTypeDecl = new SchemaGrammar.XSAnyType();
           ((PSVIElementNSImpl)fCurrentAssertDomNode).setPSVI(assertRootPsvi); 
        }        
        return assertRootPsvi; 
    } // savePsviInfoWithUntypingOfAssertRoot
    
    
    /*
     * Restore PSVI information for the XPath evaluation context.
     */
    private void restorePsviInfoForXPathContext(ElementPSVI elemPsvi) {
        ElementPSVImpl assertRootPsvi = (ElementPSVImpl)elemPsvi;
        if (fAssertRootTypeDef != null) {
           assertRootPsvi.fTypeDecl = fAssertRootTypeDef;
           ((PSVIElementNSImpl)fCurrentAssertDomNode).setPSVI(assertRootPsvi);
        }         
    } // restorePsviInfoForXPathContext

    
    /*
     * Evaluate assertions from a "simple type" definition, on elements.
     */
    private void evaluateAssertionsFromSimpleType(QName element, List assertions, String value, Augmentations augs) throws Exception {  

        if (assertions.get(0) instanceof XSSimpleTypeDecl) {
           // handling, assertions on, simpleType with variety list whose itemType is union

           // tokenize the list value by a sequence of white spaces
           StringTokenizer listStrTokens = new StringTokenizer(value, " \n\t\r");
           while (listStrTokens.hasMoreTokens()) {
              String listItemStrValue = listStrTokens.nextToken();
              // iterate over all the member types of union
              boolean isValidationForListItemSuccessful = false;
              for (int idx = 0; idx < assertions.size(); idx++) {
                  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)assertions.get(idx);                  
                  if (XS11TypeHelper.isStrValueValidForASimpleType(listItemStrValue, xsSimpleTypeDecl, Constants.SCHEMA_VERSION_1_1)) {
                      Vector assertVector = xsSimpleTypeDecl.getAssertions();
                      if (assertVector != null) {
                         int noOfAsserts = assertVector.size();
                         int noOfAssertSuccesses = 0;
                         for (int idx1 = 0; idx1 < assertVector.size(); idx1++) {
                             setXDMTypedValueOf$valueForSTVarietyAtomic(listItemStrValue, getXercesXSDTypeCodeFor$value((XSTypeDefinition)xsSimpleTypeDecl), 
                                                                                                           fXpath2DynamicContext);
                             AssertionError assertError = evaluateOneAssertion(element, (XSAssertImpl)assertVector.get(idx1), listItemStrValue, false, true);
                             if (assertError == null) {
                                noOfAssertSuccesses++;
                             }
                         }
                         if (noOfAsserts == noOfAssertSuccesses) {
                            isValidationForListItemSuccessful = true;
                            break;
                         }
                      }
                      else {
                         isValidationForListItemSuccessful = true;  
                      }
                  }
                  
                  if (isValidationForListItemSuccessful) {                      
                     break;
                  }
              }
              
              if (!isValidationForListItemSuccessful) {
                  XSSimpleTypeDefinition simpleTypeDefn = (XSSimpleTypeDefinition)((ElementPSVI) 
                                                                                     augs.getItem(Constants.ELEMENT_PSVI)).getTypeDefinition(); 
                  fXmlSchemaValidator.reportSchemaError("cvc-datatype-valid.4.1.4", new Object[] {value, element.rawname, 
                                                                                          listItemStrValue, XS11TypeHelper.getSchemaTypeName(
                                                                                          (XSTypeDefinition) simpleTypeDefn.getItemType())}); 
              }
           }
        }
        else {
            XSSimpleTypeDefinition simpleTypeDefn = (XSSimpleTypeDefinition) ((ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI)).getTypeDefinition();
            boolean isTypeDerivedFromList = isTypeDerivedFromSTList(simpleTypeDefn);
            boolean isTypeDerivedFromUnion = isTypeDerivedFromSTUnion(simpleTypeDefn);
            
            Vector assertList = (Vector) assertions;
            for (int assertIdx = 0; assertIdx < assertList.size(); assertIdx++) {
                XSAssertImpl assertImpl = (XSAssertImpl) assertList.get(assertIdx);
                String xPathDefaultNamespace = assertImpl.getXPathDefaultNamespace(); 
                if (xPathDefaultNamespace != null) {
                    fXpath2DynamicContext.add_namespace(null, xPathDefaultNamespace);  
                }
                evaluateOneAssertionFromSimpleType(element, value, augs, simpleTypeDefn, isTypeDerivedFromList, isTypeDerivedFromUnion, assertImpl, false, null, false);
            }
            
            // evaluate assertions on itemType of xs:list
            if (isTypeDerivedFromList && simpleTypeDefn.getItemType() != null) {
                evaluateAssertsFromItemTypeOfSTList(element, simpleTypeDefn.getItemType(), value, false); 
            }
        }
        
    } // evaluateAssertionsFromSimpleType
    
    
    /*
     * Evaluate one assertion instance for a simpleType (this assertion could be from an attribute, simpleType on element or a complexType with simple content).
     */
    public Boolean evaluateOneAssertionFromSimpleType(QName element, String value, Augmentations augs, XSSimpleTypeDefinition simpleTypeDefn, boolean isTypeDerivedFromList, boolean isTypeDerivedFromUnion,
                                                      XSAssertImpl assertImpl, boolean isAttribute, QName attrQname, boolean isAssertEvaluationFromSchema) throws Exception {
        
        Boolean isAssertSucceeded = Boolean.TRUE; 
        
        if (simpleTypeDefn.getVariety() == XSSimpleTypeDefinition.VARIETY_ATOMIC) {
            // evaluating assertions for "simpleType -> restriction" (not derived by union)
            if (isAttribute) {
                setXDMTypedValueOf$value(fCurrentAssertDomNode, value, null, simpleTypeDefn, false, fXpath2DynamicContext);
            }
            else {
                setXDMTypedValueOf$value(fCurrentAssertDomNode, value, null, null, false, fXpath2DynamicContext);
            }
            AssertionError assertError = evaluateOneAssertion(element, assertImpl, value, false, false);
            if (assertError != null) {
                if (isAssertEvaluationFromSchema) {
                   isAssertSucceeded = Boolean.FALSE;  
                }
                else {
                   reportAssertionsError(assertError); 
                }
            }                         
        }
        else if (simpleTypeDefn.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
            // evaluating assertions for "simpleType -> list"                    
            isAssertSucceeded = evaluateAssertionOnSTListValue(element, value, assertImpl, false, simpleTypeDefn.getItemType(), isTypeDerivedFromList, isAssertEvaluationFromSchema); 
        }
        else if (!isAttribute && ((Boolean)augs.getItem(XSAssertConstants.isAssertProcNeededForUnionElem)).booleanValue()) {
            // evaluating assertions for "simpleType -> union" for an element             
            if (!evaluateAssertionOnSTUnion(element, simpleTypeDefn, isTypeDerivedFromUnion, assertImpl, value, augs)) {
                if (isAssertEvaluationFromSchema) {
                   isAssertSucceeded = Boolean.FALSE;  
                }
                else {
                   fXmlSchemaValidator.reportSchemaError("cvc-type.3.1.3", new Object[] {element.rawname, value});
                }
            }
        }
        else if (isAttribute && ((Boolean)augs.getItem(XSAssertConstants.isAssertProcNeededForUnionAttr)).booleanValue()) {
            // evaluating assertions for "simpleType -> union" for an attribute            
            if (!evaluateAssertionOnSTUnion(element, simpleTypeDefn, isTypeDerivedFromUnion, assertImpl, value, augs)) {
                if (isAssertEvaluationFromSchema) {
                   isAssertSucceeded = Boolean.FALSE;  
                }
                else {
                   fXmlSchemaValidator.reportSchemaError("cvc-attribute.3", new Object[] {element.rawname, attrQname.localpart, value, ((XSSimpleTypeDecl)simpleTypeDefn).getTypeName()});
                }
            } 
        }
        
        return isAssertSucceeded;
        
    } // evaluateOneAssertionFromSimpleType
    
    
    /*
     * Evaluate assertions from itemType with variety 'atomic' on a simpleType->list.
     */
    public Boolean evaluateAssertsFromItemTypeOfSTList(QName element, XSSimpleTypeDefinition listItemType, String value, 
                                                       boolean isAssertEvaluationFromSchema) throws Exception {
        
        Boolean isAssertSucceeded = Boolean.TRUE;
        
        Vector itemTypeAsserts = XS11TypeHelper.getAssertsFromSimpleType(listItemType);
        
        if (listItemType.getVariety() ==  XSSimpleTypeDefinition.VARIETY_ATOMIC && itemTypeAsserts.size() > 0) {
            for (int assertIdx = 0; assertIdx < itemTypeAsserts.size(); assertIdx++) {
                XSAssertImpl itemTypeAssert = (XSAssertImpl) itemTypeAsserts.get(assertIdx);
                StringTokenizer listStrTokens = new StringTokenizer(value, " \n\t\r");
                while (listStrTokens.hasMoreTokens()) {
                    String listItemStrValue = listStrTokens.nextToken();
                    setXDMTypedValueOf$valueForSTVarietyList(fCurrentAssertDomNode, listItemStrValue, listItemType, false, fXpath2DynamicContext);                        
                    AssertionError assertError = evaluateOneAssertion(element, itemTypeAssert, listItemStrValue, false, true);                        
                    if (assertError != null) {
                       if (isAssertEvaluationFromSchema) {
                          isAssertSucceeded = Boolean.FALSE;  
                       }
                       else {
                          assertError.setIsTypeDerivedFromList(false);
                          reportAssertionsError(assertError);
                       }
                    }
                }
            }
        }
        
        return isAssertSucceeded;
        
    } // evaluateAssertsFromItemTypeOfSTList
    
    
    /*
     * Evaluate assertion on a simpleType xs:list value.
     */
    private Boolean evaluateAssertionOnSTListValue(QName element, String listStrValue, XSAssertImpl assertImpl, boolean xpathContextExists,
                                                   XSSimpleTypeDefinition itemType, boolean isTypeDerivedFromList, boolean isAssertEvaluationFromSchema) throws Exception {
        
        Boolean isAssertSucceeded = Boolean.TRUE;
        
        AssertionError assertError = null;
        
        if (isTypeDerivedFromList) {
            setXDMTypedValueOf$valueForSTVarietyList(fCurrentAssertDomNode, listStrValue, itemType, isTypeDerivedFromList, fXpath2DynamicContext);
            assertError = evaluateOneAssertion(element, assertImpl, listStrValue, xpathContextExists, true);
            if (assertError != null) {
                if (isAssertEvaluationFromSchema) {
                   isAssertSucceeded = Boolean.FALSE; 
                }
                else {
                   assertError.setIsTypeDerivedFromList(isTypeDerivedFromList);
                   reportAssertionsError(assertError);
                }
            }            
        }
        else {
            // evaluate assertion on all of list items
            // tokenize the list value by a sequence of white spaces
            StringTokenizer listStrTokens = new StringTokenizer(listStrValue, " \n\t\r");
            while (listStrTokens.hasMoreTokens()) {
                String listItemStrValue = listStrTokens.nextToken();
                setXDMTypedValueOf$valueForSTVarietyList(fCurrentAssertDomNode, listItemStrValue, itemType, isTypeDerivedFromList, fXpath2DynamicContext);                        
                assertError = evaluateOneAssertion(element, assertImpl, listItemStrValue, xpathContextExists, true);
                if (assertError != null) {
                    if (isAssertEvaluationFromSchema) {
                       isAssertSucceeded = Boolean.FALSE; 
                    }
                    else {
                       reportAssertionsError(assertError);
                    }
                }
            }
        }
        
        return isAssertSucceeded;
        
    } // evaluateAssertionOnSTListValue
    
    
    /*
     * Evaluate assertion on a simpleType with variety xs:union.
     */
    private boolean evaluateAssertionOnSTUnion(QName element, XSSimpleTypeDefinition simpleTypeDefn, boolean isTypeDerivedFromUnion, XSAssertImpl assertImpl, String value, Augmentations augs) throws Exception {
        
        boolean isValueValid = true;
        
        XSObjectList memberTypes = simpleTypeDefn.getMemberTypes();
        if (memberTypes != null && memberTypes.getLength() > 0 && !isTypeDerivedFromUnion) {            
            if (isValidationFailedForSTUnion(memberTypes, element, value, augs, false)) { 
                isValueValid = false;
                if (assertImpl.getAttrName() == null) {
                    // assertion evaluation was for an element
                    fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid-union-elem", new Object[] {value, element.rawname, ((XSSimpleTypeDecl)simpleTypeDefn).getTypeName()});
                }
                else {
                    // assertion evaluation was for an attribute
                    fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid-union-attr", new Object[] {value, assertImpl.getAttrName(), element.rawname, ((XSSimpleTypeDecl)simpleTypeDefn).getTypeName()});
                }
                fXmlSchemaValidator.reportSchemaError("cvc-datatype-valid.1.2.3", new Object[] {value, ((XSSimpleTypeDecl)simpleTypeDefn).getTypeName()});
            } 
         }
         else if (isTypeDerivedFromUnion) {
             setXDMTypedValueOf$valueForSTVarietyUnion(value, memberTypes, fXpath2DynamicContext);
             AssertionError assertError = evaluateOneAssertion(element, assertImpl, value, false, false);
             if (assertError != null) {
                 isValueValid = false;
                 reportAssertionsError(assertError);    
             }
         }
        
        return isValueValid;
        
    } // evaluateAssertionOnSTUnion
    
    
    /*
     * Evaluate assertions from a "complex type" definition.
     */
    private void evaluateAssertionsFromComplexType(QName element, List assertions, String value, Augmentations augs) throws Exception {
        
        ElementPSVI elemPsvi = (ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI);
        
        if (value != null) {
            // complex type with simple content
            restorePsviInfoForXPathContext(elemPsvi);
            setXDMValueOf$valueForCTWithSimpleContent(value, (XSComplexTypeDefinition)elemPsvi.getTypeDefinition(), fXpath2DynamicContext);
            savePsviInfoWithUntypingOfAssertRoot(elemPsvi, true);
        } else {
            // complex type with complex content. set xpath context variable $value to an empty sequence.
            fXpath2DynamicContext.set_variable(new org.eclipse.wst.xml.xpath2.processor.internal.types.QName("value"), XS11TypeHelper.getXPath2ResultSequence(new ArrayList()));
        }
        
        XSObjectList assertList = (XSObjectList) assertions;
        for (int assertIdx = 0; assertIdx < assertList.size(); assertIdx++) {
            XSAssertImpl assertImpl = (XSAssertImpl) assertList.get(assertIdx);
            String xPathDefaultNamespace = assertImpl.getXPathDefaultNamespace();             
            if (xPathDefaultNamespace != null) {
                fXpath2DynamicContext.add_namespace(null, xPathDefaultNamespace);  
            }
            // NOTE: asserts from attributes are not evaluated here. they are evaluated in method startElement -> evaluateAssertsFromAttributes.  
            if (assertImpl.getType() == XSConstants.ASSERTION) {
                // is an xs:assert component
                AssertionError assertError = evaluateOneAssertion(element, assertImpl, value, true, false);
                if (assertError != null) {
                    reportAssertionsError(assertError);    
                }   
            } 
            else if (assertImpl.getAttrName() == null) {
                // complex type with simple content
                XSSimpleTypeDefinition simpleTypeDefn = null;
                XSTypeDefinition xsTypeDefn = assertImpl.getTypeDefinition();                
                if (xsTypeDefn instanceof XSComplexTypeDefinition) {
                    simpleTypeDefn = ((XSComplexTypeDefinition) xsTypeDefn).getSimpleType();   
                }
                else {
                    simpleTypeDefn = (XSSimpleTypeDefinition) xsTypeDefn;  
                }
                XSComplexTypeDefinition complexTypeDef = (XSComplexTypeDefinition)elemPsvi.getTypeDefinition();
                if (XS11TypeHelper.isComplexTypeDerivedFromSTList(complexTypeDef, XSConstants.DERIVATION_EXTENSION)) {
                    // reassign value to simple type instance
                    simpleTypeDefn = (XSSimpleTypeDefinition)complexTypeDef.getBaseType(); 
                }
                boolean isTypeDerivedFromList = isTypeDerivedFromSTList(simpleTypeDefn);
                boolean isTypeDerivedFromUnion = isTypeDerivedFromSTUnion(simpleTypeDefn);
                restorePsviInfoForXPathContext(elemPsvi);
                evaluateOneAssertionFromSimpleType(element, value, augs, simpleTypeDefn, isTypeDerivedFromList, isTypeDerivedFromUnion, assertImpl, false, null, false);
                savePsviInfoWithUntypingOfAssertRoot(elemPsvi, true);
                // evaluate assertions on itemType of xs:list
                XSSimpleTypeDefinition listItemType = simpleTypeDefn.getItemType();
                if (isTypeDerivedFromList && listItemType != null) {
                    restorePsviInfoForXPathContext(elemPsvi);
                    evaluateAssertsFromItemTypeOfSTList(element, listItemType, value, false);
                    savePsviInfoWithUntypingOfAssertRoot(elemPsvi, true);;
                }
            }            
        }       
        
    } // evaluateAssertionsFromComplexType
    
    
    /*
     * Method to evaluate an assertion. Returns the evaluation error details in an AssertionError object.
     */
    public AssertionError evaluateOneAssertion(QName element, XSAssertImpl assertImpl, String value, boolean xPathContextExists, boolean isList) {
        
        AssertionError assertionError = null;
        
        try {  
            XPath xpathObject = assertImpl.getCompiledXPathExpr();
            
            boolean result;            
            if (value == null || xPathContextExists == true) {
                result = fAbstrXpathEngineImpl.evaluateXPathExpr(xpathObject, fCurrentAssertDomNode);  
            } 
            else {
                // XPath context is "undefined"
                result = fAbstrXpathEngineImpl.evaluateXPathExpr(xpathObject, null); 
            }
            
            if (!result) {
               // assertion evaluation is false
               assertionError = new AssertionError("cvc-assertion", element, assertImpl, value, isList, null); 
            }
        }
        catch(Exception ex) {
            assertionError = new AssertionError("cvc-assertion", element, assertImpl, value, isList, ex);   
        }
        
        // if an assertion failed, add reference of that assertion to the failed assertions list.
        // NOTE: [failed assertions] property need to be set only for "element information items" and not for attributes.
        if (assertionError != null && assertImpl.getAttrName() == null) {
            if (fFailedAssertions == null) {
                fFailedAssertions = new XSAssertImpl[1];  
            }
            else {
                XSAssertImpl [] tempArray = new XSAssertImpl[fFailedAssertions.length + 1];
                System.arraycopy(fFailedAssertions, 0, tempArray, 0, fFailedAssertions.length);
                fFailedAssertions = tempArray;
            }
            fFailedAssertions[fFailedAssertions.length-1] = (XSAssert) assertImpl;
        }
        
        return assertionError;
        
    } // evaluateOneAssertion
    
    
    /*
     * Determine if an validation episode must fail due to assertions evaluation for "simpleType -> union" member types.
     */
    public boolean isValidationFailedForSTUnion(XSObjectList memberTypes, QName element, String value, Augmentations augs, 
                                                boolean isAssertEvaluationFromSchema) {
        
        boolean isValidationFailedForUnion = true;

        for (int memberTypeIdx = 0; memberTypeIdx < memberTypes.getLength(); memberTypeIdx++) {
            XSSimpleTypeDefinition memType = (XSSimpleTypeDefinition) memberTypes.item(memberTypeIdx);            
            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(memType.getNamespace()) && !XS11TypeHelper.simpleTypeHasAsserts(memType) 
                                                            && XS11TypeHelper.isStrValueValidForASimpleType(value, (XSSimpleType)memType, 
                                                                    Constants.SCHEMA_VERSION_1_1)) {
               isValidationFailedForUnion = false;
               break;
            }
            else if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(memType.getNamespace()) && XS11TypeHelper.isStrValueValidForASimpleType(value, 
                                                                    (XSSimpleType)memType, Constants.SCHEMA_VERSION_1_1)) {
               isValidationFailedForUnion = false;
               break;
            }
            else if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(memType.getNamespace())) {
               XSObjectList memberTypeFacets = memType.getMultiValueFacets();
               for (int memberTypeFacetIdx = 0; memberTypeFacetIdx < memberTypeFacets.getLength(); memberTypeFacetIdx++) {
                  XSMultiValueFacet facet = (XSMultiValueFacet) memberTypeFacets.item(memberTypeFacetIdx);
                  if (facet.getFacetKind() == XSSimpleTypeDefinition.FACET_ASSERT) {
                     Vector assertFacets = facet.getAsserts();
                     int assertsSucceeded = 0;
                     for (Iterator iter = assertFacets.iterator(); iter.hasNext(); ) {
                        XSAssertImpl assertImpl = (XSAssertImpl) iter.next();
                        try {
                          setXDMTypedValueOf$value(fCurrentAssertDomNode, value, memType, null, false, fXpath2DynamicContext);
                          AssertionError assertError = evaluateOneAssertion(element, assertImpl, value, false, false);
                          if (assertError == null) {
                             assertsSucceeded++;  
                          }
                        }
                        catch(Exception ex) {
                           // NO OP
                        }
                      }
                      if (assertsSucceeded == assertFacets.size()) {
                         // all assertions on a 'union' member type have evaluated to 'true', therefore 
                         // validation with union has succeeded wrt assertions.
                         if (!isAssertEvaluationFromSchema) {
                             // update memberType PSVI property
                             ItemPSVI elemPSVI = (ItemPSVI)augs.getItem(Constants.ELEMENT_PSVI);
                             ItemPSVI attrPSVI = (ItemPSVI)augs.getItem(Constants.ATTRIBUTE_PSVI);
                             if (elemPSVI != null) {
                                 ((ElementPSVImpl) elemPSVI).fValue.memberType = (XSSimpleType) memType;
                             }
                             else {
                                 ((AttributePSVImpl) attrPSVI).fValue.memberType = (XSSimpleType) memType;
                             }
                         }
                         isValidationFailedForUnion = false;
                         break; 
                      }
                   }
                }
                if (!isValidationFailedForUnion) {
                   break;  
                }
             }
        }

        return isValidationFailedForUnion;
        
    } // isValidationFailedForSTUnion
    

    /*
     * (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLAssertAdapter#characters
     *      (org.apache.xerces.xni.XMLString)
     */
    public void characters(XMLString text) {        
        if (fCurrentAssertDomNode != null) {
            fIsTextNodeCall = true;
            fTextNodeStrBuff.append(new String(text.ch, text.offset, text.length));
        }        
    }
    
    public DynamicContext getDynamicContext() {
        return fXpath2DynamicContext; 
    }    
    
    /*
     * Method to report assertions error messages.
     */
    private void reportAssertionsError(AssertionError assertError) {
        
        String key = assertError.getErrorCode();
        QName element = assertError.getElement();
        XSAssertImpl assertImpl = assertError.getAssertion();
        boolean isList = assertError.isList();
        String value = assertError.getValue();
        
        // construct the message fragment in case of XPath DynamicError or StaticError  
        String exceptionMesg = "";
        Exception exception = assertError.getException();        
        if (exception instanceof DynamicError) {
            exceptionMesg = ((DynamicError) exception).code() + " : " + ((DynamicError) exception).getMessage();   
        }
        else if (exception instanceof StaticError) {
            exceptionMesg = ((StaticError) exception).code() + " : " + ((StaticError) exception).getMessage();
        }
        if (!"".equals(exceptionMesg) && !exceptionMesg.endsWith(".")) {
            exceptionMesg = exceptionMesg + ".";  
        }
               
        String typeNameStr = XS11TypeHelper.getSchemaTypeName(assertImpl.getTypeDefinition());
        
        String elemNameAnnotation = element.rawname;
        if (assertImpl.getAttrName() != null) {
            elemNameAnnotation = element.rawname + " (attribute => " + assertImpl.getAttrName()+ ")";    
        }                
        
        String listAssertErrMessage = "";        
        if (isList) {
           if (assertError.getIsTypeDerivedFromList()) {
               listAssertErrMessage =  "Assertion failed for xs:list instance '" + value + "'.";  
           }
           else {
               listAssertErrMessage =  "Assertion failed for an xs:list member value '" + value + "'.";
           }
        }
            
        String mesgSuffix = "".equals(listAssertErrMessage) ? exceptionMesg : (listAssertErrMessage + ("".equals(exceptionMesg) ? "" : " " + exceptionMesg));
        String userDefinedMessage = assertImpl.getMessage();
        if (userDefinedMessage != null) {
           // substitute all placeholder macro instances of "{$value}" with atomic value stored in variable "value"
           if (value != null && !"".equals(value)) {
               userDefinedMessage = userDefinedMessage.replaceAll(SchemaSymbols.ASSERT_ERRORMSG_PLACEHOLDER_REGEX, value);
           }
           
           if (!userDefinedMessage.endsWith(".")) {
               userDefinedMessage = userDefinedMessage + ".";    
           }
           userDefinedMessage = "Assertion failed for schema type '" + typeNameStr + "'. " + userDefinedMessage;          
           fXmlSchemaValidator.reportSchemaError("cvc-assertion-failure-mesg", new Object[] {userDefinedMessage, mesgSuffix});    
        }
        else {
           if (assertImpl.getAssertKind() == XSConstants.ASSERTION) {
              // error for xs:assert component
              fXmlSchemaValidator.reportSchemaError(key, new Object[] {elemNameAnnotation, assertImpl.getTest().getXPathStr(), typeNameStr, mesgSuffix});
           }
           else {
               // errors for xs:assertion facet
               fXmlSchemaValidator.reportSchemaError("cvc-assertions-valid", new Object[] {value, assertImpl.getTest().getXPathStr(), exceptionMesg});
               fXmlSchemaValidator.reportSchemaError(key, new Object[] {elemNameAnnotation, assertImpl.getTest().getXPathStr(), typeNameStr, mesgSuffix});  
           }
        }
        
    } // reportAssertionsError
    
    
    /*
     * Class to store "assertion evaluation" error details.
     */
    public final class AssertionError {
        
        // instance variables        
        private final String errorCode;
        private final QName element;
        private final XSAssertImpl assertImpl;
        private final String value;
        // does this error concerns "simpleType -> list"
        private final boolean isList;
        private boolean isTypeDerivedFromList = false;
        Exception ex = null;
        
        // class constructor
        public AssertionError(String errorCode, QName element, XSAssertImpl assertImpl, String value, boolean isList, Exception ex) {
           this.errorCode = errorCode;
           this.element = element;
           this.assertImpl = assertImpl;
           this.value = value;
           this.isList = isList;
           this.ex = ex;
        }
        
        public void setIsTypeDerivedFromList(boolean isTypeDerivedFromList) {
            this.isTypeDerivedFromList = isTypeDerivedFromList; 
        }

        public String getErrorCode() {
           return errorCode;    
        }
        
        public QName getElement() {
           return element;       
        }
        
        public XSAssertImpl getAssertion() {
           return assertImpl;    
        }
        
        public String getValue() {
           return value; 
        }
        
        public boolean isList() {
           return isList;    
        }
        
        public boolean getIsTypeDerivedFromList() {
           return isTypeDerivedFromList; 
        }
        
        public Exception getException() {
            return ex;
        }
        
    } // class AssertionError
    

    /*
     * Check if a simple type definition has a base type whose variety is simpleType->list.
     */
    private boolean isTypeDerivedFromSTList(XSSimpleTypeDefinition simpleTypeDefn) {
        return ((XSSimpleType) simpleTypeDefn.getBaseType()).getVariety() == XSSimpleType.VARIETY_LIST;
    }
    
    
    /*
     * Check if a simple type definition has a base type whose variety is simpleType->union.
     */
    private boolean isTypeDerivedFromSTUnion(XSSimpleTypeDefinition simpleTypeDefn) {
        return ((XSSimpleType) simpleTypeDefn.getBaseType()).getVariety() == XSSimpleType.VARIETY_UNION;
    }
    
} // class XMLAssertXPath2EngineImpl
