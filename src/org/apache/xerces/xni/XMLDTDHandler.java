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

package org.apache.xerces.xni;

import org.apache.xerces.xni.parser.XMLDTDSource;

/**
 * The DTD handler interface defines callback methods to report
 * information items in the DTD of an XML document. Parser components
 * interested in DTD information implement this interface and are
 * registered as the DTD handler on the DTD source.
 *
 * @see XMLDTDContentModelHandler
 *
 * @author Andy Clark, IBM
 *
 * @version $Id$
 */
public interface XMLDTDHandler {

    //
    // Constants
    //

    /**
     * Conditional section: INCLUDE. 
     *
     * @see XMLDTDHandler#CONDITIONAL_IGNORE
     */
    public static final short CONDITIONAL_INCLUDE = 0;

    /** 
     * Conditional section: IGNORE.
     *
     * @see XMLDTDHandler#CONDITIONAL_INCLUDE
     */
    public static final short CONDITIONAL_IGNORE = 1;

    //
    // XMLDTDHandler methods
    //

    /**
     * The start of the DTD.
     *
     * @param locator  the document locator, or null if the document
     *                 location cannot be reported during the parsing of
     *                 the document DTD. However, it is <em>strongly</em>
     *                 recommended that a locator be supplied that can
     *                 at least report the base URI of the DTD.
     * @param augmentations additional information that may include infoset augmentations
     * @throws XNIException thrown by handler to signal an error
     */
    public void startDTD(XMLLocator locator, Augmentations augmentations) 
        throws XNIException;

    /**
     * This method notifies of the start of a parameter entity. The parameter
     * entity name start with a '%' character.
     * 
     * @param name the name of the parameter entity
     * @param identifier the resource identifier
     * @param encoding the auto-detected IANA encoding name of the entity stream. This
     *                 value will be null in those situations where the entity encoding
     *                 is not auto-detected (e.g. internal parameter entities).
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void startParameterEntity(String name, 
                                     XMLResourceIdentifier identifier,
                                     String encoding,
                                     Augmentations augmentations) throws XNIException;

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p>
     * <strong>Note:</strong> This method is only called for external
     * parameter entities referenced in the DTD.
     * 
     * @param version the XML version, or null if not specified
     * @param encoding the IANA encoding name of the entity
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void textDecl(String version, String encoding,
                         Augmentations augmentations) throws XNIException;

    /**
     * This method notifies the end of a parameter entity. Parameter entity
     * names begin with a '%' character.
     * 
     * @param name the name of the parameter entity
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void endParameterEntity(String name, Augmentations augmentations) 
        throws XNIException;

    /**
     * The start of the DTD external subset.
     * 
     * @param identifier the resource identifier
     * @param augmentations additional information that may include infoset augmentations
     * @throws XNIException thrown by handler to signal an error
     */
    public void startExternalSubset(XMLResourceIdentifier identifier, 
                                    Augmentations augmentations) 
        throws XNIException;

    /**
     * The end of the DTD external subset.
     *
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void endExternalSubset(Augmentations augmentations) 
        throws XNIException;

    /**
     * A comment.
     * 
     * @param text the text in the comment
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by application to signal an error
     */
    public void comment(XMLString text, Augmentations augmentations) 
        throws XNIException;

    /**
     * A processing instruction. Processing instructions consist of a
     * target name and, optionally, text data. The data is only meaningful
     * to the application.
     * <p>
     * Typically, a processing instruction's data will contain a series
     * of pseudo-attributes. These pseudo-attributes follow the form of
     * element attributes but are <strong>not</strong> parsed or presented
     * to the application as anything other than text. The application is
     * responsible for parsing the data.
     * 
     * @param target the target
     * @param data the data or null if none specified
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void processingInstruction(String target, XMLString data,
                                      Augmentations augmentations)
        throws XNIException;

    /**
     * An element declaration.
     * 
     * @param name the name of the element
     * @param contentModel the element content model
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void elementDecl(String name, String contentModel,
                            Augmentations augmentations)
        throws XNIException;

    /**
     * The start of an attribute list.
     * 
     * @param elementName the name of the element that this attribute list is associated with
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void startAttlist(String elementName,
                             Augmentations augmentations) throws XNIException;

    /**
     * An attribute declaration.
     * 
     * @param elementName the name of the element that this attribute is associated with
     * @param attributeName the name of the attribute
     * @param type the attribute type. This value will be one of the following:
     *             "CDATA", "ENTITY", "ENTITIES", "ENUMERATION", "ID", "IDREF", "IDREFS",
     *             "NMTOKEN", "NMTOKENS", or "NOTATION"
     * @param enumeration if the type has the value "ENUMERATION" or "NOTATION", this array
     *                    holds the allowed attribute values; otherwise, this array is null
     * @param defaultType the attribute default type. This value will be one of the following:
     *                    "#FIXED", "#IMPLIED", "#REQUIRED", or null
     * @param defaultValue the attribute default value, or null if no default value is specified
     * @param nonNormalizedDefaultValue the attribute default value with no normalization
     *                                  performed, or null if no default value is specified
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void attributeDecl(String elementName, String attributeName, 
                              String type, String[] enumeration, 
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augmentations)
        throws XNIException;

    /**
     * The end of an attribute list.
     *
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void endAttlist(Augmentations augmentations) throws XNIException;

    /**
     * An internal entity declaration.
     * 
     * @param name the name of the entity. Parameter entity names start with '%', whereas
     *             the name of a general entity is just the entity name
     * @param text the value of the entity
     * @param nonNormalizedText the non-normalized value of the entity. This
     *             value contains the same sequence of characters that was in 
     *             the internal entity declaration, without any entity
     *             references expanded.
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void internalEntityDecl(String name, XMLString text, 
                                   XMLString nonNormalizedText,
                                   Augmentations augmentations) 
        throws XNIException;

    /**
     * An external entity declaration.
     * 
     * @param name the name of the entity. Parameter entity names start with '%', whereas
     *             the name of a general entity is just the entity name
     * @param identifier an object containing all location information pertinent to this
     *                   external entity
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void externalEntityDecl(String name, 
                                   XMLResourceIdentifier identifier,
                                   Augmentations augmentations) 
        throws XNIException;

    /**
     * An unparsed entity declaration.
     * 
     * @param name the name of the entity
     * @param identifier an object containing all location information pertinent to this
     *                   unparsed entity declaration
     * @param notation the name of the notation
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void unparsedEntityDecl(String name, 
                                   XMLResourceIdentifier identifier, 
                                   String notation, Augmentations augmentations) 
        throws XNIException;

    /**
     * A notation declaration
     * 
     * @param name the name of the notation
     * @param identifier an object containing all location information pertinent to this notation
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augmentations) throws XNIException;

    /**
     * The start of a conditional section.
     * 
     * @param type the type of the conditional section. This value will either be
     *             CONDITIONAL_INCLUDE or CONDITIONAL_IGNORE
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     *
     * @see XMLDTDHandler#CONDITIONAL_INCLUDE
     * @see XMLDTDHandler#CONDITIONAL_IGNORE
     */
    public void startConditional(short type, Augmentations augmentations) 
        throws XNIException;

    /**
     * Characters within an IGNORE conditional section.
     *
     * @param text the ignored text
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void ignoredCharacters(XMLString text, Augmentations augmentations) 
        throws XNIException;

    /**
     * The end of a conditional section.
     *
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void endConditional(Augmentations augmentations) throws XNIException;

    /**
     * The end of the DTD.
     *
     * @param augmentations additional information that may include infoset augmentations
     *
     * @throws XNIException thrown by handler to signal an error
     */
    public void endDTD(Augmentations augmentations) throws XNIException;

    /**
     * Sets the source of this handler.
     *
     * @param source the source of this handler
     */
    public void setDTDSource(XMLDTDSource source);

    /**
     * Returns the source from which this handler derives its events.
     *
     * @return the source from which this handler derives its events
     */
    public XMLDTDSource getDTDSource();

} // interface XMLDTDHandler
