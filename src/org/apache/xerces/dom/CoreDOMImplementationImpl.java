/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;


// DOM L3 LS
import org.apache.xerces.dom3.ls.DOMImplementationLS;
import org.apache.xerces.dom3.ls.DOMBuilder;
import org.apache.xerces.dom3.ls.DOMWriter;
import org.apache.xerces.dom3.ls.DOMInputSource;
import org.apache.xerces.parsers.DOMBuilderImpl;
import org.apache.xml.serialize.XMLSerializer;

// DOM Revalidation
import org.apache.xerces.impl.DOMRevalidationHandler;
import org.apache.xerces.impl.xs.XMLSchemaValidator;

/**
 * The DOMImplementation class is description of a particular
 * implementation of the Document Object Model. As such its data is
 * static, shared by all instances of this implementation.
 * <P>
 * The DOM API requires that it be a real object rather than static
 * methods. However, there's nothing that says it can't be a singleton,
 * so that's how I've implemented it.
 * <P>
 * This particular class, along with CoreDocumentImpl, supports the DOM
 * Core and Load/Save (Experimental). Optional modules are supported by 
 * the more complete DOMImplementation class along with DocumentImpl.
 * @version $Id$
 * @since PR-DOM-Level-1-19980818.
 */
public class CoreDOMImplementationImpl  
implements DOMImplementation, DOMImplementationLS {

    //
    // Data
    //


    DOMRevalidationHandler fDOMRevalidator = null;

    boolean free = true;

    // static

    /** Dom implementation singleton. */
    static CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();
    

    //
    // Public methods
    //

    /** NON-DOM: Obtain and return the single shared object */
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }  


    //
    // DOMImplementation methods
    //

    /** 
     * Test if the DOM implementation supports a specific "feature" --
     * currently meaning language and level thereof.
     * 
     * @param feature      The package name of the feature to test.
     * In Level 1, supported values are "HTML" and "XML" (case-insensitive).
     * At this writing, org.apache.xerces.dom supports only XML.
     *
     * @param version      The version number of the feature being tested.
     * This is interpreted as "Version of the DOM API supported for the
     * specified Feature", and in Level 1 should be "1.0"
     *
     * @returns    true iff this implementation is compatable with the
     * specified feature and version.
     */
    public boolean hasFeature(String feature, String version) {

        // Currently, we support only XML Level 1 version 1.0
        boolean anyVersion = version == null || version.length() == 0;
        return 
        (feature.equalsIgnoreCase("Core") 
         && (anyVersion
             || version.equals("1.0")
             || version.equals("2.0")))
        || (feature.equalsIgnoreCase("XML") 
            && (anyVersion
                || version.equals("1.0")
                || version.equals("2.0")))
        /* || (feature.equalsIgnoreCase("LS-Load")
            && version.equals("3.0");
            */
        ;

    } // hasFeature(String,String):boolean


    /**
     * Introduced in DOM Level 2. <p>
     * 
     * Creates an empty DocumentType node.
     *
     * @param qualifiedName The qualified name of the document type to be created. 
     * @param publicID The document type public identifier.
     * @param systemID The document type system identifier.
     * @since WD-DOM-Level-2-19990923
     */
    public DocumentType       createDocumentType(String qualifiedName, 
                                                 String publicID, 
                                                 String systemID)
    {
        if (!CoreDocumentImpl.isXMLName(qualifiedName)) {
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, 
                                   "DOM002 Illegal character");
        }
        int index = qualifiedName.indexOf(':');
        int lastIndex = qualifiedName.lastIndexOf(':');
        // it is an error for NCName to have more than one ':'
        if (index == 0 || index == qualifiedName.length() - 1 || lastIndex!=index) {
            throw new DOMException(DOMException.NAMESPACE_ERR, 
                                   "DOM003 Namespace error");
        }
        return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
    }
    /**
     * Introduced in DOM Level 2. <p>
     * 
     * Creates an XML Document object of the specified type with its document
     * element.
     *
     * @param namespaceURI     The namespace URI of the document
     *                         element to create, or null. 
     * @param qualifiedName    The qualified name of the document
     *                         element to create. 
     * @param doctype          The type of document to be created or null.<p>
     *
     *                         When doctype is not null, its
     *                         Node.ownerDocument attribute is set to
     *                         the document being created.
     * @return Document        A new Document object.
     * @throws DOMException    WRONG_DOCUMENT_ERR: Raised if doctype has
     *                         already been used with a different document.
     * @since WD-DOM-Level-2-19990923
     */
    public Document           createDocument(String namespaceURI, 
                                             String qualifiedName, 
                                             DocumentType doctype)
    throws DOMException
    {
        if (doctype != null && doctype.getOwnerDocument() != null) {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, 
                                   "DOM005 Wrong document");
        }
        CoreDocumentImpl doc = new CoreDocumentImpl(doctype);
        Element e = doc.createElementNS( namespaceURI, qualifiedName);
        doc.appendChild(e);
        return doc;
    }

    // DOM L3 LS
    /**
     * DOM Level 3 WD - Experimental.
     */
    public DOMBuilder createDOMBuilder(short mode)
    throws DOMException {
        if (mode == DOMImplementationLS.MODE_ASYNCHRONOUS) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, 
                                   "Asynchronous mode is not supported");
        }
        return new DOMBuilderImpl();
    }
    /**
     * DOM Level 3 WD - Experimental.
     */                
    public DOMWriter createDOMWriter() {
        return new XMLSerializer();
    }
    /**
     * DOM Level 3 WD - Experimental.
     */
    public DOMInputSource createDOMInputSource() {
        return new DOMInputSourceImpl();
    }



    //
    // Protected methods
    //
    /** NON-DOM */
    synchronized DOMRevalidationHandler getValidator (String schemaType){
        // REVISIT: implement a pool of validators to avoid long
        //          waiting for several threads
        //          implement retrieving grammar based on schemaType
        if (fDOMRevalidator == null) {
            fDOMRevalidator = new XMLSchemaValidator();
        }
        while (!isFree()) {
            try { 
                wait();
            }
            catch (InterruptedException e){
                return new XMLSchemaValidator();
            }
        }
        free = false;
        return fDOMRevalidator;        
    }
    
    /** NON-DOM */
    synchronized void releaseValidator(String schemaType){
        // REVISIT: implement releasing grammar base on the schema type
        notifyAll();
        free = true;

    }
    
    /** NON-DOM */
    final synchronized boolean isFree(){
        return free;
    }




} // class DOMImplementationImpl
