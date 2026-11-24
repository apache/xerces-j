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

package dom.dom3;

import junit.framework.TestCase;

import org.apache.xerces.dom.DOMConfigurationImpl;
import org.apache.xerces.dom.DOMNormalizer;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.impl.xs.opti.DefaultXMLDocumentHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class NamespaceFixUpAttributesTest extends TestCase {

    public void testNamespaceDeclarationIsPassedToValidator() {
        DocumentImpl document = new DocumentImpl();
        ElementImpl root = (ElementImpl) document.createElementNS("urn:test", "p:root");
        document.appendChild(root);
        CapturingRevalidationHandler validator = new CapturingRevalidationHandler();

        new TestNormalizer().normalizeElement(root, validator);

        assertEquals(1, validator.attributeCount);
        assertEquals("xmlns:p", validator.attributeName);
        assertEquals("urn:test", validator.attributeValue);
    }

    private static class TestNormalizer extends DOMNormalizer {

        void normalizeElement(ElementImpl element, RevalidationHandler validator) {
            fDocument = (DocumentImpl) element.getOwnerDocument();
            fConfiguration = (DOMConfigurationImpl) fDocument.getDomConfig();
            fSymbolTable = new SymbolTable();
            fNamespaceContext.reset();
            fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, null);
            fValidationHandler = validator;
            validator.setDocumentHandler(this);
            normalizeNode(element);
            validator.setDocumentHandler(null);
        }
    }

    private static class CapturingRevalidationHandler
            extends DefaultXMLDocumentHandler
            implements RevalidationHandler, XMLDocumentSource {

        int attributeCount = -1;
        String attributeName;
        String attributeValue;
        private XMLDocumentHandler documentHandler;

        public boolean characterData(String data, Augmentations augs) {
            return false;
        }

        public void startElement(QName element, XMLAttributes attributes, Augmentations augs) {
            attributeCount = attributes.getLength();
            if (attributeCount > 0) {
                QName attribute = new QName();
                attributes.getName(0, attribute);
                attributeName = attribute.rawname;
                attributeValue = attributes.getValue(0);
            }
        }

        public void setDocumentHandler(XMLDocumentHandler handler) {
            documentHandler = handler;
        }

        public XMLDocumentHandler getDocumentHandler() {
            return documentHandler;
        }
    }
}
