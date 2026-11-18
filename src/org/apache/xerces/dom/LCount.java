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


/** Internal class LCount tracks the number of listeners registered
    for a given event name within a document. This allows the document
    to avoid generating, or to discard, events for which no listeners
    are registered.
*/
/**
 * @xerces.internal
 * 
 * @version $Id$
 */

class LCount 
{ 
    private final java.util.Hashtable<String, LCount> counts =
            new java.util.Hashtable<>();
    int captures=0;

    LCount lookup(String evtName)
    {
        LCount lc=counts.get(evtName);
        if(lc==null)
            counts.put(evtName,(lc=new LCount()));
        return lc;	        
    }

    void add(String evtName, boolean useCapture)
    {
        LCount lc = lookup(evtName);
        if (useCapture) {
            ++lc.captures;
        }
        else {
            ++lc.bubbles;
        }
        ++lc.total;
    }

    void remove(String evtName, boolean useCapture)
    {
        LCount lc = lookup(evtName);
        if (useCapture) {
            --lc.captures;
        }
        else {
            --lc.bubbles;
        }
        --lc.total;
    }
} // class LCount
