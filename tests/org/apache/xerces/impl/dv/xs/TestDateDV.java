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

package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;

import junit.framework.TestCase;

/**
 * Tests {@link DateDV}.
 */
public class TestDateDV extends TestCase {

    public void testCanonicalValue() throws Exception {
        assertCanonicalValue("2003-12-31Z", "2003-12-31Z");
        assertCanonicalValue("2003-12-31Z", "2003-12-31+00:00");
        assertCanonicalValue("2003-12-31Z", "2003-12-31-00:00");

        assertCanonicalValue("2004-01-01+11:00", "2003-12-31-13:00");
        assertCanonicalValue("2003-12-30-11:00", "2003-12-31+13:00");

        assertCanonicalValue("2004-01-01+10:47", "2003-12-31-13:13");
        assertCanonicalValue("2003-12-30-10:47", "2003-12-31+13:13");

        // The recoverable time zone must be between +12:00 and -11:59 so check its wrapping the right end.
        assertCanonicalValue("2003-12-31+12:00", "2003-12-31+12:00");
        assertCanonicalValue("2004-01-01+12:00", "2003-12-31-12:00");

        assertCanonicalValue("2003-12-31+01:00", "2003-12-31+01:00");
        assertCanonicalValue("2003-12-31-01:00", "2003-12-31-01:00");

        assertCanonicalValue("2011-06-01+03:00", "2011-06-01+03:00");
        assertCanonicalValue("2011-06-01-01:00", "2011-06-01-01:00");
        assertCanonicalValue("2011-06-01Z", "2011-06-01Z");
        assertCanonicalValue("2011-06-01+00:01", "2011-06-01+00:01");
        assertCanonicalValue("2011-06-01Z", "2011-06-01+00:00");
        assertCanonicalValue("2011-06-01", "2011-06-01");
        assertCanonicalValue("2011-06-01-00:01", "2011-06-01-00:01");
        assertCanonicalValue("2011-06-01Z", "2011-06-01-00:00");
        assertCanonicalValue("2011-05-31-10:00", "2011-06-01+14:00");
        assertCanonicalValue("2011-05-31-10:30", "2011-06-01+13:30");
        assertCanonicalValue("2011-06-02+10:00", "2011-06-01-14:00");
        assertCanonicalValue("2011-06-02+10:30", "2011-06-01-13:30");
        assertCanonicalValue("2011-06-01+12:00", "2011-06-01+12:00");
        assertCanonicalValue("2011-06-01-11:59", "2011-06-01-11:59");
        assertCanonicalValue("2011-05-31-11:59", "2011-06-01+12:01");
        assertCanonicalValue("2011-06-02+12:00", "2011-06-01-12:00");

        try {
            assertCanonicalValue("This will go boom when parsing rather than give an odd toString.",
                    "2003-12-31+12312:123123");
            fail();
        }
        catch (InvalidDatatypeValueException expected) {
        }
    }

    private void assertCanonicalValue(String expected, String input) throws Exception {
        Object actualValue = new DateDV().getActualValue(input, null);
        String canonicalValue = actualValue.toString();
        assertEquals(input, expected, canonicalValue);
        assertEquals(actualValue, new DateDV().getActualValue(canonicalValue, null));
    }
}
