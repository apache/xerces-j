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

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.XIncludeParserConfiguration;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

import xni.Writer;

public class Test extends TestCase implements XMLErrorHandler {
    protected static final String NAMESPACES_FEATURE_ID =
        "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION_FEATURE_ID =
        "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema";
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String ERROR_HANDLER =
        "http://apache.org/xml/properties/internal/error-handler";

    private static final int NUM_TESTS = 41;
    private static final boolean[] TEST_RESULTS = new boolean[] {
        true, true, true, true, true, true, false, true, false, true,
        false, false, false, false, true, true, true, false, true, true,
        true, false, true, false, false, false, true, true, false, true,
        true, false, true, true, true, true, true, true, false, false,
        true,
    };

    private Writer fWriter;
    private PrintWriter fOutputWriter;

    public Test(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        XMLParserConfiguration parserConfig = new XIncludeParserConfiguration();
        parserConfig.setFeature(NAMESPACES_FEATURE_ID, true);
        parserConfig.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
        parserConfig.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
        fWriter = new Writer(parserConfig);
        parserConfig.setProperty(ERROR_HANDLER, this);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        for (int i = 1; i <= NUM_TESTS; i++) {
            String num = i < 10 ? "0" + i : String.valueOf(i);
            suite.addTest(new Test("test" + num));
        }
        return suite;
    }

    protected void runTest() throws Throwable {
        int testnum = Integer.parseInt(getName().substring(4));
        runTest(testnum);
    }

    private void runTest(int testnum) {
        String testname = "tests/xinclude/tests/test";
        String expectedOutputFilename = "tests/xinclude/output/test";
        if (testnum < 10) {
            testname += "0" + testnum;
            expectedOutputFilename += "0" + testnum;
        }
        else {
            testname += testnum;
            expectedOutputFilename += testnum;
        }
        testname += ".xml";
        if (TEST_RESULTS[testnum - 1]) {
            expectedOutputFilename += ".xml";
        }
        else {
            expectedOutputFilename += ".txt";
        }

        boolean passed = true;
        StringBuffer buffer = null;
        try {
            java.io.Writer myWriter = new StringWriter();
            buffer = ((StringWriter)myWriter).getBuffer();
            fOutputWriter = new PrintWriter(myWriter);
            fWriter.setOutput(myWriter);
            fWriter.parse(new XMLInputSource(null, testname, null));
        }
        catch (XNIException e) {
            passed = false;
        }
        catch (IOException e) {
            fail("Unexpected IO problem: " + e.getMessage());
        }

        String results = stripUserDir(buffer);
        processTestResults(passed, TEST_RESULTS[testnum - 1], expectedOutputFilename, results);
    }

    private void processTestResults(
        boolean passed,
        boolean expectedPass,
        String expectedOutputFile,
        String results) {
        try {
            assertEquals("Test " + getName() + ": pass/fail mismatch",
                expectedPass, passed);
            assertTrue("Test " + getName() + ": output mismatch",
                compareOutput(new FileReader(expectedOutputFile),
                    new StringReader(results)));
        }
        catch (IOException e) {
            fail("Unexpected IO problem attempting to verify results: " + e.getMessage());
        }
    }

    public void error(String domain, String key, XMLParseException exception)
        throws XNIException {
        printError("Error", exception);
    }

    public void fatalError(
        String domain,
        String key,
        XMLParseException exception)
        throws XNIException {
        printError("Fatal Error", exception);
    }

    public void warning(String domain, String key, XMLParseException exception)
        throws XNIException {
        printError("Warning", exception);
    }

    protected void printError(String type, XMLParseException ex) {
        fOutputWriter.print("[");
        fOutputWriter.print(type);
        fOutputWriter.print("] ");
        String systemId = ex.getExpandedSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            fOutputWriter.print(systemId);
        }
        fOutputWriter.print(':');
        fOutputWriter.print(ex.getLineNumber());
        fOutputWriter.print(':');
        fOutputWriter.print(ex.getColumnNumber());
        fOutputWriter.println();
        fOutputWriter.flush();
    }

    protected boolean compareOutput(Reader expected, Reader actual)
        throws IOException {
        LineNumberReader expectedOutput = new LineNumberReader(expected);
        LineNumberReader actualOutput = new LineNumberReader(actual);

        while (expectedOutput.ready() && actualOutput.ready()) {
            String expectedLine = expectedOutput.readLine();
            String actualLine = actualOutput.readLine();
            if (!expectedLine.equals(actualLine)) {
                return false;
            }
        }
        if (expectedOutput.ready() && !actualOutput.ready()) {
            String expectedLine = expectedOutput.readLine();
            if (expectedLine != null) {
                return false;
            }
        }
        else if (!expectedOutput.ready() && actualOutput.ready()) {
            String actualLine = actualOutput.readLine();
            if (actualLine != null) {
                return false;
            }
        }

        expectedOutput.close();
        actualOutput.close();
        return true;
    }

    private String stripUserDir(StringBuffer buf) {
        String userDir = System.getProperty("user.dir");
        String userURI = "file://";
        if (userDir.charAt(0) != '/') {
            userURI += "/";
        }
        userURI += userDir.replace('\\', '/');
        String str = getPathWithoutEscapes(buf.toString());

        int start = 0, end = 0;
        while ((start = str.indexOf(userURI, start)) != -1) {
            end = start + userURI.length();
            str = str.substring(0, start) + str.substring(end + 1);
        }

        while ((start = str.indexOf(userDir, start)) != -1) {
            end = start + userDir.length();
            str = str.substring(0, start) + str.substring(end + 1);
        }
        return str;
    }

    private static String getPathWithoutEscapes(String origPath) {
        if (origPath != null && origPath.length() != 0 && origPath.indexOf('%') != -1) {
            StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
            StringBuffer result = new StringBuffer(origPath.length());
            int size = tokenizer.countTokens();
            result.append(tokenizer.nextToken());
            for(int i = 1; i < size; ++i) {
                String token = tokenizer.nextToken();
                result.append((char)Integer.valueOf(token.substring(0, 2), 16).intValue());
                result.append(token.substring(2));
            }
            return result.toString();
        }
        return origPath;
    }
}
