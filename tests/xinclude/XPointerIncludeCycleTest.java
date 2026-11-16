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

package xinclude;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.xni.parser.XMLInputSource;

import xni.Writer;

/**
 * Tests that an XPointer which selects a fragment that does not contain an
 * include pointing back to the including document does not recurse forever.
 */
public class XPointerIncludeCycleTest extends TestCase {

    private static final String DOCUMENT =
        "tests/xinclude/tests/xpointer-cycle-a.xml";

    private static final String XINCLUDE_FEATURE =
        "http://apache.org/xml/features/xinclude";

    public void testIncludeCycleOutsideSelectedFragment() throws Exception {
        String output = parse();

        assertEquals(1, countOccurrences(output, "a chapter"));
        assertEquals(1, countOccurrences(output, "b chapter"));
        // Includes inside the selected fragment must still be expanded.
        assertEquals(1, countOccurrences(output, "nested content"));
    }

    private String parse() throws Exception {
        XIncludeAwareParserConfiguration configuration =
            new XIncludeAwareParserConfiguration();
        configuration.setFeature(XINCLUDE_FEATURE, true);
        StringWriter output = new StringWriter();
        Writer writer = new Writer(configuration);
        writer.setOutput(output);
        writer.parse(new XMLInputSource(null, DOCUMENT, null));
        return output.toString();
    }

    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
