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

import junit.framework.TestCase;

import org.w3c.dom.NodeList;

public class NodeListCacheTest extends TestCase {

    public void testRepeatedTraversalDoesNotReturnCacheToPoolTwice() {
        DocumentImpl document = new DocumentImpl();
        ParentNode root = (ParentNode) document.createElement("root");
        document.appendChild(root);
        ParentNode first = appendParent(root);
        ParentNode second = appendParent(root);
        ParentNode third = appendParent(root);

        NodeList rootChildren = root.getChildNodes();
        NodeList firstChildren = first.getChildNodes();
        assertEquals(3, rootChildren.getLength());
        assertEquals(2, firstChildren.getLength());
        NodeListCache rootCache = root.fNodeListCache;
        NodeListCache firstCache = first.fNodeListCache;

        walk(rootChildren);
        walk(firstChildren);
        // Return the root cache again while it is behind another cache in the free list.
        walk(rootChildren);

        assertEquals(2, second.getChildNodes().getLength());
        assertEquals(2, third.getChildNodes().getLength());
        assertSame(firstCache, second.fNodeListCache);
        assertSame(rootCache, third.fNodeListCache);
        assertNull(root.fNodeListCache);
        assertNull(first.fNodeListCache);
        assertNull(document.fFreeNLCache);

        // A checked-out cache can be returned to the pool again.
        walk(second.getChildNodes());
        assertSame(firstCache, document.fFreeNLCache);
    }

    private ParentNode appendParent(ParentNode root) {
        CoreDocumentImpl document = root.ownerDocument();
        ParentNode parent = (ParentNode) document.createElement("parent");
        root.appendChild(parent);
        parent.appendChild(document.createElement("leaf"));
        parent.appendChild(document.createElement("leaf"));
        return parent;
    }

    private void walk(NodeList children) {
        for (int i = 0; i < children.getLength(); i++) {
            assertNotNull(children.item(i));
        }
    }
}
