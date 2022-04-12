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

package org.apache.xerces.impl.xs.traversers;

import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xpath.XPath20;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.impl.xs.AbstractXPath2EngineImpl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.alternative.Test;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.eclipse.wst.xml.xpath2.processor.StaticChecker;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.StaticNameResolver;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.w3c.dom.Element;

/**
 * The traverser implementation for XML Schema 1.1 'type alternative' component.
 * 
 * <alternative
 *    id = ID
 *    test = an XPath expression
 *    type = QName
 *    xpathDefaultNamespace = (anyURI | (##defaultNamespace | ##targetNamespace | ##local))
 *    {any attributes with non-schema namespace . . .}>
 *    Content: (annotation?, (simpleType | complexType)?)
 * </alternative>
 * 
 * @xerces.internal
 * 
 * @author Hiranya Jayathilaka, University of Moratuwa
 * @author Mukul Gandhi IBM
 * @version $Id$
 */
class XSDTypeAlternativeTraverser extends XSDAbstractTraverser {
    
    private static final XSSimpleType fErrorType;    
    private boolean fIsFullXPathModeForCTA;
    private String[] fctaXPathModes = {"cta-subset", "cta-full"};
    
    static {
        SchemaGrammar grammar = SchemaGrammar.getS4SGrammar(Constants.SCHEMA_VERSION_1_1);
        fErrorType = (XSSimpleType)grammar.getGlobalTypeDecl("error");
    }

    XSDTypeAlternativeTraverser (XSDHandler handler, XSAttributeChecker attrChecker) {
        super(handler, attrChecker);
        fIsFullXPathModeForCTA = handler.fFullXPathForCTA;
    }

    /**
     * Traverse the given alternative element and update the
     * schema grammar. Validate the content of the type alternative
     * element.
     */
    public void traverse(Element altElement, XSElementDecl element, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {

        Object[] attrValues = fAttrChecker.checkAttributes(altElement, false, schemaDoc);
        QName typeAtt = (QName) attrValues[XSAttributeChecker.ATTIDX_TYPE];
        String testStr = (String) attrValues[XSAttributeChecker.ATTIDX_XPATH];
        String xpathDefaultNS = (String) attrValues[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];

        // get 'annotation'
        Element childNode = DOMUtil.getFirstChildElement(altElement);
        XSAnnotationImpl annotation = null;
        //first child could be an annotation
        if (childNode != null && DOMUtil.getLocalName(childNode).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = traverseAnnotationDecl(childNode, attrValues, false, schemaDoc);
            //now move on to the next child element
            childNode = DOMUtil.getNextSiblingElement(childNode);
        }
        else {
            String text = DOMUtil.getSyntheticAnnotation(altElement);
            if (text != null) {
                annotation = traverseSyntheticAnnotation(altElement, text, attrValues, false, schemaDoc);
            }          
            //here we remain in the first child element
        }

        XSObjectList annotations = null;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            //if no annotations are present add an empty list to the type alternative
            annotations = XSObjectListImpl.EMPTY_LIST;
        }

        // get 'type definition'
        XSTypeDefinition alternativeType = null;
        boolean hasAnonType = false;
        
        if (typeAtt != null) {
            alternativeType = (XSTypeDefinition)fSchemaHandler.getGlobalDecl(schemaDoc, XSDHandler.TYPEDECL_TYPE, typeAtt, altElement);
        }
        
        // check whether the childNode still points to something...
        // if it does it must be an anonymous type declaration
        if (childNode != null) {
            // traverse any anonymous type declarations present
            // do not care whether the type attr is present or not
            String childName = DOMUtil.getLocalName(childNode);
            XSTypeDefinition typeDef = null;
            if (childName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                typeDef = fSchemaHandler.fComplexTypeTraverser.traverseLocal(childNode, schemaDoc, grammar, element);
                hasAnonType = true;
                childNode = DOMUtil.getNextSiblingElement(childNode);
            }
            else if (childName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                typeDef = fSchemaHandler.fSimpleTypeTraverser.traverseLocal(childNode, schemaDoc, grammar, element);
                hasAnonType = true;
                childNode = DOMUtil.getNextSiblingElement(childNode);
            }
            
            if (alternativeType == null) {
                alternativeType = typeDef;
            }
            
            // type and either <simpleType> or <complexType> are mutually exclusive.
            if (hasAnonType && (typeAtt != null)) {
                reportSchemaError("src-type-alternative.3.12.13.1", null, altElement);
            }
        }

        // if the type definition component is not present..
        // i.e. test attr value is absent and no anonymous types are defined
        if (typeAtt == null && !hasAnonType) {
            reportSchemaError("src-type-alternative.3.12.13.2", null, altElement);
        }

        // fall back to the element declaration's type
        if (alternativeType == null) {
            alternativeType = element.fType;
        }
        // Element Properties Correct
        // 7.1 T is validly substitutable for E.{type definition}, subject to
        //     the blocking keywords of E.{disallowed substitutions}.
        // 7.2 T is the type xs:error
        else if (alternativeType != fErrorType){
            short block = element.fBlock; 
            if (element.fType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
                block |= ((XSComplexTypeDecl) element.fType).getProhibitedSubstitutions();
            }
            if (!fSchemaHandler.fXSConstraints.checkTypeDerivationOk(alternativeType, element.fType, block)) {
                reportSchemaError(
                        "e-props-correct.7",
                        new Object[] {element.getName(), XS11TypeHelper.getSchemaTypeName(alternativeType), 
                                      XS11TypeHelper.getSchemaTypeName(element.fType)},
                        altElement);
                // fall back to element declaration's type
                alternativeType = element.fType;
            }
        }

        // not expecting any more children
        if (childNode != null) {            
            reportSchemaError("s4s-elt-must-match.1", new Object[]{"type alternative", "(annotation?, (simpleType | complexType)?)", 
                                                                   DOMUtil.getLocalName(childNode)}, childNode);
        }

        // create type alternative component
        XSTypeAlternativeImpl typeAlternative = new XSTypeAlternativeImpl(element.fName, alternativeType, annotations);

        // now look for other optional attributes like test and xpathDefaultNamespace
        if (testStr != null) {
            Test testExpr = null;
            try {
                if (fIsFullXPathModeForCTA) {
                    // if full XPath 2.0 support is enabled for CTA, use Eclipse XPath 2.0 engine for XPath evaluation
                    XPathParser xpp = new JFlexCupParser();
                    XPath xp = xpp.parse("boolean(" + testStr + ")", true);
                    Map eclipseXpathParams = new HashMap();
                    eclipseXpathParams.put(Constants.XPATH2_NAMESPACE_CONTEXT, schemaDoc.fNamespaceSupport);
                    AbstractXPath2EngineImpl abstractXpathEngineImpl = new AbstractXPath2EngineImpl();
                    StaticChecker name_check = new StaticNameResolver(abstractXpathEngineImpl.initXPath2DynamicContext(null, null, 
                                                                                                                  eclipseXpathParams));
                    name_check.check(xp);
                    testExpr = new Test(xp, testStr, typeAlternative, schemaDoc.fNamespaceSupport);
                }
                else {
                    // if XPath subset is enabled for CTA (this is also the default option), use Xerces native XPath parser for CTA
                    testExpr = new Test(new XPath20(testStr, fSymbolTable, new NamespaceSupport(schemaDoc.fNamespaceSupport)), 
                                                                                                typeAlternative, 
                                                                                                new NamespaceSupport(schemaDoc.fNamespaceSupport));
                }
            }
            catch (XPathException e) {
                // if XPath expression couldn't compile, create a "test" without an expression
                testExpr = new Test((XPath20) null, typeAlternative, new NamespaceSupport(schemaDoc.fNamespaceSupport));
                reportSchemaError("c-cta-xpath", new Object[] {testStr, fctaXPathModes[0]}, altElement);                
            }
            catch(XPathParserException ex) {
                // XPath parser exception. create a "test" without an expression.
                testExpr = new Test((XPath20) null, typeAlternative, new NamespaceSupport(schemaDoc.fNamespaceSupport));
                if (SchemaSymbols.XS11_XPATHEXPR_COMPILE_WRN_MESG_1.equals(ex.getMessage())) {               
                    fSchemaHandler.reportSchemaWarning("c-cta-xpath-b", new Object[] {testStr, fctaXPathModes[1]}, altElement);
                }
                else {
                    reportSchemaError("c-cta-xpath", new Object[] {testStr, fctaXPathModes[1]}, altElement);
                }
            } catch (StaticError serr) {
                // if XPath expression couldn't compile, and there's a static error in XPath expression, create a "test" without an expression
                testExpr = new Test((XPath20) null, typeAlternative, new NamespaceSupport(schemaDoc.fNamespaceSupport));
                reportSchemaError("c-cta-xpath-serr", new Object[] {testStr, fctaXPathModes[1], serr.code()}, altElement);
            }            
            typeAlternative.setTest(testExpr);
        }
        else {
            typeAlternative.setNamespaceContext(new NamespaceSupport(schemaDoc.fNamespaceSupport)); 
        }
        
        // REVISIT : is using Document.getDocumentURI() correct to retrieve base URI in every case, for type alternatives?
        String baseURI = fSchemaHandler.getDocumentURI(altElement);
        if (baseURI != null) {
            typeAlternative.setBaseURI(baseURI);
        }

        if (xpathDefaultNS == null) {
            if (schemaDoc.fXpathDefaultNamespaceIs2PoundDefault) {
                xpathDefaultNS = schemaDoc.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                if (xpathDefaultNS != null) {
                    xpathDefaultNS = fSymbolTable.addSymbol(xpathDefaultNS);
                    
                }
            }
            else {
                xpathDefaultNS = schemaDoc.fXpathDefaultNamespace;
            }          
        }
        if (xpathDefaultNS != null) {
            //set the xpathDefaultNamespace attribute value
            typeAlternative.setXPathDefauleNamespace(xpathDefaultNS);
        }
        
        grammar.addTypeAlternative(element, typeAlternative);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        
    } // traverse
    
} // class XSDTypeAlternativeTraverser
