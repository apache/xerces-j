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

package org.apache.xerces.dom;

import org.w3c.dom.Node;

/** 
 * A DOM node backed by a table entry in a {@link DeferredDocumentImpl}.
 *
 * <p>When deferred node expansion is enabled, Xerces parses the XML into
 * compact tables in the document. It creates DOM node objects and populates
 * their data and child lists as needed when applications access the tree.
 * The XML has already been parsed when the DOM is returned to the caller.</p>
 * 
 * @xerces.internal
 *
 * @version $Id$
 */
public interface DeferredNode extends Node {


     public static final short TYPE_NODE  =  20;

    //
    // DeferredNode methods
    //

    /** Returns the index that identifies this node in the deferred document. */
    public int getNodeIndex();

} // interface DeferredNode
