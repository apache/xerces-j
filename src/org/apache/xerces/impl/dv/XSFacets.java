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

package org.apache.xerces.impl.dv;

import java.util.Vector;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSObjectList;

/**
 * The class used to pass all facets to {@link XSSimpleType#applyFacets}.
 *
 * @xerces.internal 
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id$
 */
public class XSFacets {

    /**
     * value of length facet.
     */
    public int length;
    
    /**     
     * value of maxScale facet.
     */
    public int maxScale;

    /**     
     * value of minScale facet.
     */
    public int minScale;
    
    /**
     * value of minLength facet.
     */
    public int minLength;

    /**
     * value of maxLength facet.
     */
    public int maxLength;

    /**
     * value of whiteSpace facet.
     */
    public short whiteSpace;
    
    /**
     * value of explicitTimezone facet.
     */
    public short explicitTimezone;

    /**
     * value of totalDigits facet.
     */
    public int totalDigits;

    /**
     * value of fractionDigits facet.
     */
    public int fractionDigits;

    /**
     * string containing value of pattern facet, for multiple patterns values
     * are ORed together.
     */
    public String pattern;

    /**
     * Vector containing values of Enumeration facet, as String's.
     */
    public Vector enumeration;

    /**
     * An array parallel to "Vector enumeration". It contains namespace context
     * of each enumeration value. Elements of this vector are NamespaceContext
     * objects.
     */
    public Vector enumNSDecls;

    /**
     * value of maxInclusive facet.
     */
    public String maxInclusive;

    /**
     * value of maxExclusive facet.
     */
    public String maxExclusive;

    /**
     * value of minInclusive facet.
     */
    public String minInclusive;

    /**
     * value of minExclusive facet.
     */
    public String minExclusive;
    
    /*
     * Vector containing reference to the assert facets. introduced in XML Schema 1.1
     */
    public Vector assertFacets;
   
    public XSAnnotation lengthAnnotation;
    public XSAnnotation minLengthAnnotation;
    public XSAnnotation maxLengthAnnotation;
    public XSAnnotation whiteSpaceAnnotation;
    public XSAnnotation totalDigitsAnnotation;
    public XSAnnotation fractionDigitsAnnotation;
    public XSObjectListImpl patternAnnotations;
    public XSObjectList enumAnnotations;
    public XSAnnotation maxInclusiveAnnotation;
    public XSAnnotation maxExclusiveAnnotation;
    public XSAnnotation minInclusiveAnnotation;
    public XSAnnotation minExclusiveAnnotation;
    public XSAnnotation maxScaleAnnotation; //XML Schema 1.1    
    public XSAnnotation minScaleAnnotation; //XML Schema 1.1   
    public XSAnnotation explicitTimezoneAnnotation; //XML schema 1.1
    
    // the annotations of the assertions are stored in XSAssertImpl objects
    // stored in the 'assertFacets' Vector. 
    
    public void reset(){
        lengthAnnotation = null;
        minLengthAnnotation = null;
        maxLengthAnnotation = null;
        whiteSpaceAnnotation = null;
        totalDigitsAnnotation = null;
        fractionDigitsAnnotation = null;
        patternAnnotations = null;
        enumAnnotations = null;
        maxInclusiveAnnotation = null;
        maxExclusiveAnnotation = null;
        minInclusiveAnnotation = null;
        minExclusiveAnnotation = null;
        assertFacets = null;
        maxScaleAnnotation = null;//XML Schema 1.1      
        minScaleAnnotation = null;//XML Schema 1.1  
        explicitTimezoneAnnotation = null; //XML Schema 1.1
    }
}
