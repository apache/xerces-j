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

import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class EncodingsTest extends TestCase {

    public void testNullEncodingUsesDefault() throws Exception {
        EncodingInfo info = Encodings.getEncodingInfo(null, false);
        assertEquals("UTF-8", info.getIANAName());
        assertTrue(info.isPrintable('\uffff'));
    }

    public void testIANAEncoding() throws Exception {
        EncodingInfo ascii = Encodings.getEncodingInfo("US-ASCII", false);
        assertEquals("US-ASCII", ascii.getIANAName());
        assertFalse(ascii.isPrintable('\u00e9'));

        EncodingInfo utf8 = Encodings.getEncodingInfo("UTF-8", false);
        assertEquals("UTF-8", utf8.getIANAName());
        assertTrue(utf8.isPrintable('\u00e9'));
        assertTrue(utf8.isPrintable('\uffff'));
    }

    public void testIANAEncodingNamesAreCaseInsensitive() throws Exception {
        EncodingInfo lowerCase = Encodings.getEncodingInfo("utf-8", false);
        assertEquals("UTF-8", lowerCase.getIANAName());
        assertTrue(lowerCase.isPrintable('\uffff'));

        EncodingInfo mixedCase = Encodings.getEncodingInfo("Utf-8", false);
        assertEquals("UTF-8", mixedCase.getIANAName());
        assertSame(lowerCase, mixedCase);
    }

    public void testJavaEncodingNameNotAllowed() {
        try {
            Encodings.getEncodingInfo("UTF8", false);
            fail("Java encoding name should not be allowed");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    // https://issues.apache.org/jira/projects/XERCESJ/issues/XERCESJ-762
    public void testUnicodeLittle() throws Exception {
        EncodingInfo unicodeLittle = Encodings.getEncodingInfo("UnicodeLittle", true);
        assertEquals("UTF-16LE", unicodeLittle.getIANAName());
        assertTrue(unicodeLittle.isPrintable('\uffff'));
    }

    public void testJavaEncodingNameAllowed() throws Exception {
        EncodingInfo utf8 = Encodings.getEncodingInfo("UTF8", true);
        assertEquals("UTF-8", utf8.getIANAName());
        assertTrue(utf8.isPrintable('\uffff'));

        EncodingInfo windows1252 = Encodings.getEncodingInfo("Cp1252", true);
        assertEquals("WINDOWS-1252", windows1252.getIANAName());
        assertFalse(windows1252.isPrintable('\u4e2d'));
    }

    public void testEncodingInfoIsCached() throws Exception {
        assertSame(Encodings.getEncodingInfo("US-ASCII", false),
                Encodings.getEncodingInfo("US-ASCII", false));
    }

    public void testUnknownEncoding() {
        try {
            Encodings.getEncodingInfo("no-such-encoding", false);
            fail("Unknown encoding should not be recognized");
        } catch (UnsupportedEncodingException expected) {
        }
        try {
            Encodings.getEncodingInfo("no-such-encoding", true);
            fail("Unknown encoding should not be recognized");
        } catch (UnsupportedEncodingException expected) {
        }
    }
}
