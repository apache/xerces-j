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

package org.apache.xerces.jaxp.datatype;

import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

public class XMLGregorianCalendarTest extends TestCase {
    private final DatatypeFactory factory = new DatatypeFactoryImpl();

    public void testParsesUtcMonth() {
        XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar("--07Z");
        assertEquals(DatatypeConstants.GMONTH, calendar.getXMLSchemaType());
        assertEquals(7, calendar.getMonth());
        assertEquals(0, calendar.getTimezone());
    }

    public void testFormatsMonthWithoutLegacySuffix() {
        assertMonthFormat("--07", "--07");
        assertMonthFormat("--07Z", "--07Z");
        assertMonthFormat("--07+05:30", "--07+05:30");
        assertMonthFormat("--07-05:30", "--07-05:30");
    }

    public void testAcceptsLegacyMonthInput() {
        assertMonthFormat("--07--", "--07");
        assertMonthFormat("--07--Z", "--07Z");
        assertMonthFormat("--07--+05:30", "--07+05:30");
        assertMonthFormat("--07---05:30", "--07-05:30");
    }

    public void testParsesMonthDayWithNegativeOffset() {
        XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar("--07-15-05:30");
        assertEquals(DatatypeConstants.GMONTHDAY, calendar.getXMLSchemaType());
        assertEquals("--07-15-05:30", calendar.toXMLFormat());
    }

    public void testRejectsMalformedMonths() {
        for (String invalid : new String[] {"--00Z", "--13Z", "--07-Z", "--07Zjunk"}) {
            try {
                factory.newXMLGregorianCalendar(invalid);
                fail("Accepted malformed gMonth: " + invalid);
            } catch (IllegalArgumentException expected) {
                // Expected.
            }
        }
    }

    public void testHashCodeDistinguishesDatesAcrossMonths() {
        Set<Integer> hashes = new HashSet<Integer>();
        int[] leapYearMonthLengths = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        for (int month = 1; month <= leapYearMonthLengths.length; month++) {
            for (int day = 1; day <= leapYearMonthLengths[month - 1]; day++) {
                hashes.add(factory.newXMLGregorianCalendarDate(2024, month, day, 0).hashCode());
            }
        }
        assertEquals(366, hashes.size());
    }

    public void testHashCodeDistinguishesMinutesFromSeconds() {
        XMLGregorianCalendar thirtyOneSeconds = factory.newXMLGregorianCalendar("2024-01-01T00:00:31Z");
        XMLGregorianCalendar oneMinute = factory.newXMLGregorianCalendar("2024-01-01T00:01:00Z");
        assertTrue("One minute and 31 seconds should have distinct hashes",
                thirtyOneSeconds.hashCode() != oneMinute.hashCode());
    }

    public void testEqualCalendarsHaveEqualHashes() {
        assertEqualHashes("2026-01-02T00:30:00+01:00", "2026-01-01T23:30:00Z");
        assertEqualHashes("2026-01-01T23:30:00Z", "2026-01-01T23:30:00.000Z");
        assertEqualHashes("--07", "--07--");
    }

    private void assertEqualHashes(String firstValue, String secondValue) {
        XMLGregorianCalendar first = factory.newXMLGregorianCalendar(firstValue);
        XMLGregorianCalendar second = factory.newXMLGregorianCalendar(secondValue);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    private void assertMonthFormat(String input, String expected) {
        XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar(input);
        assertEquals(DatatypeConstants.GMONTH, calendar.getXMLSchemaType());
        assertEquals(input, expected, calendar.toXMLFormat());
    }
}
