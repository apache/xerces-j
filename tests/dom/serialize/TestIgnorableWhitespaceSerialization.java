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

package dom.serialize;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * Tests serialization of ignorable whitespace in element content.
 */
public class TestIgnorableWhitespaceSerialization extends TestCase {

    public void testIgnorableWhitespaceIsPreservedWhenNotIndenting() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer();
        OutputFormat format = new OutputFormat();
        format.setIndenting(false);
        serializer.setOutputFormat(format);
        serializer.setOutputByteStream(out);

        serializer.startDocument();
        serializer.startElement("root", null, "root", null);
        serializer.ignorableWhitespace("  \n".toCharArray(), 0, 3);
        serializer.characters("alpha".toCharArray(), 0, 5);
        serializer.ignorableWhitespace(" \t".toCharArray(), 0, 2);
        serializer.characters("beta".toCharArray(), 0, 4);
        serializer.ignorableWhitespace("\n  ".toCharArray(), 0, 3);
        serializer.endElement("root", null, "root");
        serializer.endDocument();

        String output = out.toString("UTF-8");
        String expected = "<root>  \nalpha \tbeta\n  </root>";
        assertTrue("Expected '" + expected + "' in: " + output,
                   output.indexOf(expected) >= 0);
    }
}
