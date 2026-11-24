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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Element;

public class InvalidEntityReferenceNameTest extends TestCase {

    public void testInvalidEntityReferenceNameIsReported() throws Exception {
        DocumentImpl document = new DocumentImpl();
        document.setErrorChecking(false);
        Element root = document.createElementNS(null, "root");
        root.appendChild(document.createEntityReference("1bad"));
        document.appendChild(root);
        document.setErrorChecking(true);
        document.setXmlVersion("1.1");

        final List<DOMError> errors = new ArrayList<DOMError>();
        document.getDomConfig().setParameter("error-handler", new DOMErrorHandler() {
            public boolean handleError(DOMError error) {
                errors.add(error);
                return true;
            }
        });

        document.normalizeDocument();

        assertEquals(1, errors.size());
        assertEquals(DOMError.SEVERITY_ERROR, errors.get(0).getSeverity());
        assertEquals("wf-invalid-character-in-node-name", errors.get(0).getType());
    }
}
