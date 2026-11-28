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

import java.nio.charset.Charset;
import java.util.Enumeration;

import junit.framework.TestCase;

public class EncodingMapTest extends TestCase {

    public void testIANAToJavaValuesHaveJavaToIANAMappings() {
        Enumeration names = EncodingMap.fIANA2JavaMap.keys();
        while (names.hasMoreElements()) {
            String ianaName = (String) names.nextElement();
            String javaName = EncodingMap.getIANA2JavaMapping(ianaName);
            assertNotNull("No Java to IANA mapping for " + javaName + " used by " + ianaName,
                    EncodingMap.getJava2IANAMapping(javaName));
        }
    }

    public void testJavaToIANAMappingsHaveIANAToJavaMappings() {
        Enumeration names = EncodingMap.fJava2IANAMap.keys();
        while (names.hasMoreElements()) {
            String javaName = (String) names.nextElement();
            String ianaName = EncodingMap.getJava2IANAMapping(javaName);
            assertNotNull("No IANA to Java mapping for " + ianaName + " used by " + javaName,
                    EncodingMap.getIANA2JavaMapping(ianaName));
        }
    }

    public void testISO885915AliasRoundTrip() {
        String javaName = EncodingMap.getIANA2JavaMapping("iso_8859-15");
        assertNotNull(javaName);
        assertEquals(Charset.forName("ISO-8859-15"), Charset.forName(javaName));
        assertEquals("ISO-8859-15", EncodingMap.getJava2IANAMapping(javaName));
    }

    public void testJavaAliasesRoundTripToEquivalentCharsets() {
        String[] aliases = {"ASCII", "ISO8859_15_FDIS", "Cp1140", "Cp1252"};
        for (String alias : aliases) {
            String ianaName = EncodingMap.getJava2IANAMapping(alias);
            assertNotNull(alias, ianaName);
            String javaName = EncodingMap.getIANA2JavaMapping(ianaName);
            assertNotNull(ianaName, javaName);
            assertEquals(alias, Charset.forName(alias), Charset.forName(javaName));
        }
    }

    public void testJavaCharsetNamesAndAliases() {
        String[][] mappings = {
            {"Unicode", "UTF-16"},
            {"UTF_16", "UTF-16"},
            {"UnicodeBigUnmarked", "UTF-16BE"},
            {"UnicodeLittleUnmarked", "UTF-16LE"},
            {"UTF_16LE", "UTF-16LE"},
            {"Latin1", "ISO-8859-1"},
            {"UTF-8", "UTF-8"},
            {"US-ASCII", "US-ASCII"},
            {"ISO-8859-15", "ISO-8859-15"},
            {"windows-1252", "WINDOWS-1252"},
            {"IBM1047", "IBM1047"},
            {"IBM01140", "IBM01140"}
        };
        for (String[] mapping : mappings) {
            String javaName = mapping[0];
            assertEquals(javaName, mapping[1], EncodingMap.getJava2IANAMapping(javaName));
        }
    }

    public void testGetJava2IANAMapping() {
        assertEquals("US-ASCII", EncodingMap.getJava2IANAMapping("ASCII"));
        assertEquals("IBM1047", EncodingMap.getJava2IANAMapping("Cp1047"));
        assertEquals("IBM01140", EncodingMap.getJava2IANAMapping("Cp1140"));
        assertEquals("IBM01149", EncodingMap.getJava2IANAMapping("Cp1149"));
        assertEquals("WINDOWS-1252", EncodingMap.getJava2IANAMapping("Cp1252"));
        assertEquals("WINDOWS-1258", EncodingMap.getJava2IANAMapping("Cp1258"));
        assertEquals("TIS-620", EncodingMap.getJava2IANAMapping("TIS620"));
        assertEquals("ISO-8859-15", EncodingMap.getJava2IANAMapping("ISO8859_15"));
        assertEquals("ISO-8859-15", EncodingMap.getJava2IANAMapping("ISO8859_15_FDIS"));
        assertEquals("UTF-16", EncodingMap.getJava2IANAMapping("UTF-16"));
    }
}
