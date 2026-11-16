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
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

import xni.Writer;

/**
 * Tests that mutually recursive XIncludes are detected and reported as fatal
 * errors instead of causing unbounded recursion.
 */
public class XIncludeCycleTest extends TestCase implements XMLErrorHandler {

    private static final String DOCUMENT =
        "tests/xinclude/tests/xinclude-cycle-a.xml";

    private static final String XINCLUDE_FEATURE =
        "http://apache.org/xml/features/xinclude";

    private static final String ERROR_HANDLER =
        "http://apache.org/xml/properties/internal/error-handler";

    private boolean fRecursiveIncludeReported;

    public void testRecursiveIncludeWithoutXPointerIsReported() throws Exception {
        fRecursiveIncludeReported = false;

        XIncludeAwareParserConfiguration configuration =
            new XIncludeAwareParserConfiguration();
        configuration.setFeature(XINCLUDE_FEATURE, true);
        StringWriter output = new StringWriter();
        Writer writer = new Writer(configuration);
        writer.setOutput(output);
        configuration.setProperty(ERROR_HANDLER, this);

        try {
            writer.parse(new XMLInputSource(null, DOCUMENT, null));
            fail("Expected the recursive include to be reported");
        } catch (XNIException expected) {
            // The recursive include is a fatal error.
        }

        assertTrue("Recursive include was not reported",
            fRecursiveIncludeReported);
    }

    public void warning(String domain, String key, XMLParseException exception) {
    }

    public void error(String domain, String key, XMLParseException exception) {
    }

    public void fatalError(String domain, String key, XMLParseException exception) {
        if ("RecursiveInclude".equals(key)
                || (exception.getMessage() != null
                    && exception.getMessage().contains("Recursive include"))) {
            fRecursiveIncludeReported = true;
        }
    }
}
