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

package org.apache.xerces.impl.dtd;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.xerces.impl.xs.opti.DefaultXMLDocumentHandler;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.impl.dtd.XMLDTDLoader;

public class ParameterEntityNonNormalizedTextTest extends TestCase {

    public void testNonNormalizedTextExcludesExpandedParameterEntity() throws Exception {
        String dtd = "<!ELEMENT foo EMPTY>"
                + "<!ENTITY % pe1 \"This is the value of pe1\">"
                + "<!ENTITY % pe2 \"Entity pe2 references pe1: [%pe1;]\">";
        Map decls = parseInternalEntityDecls(dtd);

        assertEquals("This is the value of pe1",
                ((EntityDecl) decls.get("%pe1")).text);
        assertEquals("This is the value of pe1",
                ((EntityDecl) decls.get("%pe1")).nonNormalizedText);
        assertEquals("Entity pe2 references pe1: [This is the value of pe1]",
                ((EntityDecl) decls.get("%pe2")).text);
        assertEquals("Entity pe2 references pe1: [%pe1;]",
                ((EntityDecl) decls.get("%pe2")).nonNormalizedText);
    }

    public void testNonNormalizedTextExcludesMultipleParameterEntityExpansions() throws Exception {
        String dtd = "<!ELEMENT foo EMPTY>"
                + "<!ENTITY % pe1 \"ONE\">"
                + "<!ENTITY % pe2 \"A%pe1;B%pe1;C\">";
        Map decls = parseInternalEntityDecls(dtd);

        assertEquals("AONEBONEC", ((EntityDecl) decls.get("%pe2")).text);
        assertEquals("A%pe1;B%pe1;C",
                ((EntityDecl) decls.get("%pe2")).nonNormalizedText);
    }

    private Map parseInternalEntityDecls(String dtd) throws Exception {
        XMLDTDLoader loader = new XMLDTDLoader();
        final Map decls = new HashMap();
        loader.setDTDHandler(new DefaultXMLDocumentHandler() {
            public void internalEntityDecl(String name, XMLString text,
                    XMLString nonNormalizedText, Augmentations augs) {
                decls.put(name, new EntityDecl(text.toString(),
                        nonNormalizedText.toString()));
            }
        });
        loader.loadGrammar(new XMLInputSource(null, "test.dtd", null,
                new StringReader(dtd), null));
        return decls;
    }

    private static final class EntityDecl {
        final String text;
        final String nonNormalizedText;

        EntityDecl(String text, String nonNormalizedText) {
            this.text = text;
            this.nonNormalizedText = nonNormalizedText;
        }
    }
}
