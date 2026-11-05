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
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.apache.xerces.parsers.XIncludeParserConfiguration;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

import xni.Writer;

/**
 * Tests malformed XPointer expressions.
 */
public class MalformedXPointerTest extends TestCase {

    // Timeout in milliseconds.
    private static final long PARSE_TIMEOUT = 10000;

    public void testUnbalancedOpenParenthesisDoesNotHang() throws Exception {
        assertMalformedXPointerRejected("tests/xinclude/tests/test42.xml");
    }

    public void testExtraWrappingParenthesesAreRejected() throws Exception {
        assertMalformedXPointerRejected("tests/xinclude/tests/test43.xml");
    }

    private void assertMalformedXPointerRejected(final String testFile)
            throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        Thread parserThread = new Thread(new Runnable() {
            public void run() {
                try {
                    XMLParserConfiguration parserConfig = new XIncludeParserConfiguration();
                    Writer writer = new Writer(parserConfig);
                    writer.setOutput(new StringWriter());
                    writer.parse(new XMLInputSource(null, testFile, null));
                    failure.set(new AssertionError(
                            "Malformed XPointer was accepted: " + testFile));
                } catch (XNIException e) {
                    // Expected
                } catch (Throwable t) {
                    failure.set(t);
                }
            }
        });
        parserThread.setDaemon(true);
        parserThread.start();
        parserThread.join(PARSE_TIMEOUT);

        if (parserThread.isAlive()) {
            fail("Parsing the malformed XPointer did not terminate within "
                    + PARSE_TIMEOUT + " ms");
        }
        if (failure.get() != null) {
            Throwable cause = failure.get();
            if (cause instanceof AssertionError) {
                throw (AssertionError) cause;
            }
            throw new AssertionError(cause.toString());
        }
    }
}
