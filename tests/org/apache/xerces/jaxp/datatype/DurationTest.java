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

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import junit.framework.TestCase;

/**
 * Tests arithmetic on durations with fractional seconds.
 */
public class DurationTest extends TestCase {

    private DatatypeFactory factory;
    private Duration fourHours;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factory = new DatatypeFactoryImpl();
        fourHours = factory.newDuration("PT4H");
    }

    public void testSubtractFractionalSeconds() throws Exception {
        Duration oneSecond = factory.newDuration("PT1.0S");

        assertEquals("PT3H59M59S", fourHours.subtract(oneSecond).toString());
    }

    public void testSubtractFractionalSecondsKeepsNonZeroFraction() throws Exception {
        Duration oneAndAHalfSeconds = factory.newDuration("PT1.5S");

        assertEquals("PT3H59M58.5S",
            fourHours.subtract(oneAndAHalfSeconds).toString());
    }
}
