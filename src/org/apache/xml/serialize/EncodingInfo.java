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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.xerces.util.EncodingMap;

/**
 * This class represents an encoding.
 * 
 * @deprecated This class was deprecated in Xerces 2.9.0.
 * New applications should use the DOM Level 3 LSSerializer or JAXP's Transformation 
 * API for XML (TrAX) for serializing XML. See the Xerces documentation for more 
 * information.
 * @version $Id$
 */
@Deprecated
public class EncodingInfo {
    
    // name of encoding as registered with IANA;
    // preferably a MIME name, but aliases are fine too.
    String ianaName;
    String javaName;
    int lastPrintable;
    
    // The CharsetEncoder with which we test unusual characters.
    private CharsetEncoder fCharsetEncoder = null;
    
    // Is the charset encoder usable or available.
    private boolean fHaveTriedCharsetEncoder = false;

    /**
     * Creates new <code>EncodingInfo</code> instance.
     *
     * @param ianaName name of encoding as registered with IANA (preferably a MIME name, but aliases are fine too)
     * @param javaName not actually used
     * @param lastPrintable the integer value of the last printable char in the character encoding
     */
    public EncodingInfo(String ianaName, String javaName, int lastPrintable) {
        this.ianaName = ianaName;
        this.javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        this.lastPrintable = lastPrintable;
    }

    /**
     * Returns a MIME charset name of this encoding.
     * @return a string value of the MIME charset name for this encoding
     */
    public String getIANAName() {
        return this.ianaName;
    }

    /**
     * Returns a writer for this encoding based on
     * an output stream.
     *
     * @param output the output stream to use
     * @return a suitable writer
     * @throws UnsupportedEncodingException if there is no convertor to support this encoding
     */
    public Writer getWriter(OutputStream output)
        throws UnsupportedEncodingException {
        // this should always be true!
        if (javaName != null) 
            return new OutputStreamWriter(output, javaName);
        javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        if(javaName == null) 
            // use UTF-8 as preferred encoding
            return new OutputStreamWriter(output, "UTF8");
        return new OutputStreamWriter(output, javaName);
    }
    
    /**
     * Checks whether the specified character is printable or not in this encoding.
     *
     * @param ch a code point (0-0x10ffff)
     * @return true if the char is printable with this encoding
     */
    public boolean isPrintable(char ch) {
        if (ch <= this.lastPrintable) {
            return true;
        }
        return isPrintable0(ch);
    }
    
    /**
     * Checks whether the specified character is printable or not in this encoding.
     *
     * @param ch a code point (0-0x10ffff)
     * @return true if the char is printable with this encoding
     */
    private boolean isPrintable0(char ch) {
        // Attempt to get a CharsetEncoder for this encoding.
        if (fCharsetEncoder == null && !fHaveTriedCharsetEncoder) {
            fHaveTriedCharsetEncoder = true;
            if (javaName != null) {
                try {
                    Charset charset = Charset.forName(javaName);
                    if (charset.canEncode()) {
                        fCharsetEncoder = charset.newEncoder();
                    }
                }
                catch (IllegalArgumentException e) {
                    // The charset is not supported; nothing more we can do.
                }
            }
        }
        // Attempt to use the CharsetEncoder to determine whether the character is printable.
        if (fCharsetEncoder != null) {
            return fCharsetEncoder.canEncode(ch);
        }
        return false;
    }

    /**
     * Checks that a charset is recognized by this JDK.
     *
     * @param name the charset name
     * @throws UnsupportedEncodingException if the charset name is not recognized by the JDK
     */
    public static void testJavaEncodingName(String name)  throws UnsupportedEncodingException {
        final byte [] bTest = {(byte)'v', (byte)'a', (byte)'l', (byte)'i', (byte)'d'};
        String s = new String(bTest, name);
    }
}
