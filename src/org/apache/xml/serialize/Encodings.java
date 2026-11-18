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

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.xerces.util.EncodingMap;

/**
 * Provides information about encodings. Depends on the Java runtime
 * to provides writers for the different encodings, but can be used
 * to override encoding names and provide the last printable character
 * for each encoding.
 *
 * @deprecated This class was deprecated in Xerces 2.9.0.
 * New applications should use the DOM Level 3 LSSerializer or JAXP's Transformation
 * API for XML (TrAX) for serializing XML. See the Xerces documentation for more 
 * information.
 * @version $Id$
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 */
@Deprecated
public class Encodings {

    /**
     * The last printable character for unknown encodings.
     */
    private static final int DEFAULT_LAST_PRINTABLE = 0x7F;

    // last printable character for Unicode-compatible encodings
    private static final int LAST_PRINTABLE_UNICODE = 0xffff;
    // unicode-compliant encodings; can express plane 0
    private static final String[] UNICODE_ENCODINGS = {
        "Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-8", "UTF-16",
    };
    // default (Java) encoding if none supplied:
    private static final String DEFAULT_ENCODING = "UTF8";

    // note that the size of this Hashtable
    // is bounded by the number of encodings recognized by EncodingMap;
    // therefore it poses no static mutability risk.
    private static Hashtable _encodings = new Hashtable();

    /**
     * @param encoding a MIME charset name, or null
     */
    static EncodingInfo getEncodingInfo(String encoding, boolean allowJavaNames) throws UnsupportedEncodingException {
        EncodingInfo eInfo = null;
        if (encoding == null) {
            if((eInfo = (EncodingInfo)_encodings.get(DEFAULT_ENCODING)) != null) 
                return eInfo;
            eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(DEFAULT_ENCODING), DEFAULT_ENCODING, LAST_PRINTABLE_UNICODE);
            _encodings.put(DEFAULT_ENCODING, eInfo);
            return eInfo;
        }
        String javaEncodingName = EncodingMap.getIANA2JavaMapping(encoding);
        String ianaEncodingName = encoding;
        if (javaEncodingName == null) {
            // IANA charset names are case-insensitive.
            String upperCaseEncoding = encoding.toUpperCase(Locale.ENGLISH);
            javaEncodingName = EncodingMap.getIANA2JavaMapping(upperCaseEncoding);
            if (javaEncodingName != null) {
                ianaEncodingName = upperCaseEncoding;
            }
        }
        if (javaEncodingName == null) {
            // see if the encoding passed in is a Java encoding name.
            if (allowJavaNames) {
                EncodingInfo.testJavaEncodingName(encoding);
                if ((eInfo = (EncodingInfo)_encodings.get(encoding)) != null) 
                    return eInfo;
                // is it known to be unicode-compliant?
                int i = 0;
                for(; i<UNICODE_ENCODINGS.length; i++) {
                    if(UNICODE_ENCODINGS[i].equalsIgnoreCase(encoding)) {
                        eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, LAST_PRINTABLE_UNICODE);
                        break;
                    }
                }
                if(i == UNICODE_ENCODINGS.length) {
                    eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, DEFAULT_LAST_PRINTABLE);
                }
                _encodings.put(encoding, eInfo); 
                return eInfo;
            }
            throw new UnsupportedEncodingException(encoding);
        }
        if ((eInfo = (EncodingInfo)_encodings.get(javaEncodingName)) != null)
            return eInfo;
        // have to create one...
        // is it known to be unicode-compliant?
        int i=0;
        for(; i<UNICODE_ENCODINGS.length; i++) {
            if(UNICODE_ENCODINGS[i].equalsIgnoreCase(javaEncodingName)) {
                eInfo = new EncodingInfo(ianaEncodingName, javaEncodingName, LAST_PRINTABLE_UNICODE);
                break;
            }
        }
        if(i == UNICODE_ENCODINGS.length) {
            eInfo = new EncodingInfo(ianaEncodingName, javaEncodingName, DEFAULT_LAST_PRINTABLE);
        }
        _encodings.put(javaEncodingName, eInfo);
        return eInfo;
    }
}
