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
package org.apache.wml;

/**
 * The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from 
 * <a href="https://www.wapforum.org/DTD/wml_1.1.xml">https://www.wapforum.org/DTD/wml_1.1.xml</a>
 *
 * <pre>
 * {@code
 *     <!ATTLIST access
 *       http-equiv     CDATA      #IMPLIED
 *       name           CDATA      #IMPLIED
 *       forua          %boolean;  #IMPLIED
 *       content        CDATA      #REQUIRED
 *       scheme         CDATA      #IMPLIED
 *       %coreattrs;
 *     >
 * }
 * </pre>
 * <p>The meta element contains meta-info of an WML deck
 * (Section 11.3.2, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id$
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public interface WMLMetaElement extends WMLElement {

    /**
     * 'name' attribute specific the property name
     *
     * @param newValue a new value to set for the name attribute
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'http-equiv' attribute indicates the property should be
     * interpreted as HTTP header.
     *
     * @param newValue a new value to set for the http-equiv attribute
     */
    public void setHttpEquiv(String newValue);
    public String getHttpEquiv();

    /**
     * 'forua' attribute specifies whether a intermediate agent should
     * remove this meta element. A value of false means the
     * intermediate agent must remove the element.
     *
     * @param newValue a new value to set for the forua attribute
     */
    public void setForua(boolean newValue);
    public boolean getForua();

    /**
     * 'scheme' attribute specifies a form that may be used to
     * interpret the property value
     *
     * @param newValue a new value to set for the scheme attribute
     */
    public void setScheme(String newValue);
    public String getScheme();

    /**
     * 'content' attribute specifies the property value
     *
     * @param newValue a new value to set for the content attribute
     */
    public void setContent(String newValue);
    public String getContent();
}
