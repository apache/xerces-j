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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

@SuppressWarnings("deprecation")
public class EncodingInfoTest {

    @Test
    public void testGetIANAName() {
        EncodingInfo info = new EncodingInfo("UTF-8", "UTF8", 0xffff);
        assertEquals("UTF-8", info.getIANAName());

        EncodingInfo alias = new EncodingInfo("WINDOWS-1252", "Cp1252", 0x7f);
        assertEquals("WINDOWS-1252", alias.getIANAName());
    }

    @Test
    public void testIsPrintableUsesLastPrintable() {
        EncodingInfo info = new EncodingInfo("UTF-8", "UTF8", 0x7f);
        assertTrue(info.isPrintable('\u007f'));
        assertTrue(info.isPrintable('\u00e9'));
        assertTrue(info.isPrintable('\u4e2d'));
    }

    @Test
    public void testIsPrintableUsesCharsetEncoder() {
        EncodingInfo ascii = new EncodingInfo("US-ASCII", "ASCII", 0x7f);
        assertTrue(ascii.isPrintable('A'));
        assertFalse(ascii.isPrintable('\u0080'));
        assertFalse(ascii.isPrintable('\u00e9'));

        EncodingInfo latin1 = new EncodingInfo("ISO-8859-1", "ISO8859_1", 0x7f);
        assertTrue(latin1.isPrintable('\u00e9'));
        assertFalse(latin1.isPrintable('\u4e2d'));
    }

    @Test
    public void testIsPrintableWithUnknownEncoding() {
        EncodingInfo info = new EncodingInfo("X-NOT-A-CHARSET", "X", 0x7f);
        assertFalse(info.isPrintable('\u00e9'));
    }

    @Test
    public void testGetWriterUsesJavaEncoding() throws Exception {
        EncodingInfo info = new EncodingInfo("US-ASCII", "ASCII", 0x7f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = info.getWriter(out);
        writer.write("abc");
        writer.close();
        assertEquals("abc", new String(out.toByteArray(), "US-ASCII"));
    }

    @Test
    public void testGetWriterFallsBackToUtf8() throws Exception {
        EncodingInfo info = new EncodingInfo("X-NOT-A-CHARSET", "X", 0x7f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = info.getWriter(out);
        writer.write('\u00e9');
        writer.close();
        assertEquals("\u00e9", new String(out.toByteArray(), "UTF-8"));
    }

    @Test
    public void testTestJavaEncodingName() throws Exception {
        EncodingInfo.testJavaEncodingName("UTF-8");
        assertThrows(UnsupportedEncodingException.class, new ThrowingRunnable() {
            @Override
            public void run() throws UnsupportedEncodingException {
                EncodingInfo.testJavaEncodingName("X-NOT-A-CHARSET");
            }
        });
    }
}
