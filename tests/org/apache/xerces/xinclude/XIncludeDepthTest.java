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

package org.apache.xerces.xinclude;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;

import junit.framework.TestCase;

import org.apache.xerces.parsers.XIncludeParserConfiguration;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

/**
 * Tests that deeply nested XInclude processing does not exhaust the stack.
 */
public class XIncludeDepthTest extends TestCase {

    private static final int DEPTH = 5000;
    private static final int SEARCH_DEPTH = 100000;
    private static final String ERROR_HANDLER =
            "http://apache.org/xml/properties/internal/error-handler";

    public void testRecursiveIncludeSearchWalksDeepParentChain() {
        XIncludeHandler handler = new XIncludeHandler();
        for (int i = 0; i < SEARCH_DEPTH; i++) {
            XIncludeHandler child = new XIncludeHandler();
            child.setParent(handler);
            handler = child;
        }
        assertFalse(handler.searchForRecursiveIncludes(
                "http://example.com/not-included.xml"));
    }

    public void testIncludeChainBelowMaximumDepthIsExpanded()
            throws Exception {
        File directory = Files.createTempDirectory("xinclude-depth").toFile();
        try {
            parse(createIncludeChain(directory, 50));
        }
        finally {
            delete(directory);
        }
    }

    public void testDeepIncludeChainDoesNotOverflowStack() throws Exception {
        File directory = Files.createTempDirectory("xinclude-depth").toFile();
        try {
            File root = createIncludeChain(directory, DEPTH);
            try {
                parse(root);
                fail("Expected an include depth error");
            }
            catch (XNIException expected) {
                assertFalse(expected.getCause() instanceof StackOverflowError);
            }
        }
        finally {
            delete(directory);
        }
    }

    private void parse(File root) throws Exception {
        XMLParserConfiguration configuration =
                new XIncludeParserConfiguration();
        configuration.setFeature(
                "http://xml.org/sax/features/namespaces", true);
        configuration.setProperty(ERROR_HANDLER, new XMLErrorHandler() {
            public void warning(String domain, String key,
                    XMLParseException exception) {
            }
            public void error(String domain, String key,
                    XMLParseException exception) {
            }
            public void fatalError(String domain, String key,
                    XMLParseException exception) {
            }
        });
        configuration.parse(new XMLInputSource(
                null, root.toURI().toString(), null));
    }

    private File createIncludeChain(File directory, int depth)
            throws Exception {
        String xincludeNamespace = "http://www.w3.org/2001/XInclude";
        File root = null;
        for (int i = 0; i < depth; i++) {
            File file = new File(directory, i + ".xml");
            if (i == 0) {
                root = file;
            }
            Writer writer = new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8");
            try {
                writer.write("<doc xmlns:xi='" + xincludeNamespace + "'>");
                if (i + 1 < depth) {
                    writer.write("<xi:include href='" + (i + 1) + ".xml'/>");
                }
                else {
                    writer.write("<leaf/>");
                }
                writer.write("</doc>");
            }
            finally {
                writer.close();
            }
        }
        return root;
    }

    private void delete(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                delete(children[i]);
            }
        }
        file.delete();
    }
}
