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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLChar;

/**
 * All primitive types plus ID/IDREF/ENTITY/INTEGER are derived from this abstract
 * class. It provides extra information XSSimpleTypeDecl requires from each
 * type: allowed facets, converting String to actual value, check equality,
 * comparison, etc.
 * 
 * @xerces.internal 
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id$
 */
public abstract class TypeValidator {
    
    private static final boolean USE_CODE_POINT_COUNT_FOR_STRING_LENGTH = AccessController.doPrivileged(new PrivilegedAction() {
        @Override
        public Object run() {
            try {
                return Boolean.getBoolean("org.apache.xerces.impl.dv.xs.useCodePointCountForStringLength") ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (SecurityException ex) {}
            return Boolean.FALSE;
        }}) == Boolean.TRUE;

    /**
     * Which facets are allowed for this type.
     * <p>{@link org.apache.xerces.xs.XSSimpleTypeDefinition} defines constants for various facets</p>
     *
     * @return a bit-combination of allowed facets
     *
     * @see org.apache.xerces.xs.XSSimpleTypeDefinition
     */
    public abstract short getAllowedFacets();

    /**
     * Converts a string to an actual value.
     * <p>For example: for number types (decimal, double, float, and types derived from them),
     * get the BigDecimal, Double, or Float objects.
     * For string and derived types, they just return the string itself.
     * </p>
     *
     * @param content the string value that needs to be converted to an actual value
     * @param context the validation context
     *
     * @throws InvalidDatatypeValueException if the content is invalid
     */
    public abstract Object getActualValue(String content, ValidationContext context)
        throws InvalidDatatypeValueException;

    /**
     * Check extra rules.
     * <p>For ID, IDREF, and ENTITY types, do some extra checking after the value is checked
     * to be valid with respect to both lexical representation and facets.</p>
     *
     * @param value the value for which additional checks will be made
     * @param context the validation context
     *
     * @throws InvalidDatatypeValueException if the value is invalid
     */
    public void checkExtraRules(Object value, ValidationContext context) throws InvalidDatatypeValueException {
    }

    // the following methods might not be supported by every DV.
    // but XSSimpleTypeDecl should know which type supports which methods,
    // and it's an *internal* error if a method is called on a DV that
    // doesn't support it.

    //order constants
    public static final short LESS_THAN     = -1;
    public static final short EQUAL         = 0;
    public static final short GREATER_THAN  = 1;
    public static final short INDETERMINATE = 2;

    /**
     * Is identical checks whether the two values are identical.
     * <p>For example; this distinguishes -0.0 from 0.0</p>
     * <p>Where there is distinction between identity and equality, this method will
     * be overwritten.</p>
     *
     * @param value1 a value to compare
     * @param value2 a value to compare
     * @return true if the values are identical
     */
    public boolean isIdentical (Object value1, Object value2) {
        return value1.equals(value2);
    }

    /**
     * Checks the order relation between two values.
     * <p>The parameters are in compiled form (from {@link #getActualValue(String, ValidationContext)})</p>
     * <p>This class should be overridden with the first value calling <code>compareTo()</code> with
     * the second value.</p>
     * <p><em>Note</em>: The default behaviour in TypeValidator is to return -1</p>
     *
     * @param value1 a value to compare
     * @param value2 a value to compare
     * @return either -1, 0, or 1 to indicate if the first arg should be considered before, same, or after the second arg
     */
    // note that this exists because Xerces predates JDK 1.2, should really use java.lang.Comparable now
    public int compare(Object value1, Object value2) {
        return -1;
    }

    /**
     * Get the length of the value.
     * <p>The parameters are in compiled form (from {@link #getActualValue(String, ValidationContext)})</p>
     *
     * @param value the data to check
     * @return the length of the value
     */
    public int getDataLength(Object value) {
        if (value instanceof String) {
            final String str = (String)value;
            if (!USE_CODE_POINT_COUNT_FOR_STRING_LENGTH) {
                return str.length();
            }
            return getCodePointLength(str);
        }
        return -1;
    }

    /**
     * Get the number of digits of the value.
     * <p>The parameters are in compiled form (from {@link #getActualValue(String, ValidationContext)})</p>
     *
     * @param value the data to check
     * @return the number of digits of the value
     */
    public int getTotalDigits(Object value) {
        return -1;
    }

    /**
     * Get the number of fraction digits of the value.
     * <p>The parameters are in compiled form (from {@link #getActualValue(String, ValidationContext)})</p>
     *
     * @param value the data to check
     * @return the number of fraction digits of the value
     */
    public int getFractionDigits(Object value) {
        return -1;
    }
    
    // Returns the length of the string in Unicode code points.
    private int getCodePointLength(String value) {
        // Count the number of surrogate pairs, and subtract them from
        // the total length.
        final int len = value.length();
        int surrogatePairCount = 0;
        for (int i = 0; i < len - 1; ++i) {
            if (XMLChar.isHighSurrogate(value.charAt(i))) {
                if (XMLChar.isLowSurrogate(value.charAt(++i))) {
                    ++surrogatePairCount;
                }
                else {
                    --i;
                }
            }
        }
        return len - surrogatePairCount;
    }

    /**
     * Check whether the char is a digit or not
     * <p>check whether the character is in the range <code>0x30 ~ 0x39</code></p>
     *
     * @param ch the char value to check
     * @return true if the char is an ASCII digit
     */
    public static final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Get the numeric value of the digit, or -1.
     * <p>If the character is in the range <code>0x30 ~ 0x39</code>, return its int value (0~9), otherwise, return -1</p>
     *
     * @param ch the char value to check
     * @return the numeric value of the digit, or -1
     */
    public static final int getDigit(char ch) {
        return isDigit(ch) ? ch - '0' : -1;
    }
    
} // interface TypeValidator
