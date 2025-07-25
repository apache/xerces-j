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


package org.apache.xml.serialize;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;


/**
 * Interface for a DOM serializer implementation, factory for DOM and SAX
 * serializers, and static methods for serializing DOM documents.
 * <p>
 * To serialize a document using SAX events, create a compatible serializer
 * and pass it around as a {@link
 * org.xml.sax.DocumentHandler}. If an I/O error occurs while serializing, it will
 * be thrown by {@link DocumentHandler#endDocument}. The SAX serializer
 * may also be used as {@link org.xml.sax.DTDHandler}, {@link org.xml.sax.ext.DeclHandler} and
 * {@link org.xml.sax.ext.LexicalHandler}.
 * <p>
 * To serialize a DOM document or DOM element, create a compatible
 * serializer and call it's {@link
 * DOMSerializer#serialize(Document)} or {@link DOMSerializer#serialize(Element)} methods.
 * Both methods would produce a full XML document, to serizlie only
 * the portion of the document use {@link OutputFormat#setOmitXMLDeclaration}
 * and specify no document type.
 * <p>
 * The {@link OutputFormat} dictates what underlying serialized is used
 * to serialize the document based on the specified method. If the output
 * format or method are missing, the default is an XML serializer with
 * UTF-8 encoding and now indentation.
 * 
 * @deprecated This class was deprecated in Xerces 2.9.0. It is recommended 
 * that new applications use the DOM Level 3 LSSerializer or JAXP's Transformation 
 * API for XML (TrAX) for serializing XML and HTML. See the Xerces documentation for more 
 * information.
 * @version $Revision$ $Date$
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 * @author <a href="mailto:Scott_Boag/CAM/Lotus@lotus.com">Scott Boag</a>
 * @see DocumentHandler
 * @see ContentHandler
 * @see OutputFormat
 * @see DOMSerializer
 */
@Deprecated
public interface Serializer
{


    /**
     * Specifies an output stream to which the document should be
     * serialized. This method should not be called while the
     * serializer is in the process of serializing a document.
     */
    public void setOutputByteStream(OutputStream output);
    

    /**
     * Specifies a writer to which the document should be serialized.
     * This method should not be called while the serializer is in
     * the process of serializing a document.
     */
    public void setOutputCharStream( Writer output );


    /**
     * Specifies an output format for this serializer. It the
     * serializer has already been associated with an output format,
     * it will switch to the new format. This method should not be
     * called while the serializer is in the process of serializing
     * a document.
     *
     * @param format The output format to use
     */
    public void setOutputFormat( OutputFormat format );


    /**
     * Return a {@link DocumentHandler} interface into this serializer.
     * If the serializer does not support the {@link DocumentHandler}
     * interface, it should return null.
     */
    public DocumentHandler asDocumentHandler()
        throws IOException;


    /**
     * Return a {@link ContentHandler} interface into this serializer.
     * If the serializer does not support the {@link ContentHandler}
     * interface, it should return null.
     */
    public ContentHandler asContentHandler()
        throws IOException;


    /**
     * Return a {@link DOMSerializer} interface into this serializer.
     * If the serializer does not support the {@link DOMSerializer}
     * interface, it should return null.
     */
    public DOMSerializer asDOMSerializer()
        throws IOException;


}





