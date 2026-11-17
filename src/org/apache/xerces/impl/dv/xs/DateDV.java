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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

/**
 * Validator for <code>date</code> datatype (W3C Schema datatypes)
 *
 * @xerces.internal 
 *
 * @author Elena Litani
 * @author Gopal Sharma, SUN Microsystems Inc.
 *
 * @version $Id$
 */
public class DateDV extends DateTimeDV {

    /** Number of minutes in an hour. */
    private static final int MINUTES_PER_HOUR = 60;

    /** Number of minutes in a day. */
    private static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

    /** Largest recoverable time zone offset in minutes. */
    private static final int MAX_TIMEZONE_MINUTES = 12 * MINUTES_PER_HOUR;

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        try{
            return parse(content);
        } catch(Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "date"});
        }
    }

    /**
     * Parses, validates and computes normalized version of dateTime object
     *
     * @param str    The lexical representation of dateTime object CCYY-MM-DD
     *               with possible time zone Z or (-),(+)hh:mm
     * @return normalized dateTime representation
     * @exception SchemaDateTimeException Invalid lexical representation
     */
    protected DateTimeData parse(String str) throws SchemaDateTimeException {
        DateTimeData date = new DateTimeData(str, this);
        int len = str.length();

        int end = getDate(str, 0, len, date);
        parseTimeZone (str, end, len, date);

        //validate and normalize
        //REVISIT: do we need SchemaDateTimeException?
        validateDateTime(date);

        //save unnormalized values
        saveUnnormalized(date);
        
        if (date.utc!=0 && date.utc!='Z') {
            normalize(date);
        }
        return date;
    }

    protected int getDate(String buffer, int start, int end, DateTimeData date) {
        int endDate = super.getDate(buffer, start, end, date);
        // Date values have no time component. Use noon so normalizing the
        // time zone does not move the date.
        date.hour = 12;
        return endDate;
    }

    protected String dateToString(DateTimeData date) {
        StringBuffer message = new StringBuffer(25);
        append(message, date.year, 4);
        message.append('-');
        append(message, date.month, 2);
        message.append('-');
        append(message, date.day, 2);
        if (date.hasTimeZone()) {
            int timezone = date.timezoneHr * MINUTES_PER_HOUR + date.timezoneMin;
            if (timezone == 0) {
                message.append('Z');
            }
            else {
                // The recoverable time zone is in the range (-12:00, +12:00].
                if (timezone <= -MAX_TIMEZONE_MINUTES) {
                    timezone += MINUTES_PER_DAY;
                }
                else if (timezone > MAX_TIMEZONE_MINUTES) {
                    timezone -= MINUTES_PER_DAY;
                }
                int hours = timezone / MINUTES_PER_HOUR;
                int minutes = timezone % MINUTES_PER_HOUR;
                message.append(timezone < 0 ? '-' : '+');
                append(message, Math.abs(hours), 2);
                message.append(':');
                append(message, Math.abs(minutes), 2);
            }
        }
        return message.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData date) {
        return datatypeFactory.newXMLGregorianCalendar(date.unNormYear, date.unNormMonth, 
                date.unNormDay, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, 
                DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, 
                date.hasTimeZone() ? (date.timezoneHr * MINUTES_PER_HOUR + date.timezoneMin) : DatatypeConstants.FIELD_UNDEFINED);
    }

}
