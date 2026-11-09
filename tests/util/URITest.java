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

package util;

import junit.framework.TestCase;

import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;

/**
 * Tests parsing and serialization of the port component of a URI.
 */
public class URITest extends TestCase {

    public void testPortWithLeadingZerosIsPreserved() throws Exception {
        URI uri = new URI("http://example.com:0080/path?query#fragment");
        assertEquals("http://example.com:0080/path?query#fragment", uri.toString());
        assertEquals("example.com", uri.getHost());
        assertEquals(80, uri.getPort());
        assertEquals("//example.com:0080", uri.getAuthority());
        assertEquals("//example.com:0080/path?query#fragment", uri.getSchemeSpecificPart());
    }

    public void testPortOutsideRangeIsPreserved() throws Exception {
        URI uri = new URI("http://example.com:70000/path");
        assertEquals("http://example.com:70000/path", uri.toString());
        assertEquals("example.com", uri.getHost());
        assertEquals(70000, uri.getPort());
        assertEquals("//example.com:70000", uri.getAuthority());
    }

    public void testPortBeyondIntegerRangeIsPreserved() throws Exception {
        URI uri = new URI("http://example.com:999999999999999999999/path");
        assertEquals("http://example.com:999999999999999999999/path", uri.toString());
        assertEquals("example.com", uri.getHost());
        assertEquals("//example.com:999999999999999999999", uri.getAuthority());
    }

    public void testEmptyPortIsPreserved() throws Exception {
        URI uri = new URI("http://example.com:/path");
        assertEquals("http://example.com:/path", uri.toString());
        assertEquals("example.com", uri.getHost());
        assertEquals(-1, uri.getPort());
        assertEquals("//example.com:", uri.getAuthority());
    }

    public void testIPv6PortIsPreserved() throws Exception {
        URI uri = new URI("http://[::1]:0080/path");
        assertEquals("http://[::1]:0080/path", uri.toString());
        assertEquals("[::1]", uri.getHost());
        assertEquals(80, uri.getPort());
        assertEquals("//[::1]:0080", uri.getAuthority());
    }

    public void testCopyPreservesPortString() throws Exception {
        URI uri = new URI("http://example.com:0080/path");
        URI copy = new URI(uri);
        assertEquals("http://example.com:0080/path", copy.toString());
        assertEquals(80, copy.getPort());
    }

    public void testSetPortOutsideRange() throws Exception {
        URI uri = new URI("http://example.com/path");
        uri.setPort(70000);
        assertEquals("http://example.com:70000/path", uri.toString());
        assertEquals(70000, uri.getPort());
    }

    public void testPortsWithLeadingZerosAreNotEqual() throws Exception {
        URI uri1 = new URI("http://example.com:0080");
        URI uri2 = new URI("http://example.com:80");
        assertFalse(uri1.equals(uri2));
    }

    public void testInvalidPortSyntaxIsRejected() {
        String[] invalidURIs = {
            "http://example.com:80a",
            "http://example.com:abc",
            "http://example.com:-1",
            "http://example.com:+80",
            "http://example.com:80.0",
            "http://example.com: 80"
        };
        for (int i = 0; i < invalidURIs.length; i++) {
            assertMalformed(invalidURIs[i]);
        }
    }

    private void assertMalformed(String uriSpec) {
        try {
            new URI(uriSpec);
            fail("Expected MalformedURIException for " + uriSpec);
        }
        catch (MalformedURIException expected) {
        }
    }
}
