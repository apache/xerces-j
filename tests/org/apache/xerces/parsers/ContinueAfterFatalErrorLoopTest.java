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

package org.apache.xerces.parsers;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ContinueAfterFatalErrorLoopTest extends TestCase {

    public void testReferenceInPrologDoesNotLoop() throws Exception {
        assertParseCompletes("&amp;<root/>");
    }

    public void testMarkupNotRecognizedInPrologDoesNotLoop() throws Exception {
        assertParseCompletes("<#root/>");
    }

    public void testMarkupNotRecognizedInTrailingMiscDoesNotLoop() throws Exception {
        assertParseCompletes("<root/><#");
    }

    private void assertParseCompletes(final String xml) throws Exception {
        final Exception[] failure = new Exception[1];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    SAXParser parser = new SAXParser();
                    parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
                    parser.setErrorHandler(new DefaultHandler() {
                        public void fatalError(SAXParseException e) throws SAXException {
                        }
                    });
                    parser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
                }
                catch (Exception e) {
                    failure[0] = e;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        thread.join(1000);
        if (thread.isAlive()) {
            fail("parser did not complete for input: " + xml);
        }
        if (failure[0] != null) {
            throw failure[0];
        }
    }
}
