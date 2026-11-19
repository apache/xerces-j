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

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;
import org.apache.xerces.impl.io.UTF16Reader;
import org.apache.xerces.util.XMLChar;

/**
 * This program tests the customized UTF-16 reader for the parser,
 * comparing it with the Java UTF-16 reader.
 *
 * @version $Id$
 */
public class UTF16 extends TestCase {

    public void testUTF16DecoderBigEndian() throws Exception {
        testUTF16Decoder(true);
    }

    public void testUTF16DecoderLittleEndian() throws Exception {
        testUTF16Decoder(false);
    }

    private void testUTF16Decoder(boolean isBigEndian) throws Exception {
        final int BLOCK_READ_SIZE = 2048;
        final String encoding = isBigEndian ? "UnicodeBig" : "UnicodeLittle";
        final String shortName = isBigEndian ? "BE" : "LE";

        // Test Java reference implementation of UTF-16 decoder
        // test character by character
        InputStream stream = new UTF16Producer(isBigEndian);
        Reader reader = new InputStreamReader(stream, encoding);
        testCharByChar(reader);
        reader.close();

        // test character array
        stream = new UTF16Producer(isBigEndian);
        reader = new InputStreamReader(stream, encoding);
        testCharArray(reader, BLOCK_READ_SIZE);
        reader.close();

        // Test custom implementation of UTF-16 decoder
        // test character by character
        stream = new UTF16Producer(isBigEndian);
        reader = new UTF16Reader(stream, isBigEndian);
        testCharByChar(reader);
        reader.close();

        // test character array
        stream = new UTF16Producer(isBigEndian);
        reader = new UTF16Reader(stream, isBigEndian);
        testCharArray(reader, BLOCK_READ_SIZE);
        reader.close();
    }

    private void testCharByChar(Reader reader) throws Exception {
        for (int i = 0; i < 0xD800; i++) {
            assertEquals("character 0x" + Integer.toHexString(i) + " mismatch", i, reader.read());
        }
        for (int i = 0xE000; i < 0xFFFE; i++) {
            assertEquals("character 0x" + Integer.toHexString(i) + " mismatch", i, reader.read());
        }
        for (int i = 0x10000; i < 0x110000; i++) {
            int uuuuu = (i >> 16) & 0x001F;
            int wwww = uuuuu - 1;
            int zzzz = (i >> 12) & 0x000F;
            int yyyyyy = (i >> 6) & 0x003F;
            int xxxxxx = i & 0x003F;
            int hs = 0xD800 | (wwww << 6) | (zzzz << 2) | (yyyyyy >> 4);
            int ls = 0xDC00 | ((yyyyyy << 6) & 0x03C0) | xxxxxx;
            assertEquals("high surrogate mismatch", hs, reader.read());
            assertEquals("low surrogate mismatch", ls, reader.read());
        }
        assertEquals("Expected EOF", -1, reader.read());
    }

    private void testCharArray(Reader reader, int size) throws Exception {
        char[] ch = new char[size];
        int count = 0;
        int position = 0;

        for (int i = 0; i < 0xD800; i++) {
            if (position == count) {
                count = reader.read(ch, 0, ch.length);
                position = 0;
            }
            int c = ch[position++];
            assertEquals("character 0x" + Integer.toHexString(i) + " mismatch", i, c);
        }
        for (int i = 0xE000; i < 0xFFFE; i++) {
            if (position == count) {
                count = reader.read(ch, 0, ch.length);
                position = 0;
            }
            int c = ch[position++];
            assertEquals("character 0x" + Integer.toHexString(i) + " mismatch", i, c);
        }
        for (int i = 0x10000; i < 0x110000; i++) {
            int uuuuu = (i >> 16) & 0x001F;
            int wwww = uuuuu - 1;
            int zzzz = (i >> 12) & 0x000F;
            int yyyyyy = (i >> 6) & 0x003F;
            int xxxxxx = i & 0x003F;
            int hs = 0xD800 | (wwww << 6) | (zzzz << 2) | (yyyyyy >> 4);
            int ls = 0xDC00 | ((yyyyyy << 6) & 0x03C0) | xxxxxx;
            if (position == count) {
                count = reader.read(ch, 0, ch.length);
                position = 0;
            }
            int c = ch[position++];
            assertEquals("high surrogate mismatch", hs, c);
            if (position == count) {
                count = reader.read(ch, 0, ch.length);
                position = 0;
            }
            c = ch[position++];
            assertEquals("low surrogate mismatch", ls, c);
        }
        if (position == count) {
            count = reader.read(ch, 0, ch.length);
            position = 0;
        }
        assertEquals("Expected EOF", -1, count);
    }

    //
    // Classes
    //

    /**
     * This classes produces a stream of UTF-16 byte sequences for all 
     * valid Unicode characters.
     */
    public static class UTF16Producer
        extends InputStream {

        //
        // Data
        //

        /** The current code point. */
        private int fCodePoint;

        /** The current byte of the current code point. */
        private int fByte;

        /** Endianness. */
        private final boolean fIsBigEndian;

        //
        // Constructors
        //

        public UTF16Producer(boolean isBigEndian) {
            fIsBigEndian = isBigEndian;
        }

        //
        // InputStream methods
        //

        /** Reads the next character. */
        public int read() throws IOException {

            if (fCodePoint < 0xFFFE) {
                // skip surrogate blocks
                if (fCodePoint == 0xD800) {
                    fCodePoint = 0xE000;
                }
                switch (fByte) {
                    case 0: {
                        final int b;
                        if (fIsBigEndian) {
                            b = fCodePoint >> 8;
                        }
                        else {
                            b = fCodePoint & 0xff;
                        }
                        fByte++;
                        return b;
                    }
                    case 1: {
                        final int b;
                        if (fIsBigEndian) {
                            b = fCodePoint & 0xff;
                        }
                        else {
                            b = fCodePoint >> 8;
                        }
                        fCodePoint++;
                        fByte = 0;
                        return b;
                    }
                    default: {
                        throw new RuntimeException("byte "+fByte+" of 2 byte UTF-8 sequence");
                    }
                }
            }
            if (fCodePoint == 0xFFFE) {
                fCodePoint = 0x10000;
            }
            if (fCodePoint < 0x110000) {
                switch (fByte) {
                    case 0: {
                        final int b;
                        if (fIsBigEndian) {
                            b = XMLChar.highSurrogate(fCodePoint) >> 8;
                        }
                        else {
                            b = XMLChar.highSurrogate(fCodePoint) & 0xff;
                        }
                        fByte++;
                        return b;
                    }
                    case 1: {
                        final int b;
                        if (fIsBigEndian) {
                            b = XMLChar.highSurrogate(fCodePoint) & 0xff;
                        }
                        else {
                            b = XMLChar.highSurrogate(fCodePoint) >> 8;
                        }
                        fByte++;
                        return b;
                    }
                    case 2: {
                        final int b;
                        if (fIsBigEndian) {
                            b = XMLChar.lowSurrogate(fCodePoint) >> 8;
                        }
                        else {
                            b = XMLChar.lowSurrogate(fCodePoint) & 0xff;
                        }
                        fByte++;
                        return b;
                    }
                    case 3: {
                        final int b;
                        if (fIsBigEndian) {
                            b = XMLChar.lowSurrogate(fCodePoint) & 0xff;
                        }
                        else {
                            b = XMLChar.lowSurrogate(fCodePoint) >> 8;
                        }
                        fCodePoint++;
                        fByte = 0;
                        return b;
                    }
                    default: {
                        throw new RuntimeException("byte "+fByte+" of 2 byte UTF-8 sequence");
                    }
                }
            }
            return -1;
        }
    }
}
