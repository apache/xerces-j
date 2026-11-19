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

package org.apache.xerces.util;

/**
 * All internalized xml symbols. They can be compared using "==".
 * 
 * @author Sandy Gao, IBM
 * @version $Id$
 */
public class XMLSymbols {
    
    // public constructor.
    public XMLSymbols(){}
    
    //==========================
    // Commonly used strings
    //==========================
    
    /**
     * The empty string.
     */
    public final static String EMPTY_STRING = "";

    //==========================
    // Namespace prefixes/uris
    //==========================
    
    /**
     * The internalized "xml" prefix.
     */
    public final static String PREFIX_XML = "xml";

    /**
     * The internalized "xmlns" prefix.
     */
    public final static String PREFIX_XMLNS = "xmlns";

    //==========================
    // DTD symbols
    //==========================
    
    /** Symbol: "ANY". */
    public static final String fANYSymbol = "ANY";

    /** Symbol: "CDATA". */
    public static final String fCDATASymbol = "CDATA";

    /** Symbol: "ID". */
    public static final String fIDSymbol = "ID";

    /** Symbol: "IDREF". */
    public static final String fIDREFSymbol = "IDREF";

    /** Symbol: "IDREFS". */
    public static final String fIDREFSSymbol = "IDREFS";

    /** Symbol: "ENTITY". */
    public static final String fENTITYSymbol = "ENTITY";

    /** Symbol: "ENTITIES". */
    public static final String fENTITIESSymbol = "ENTITIES";

    /** Symbol: "NMTOKEN". */
    public static final String fNMTOKENSymbol = "NMTOKEN";

    /** Symbol: "NMTOKENS". */
    public static final String fNMTOKENSSymbol = "NMTOKENS";

    /** Symbol: "NOTATION". */
    public static final String fNOTATIONSymbol = "NOTATION";

    /** Symbol: "ENUMERATION". */
    public static final String fENUMERATIONSymbol = "ENUMERATION";

    /** Symbol: "#IMPLIED. */
    public static final String fIMPLIEDSymbol = "#IMPLIED";

    /** Symbol: "#REQUIRED". */
    public static final String fREQUIREDSymbol = "#REQUIRED";

    /** Symbol: "#FIXED". */
    public static final String fFIXEDSymbol = "#FIXED";
    
    
}
