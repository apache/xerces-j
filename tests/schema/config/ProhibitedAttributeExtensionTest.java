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

package schema.config;

import java.io.StringReader;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

public class ProhibitedAttributeExtensionTest extends TestCase {

    public void testProhibitedAttributeCannotBeAddedByExtension() throws Exception {
        String xsd = "<?xml version='1.0'?>"
                + "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                + "<xs:complexType name='CT'>"
                + "  <xs:attribute name='x' type='xs:int'/>"
                + "  <xs:attribute name='y' type='xs:int'/>"
                + "</xs:complexType>"
                + "<xs:complexType name='CT2'>"
                + "  <xs:complexContent>"
                + "    <xs:restriction base='CT'>"
                + "      <xs:attribute name='x' type='xs:int' use='prohibited'/>"
                + "    </xs:restriction>"
                + "  </xs:complexContent>"
                + "</xs:complexType>"
                + "<xs:complexType name='CT3'>"
                + "  <xs:complexContent>"
                + "    <xs:extension base='CT2'>"
                + "      <xs:attribute name='x' type='xs:int'/>"
                + "    </xs:extension>"
                + "  </xs:complexContent>"
                + "</xs:complexType>"
                + "<xs:element name='root' type='CT3'/>"
                + "</xs:schema>";
        Vector keys = parseSchema(xsd);
        assertTrue(keys.toString(), keys.contains("cos-ct-extends.1.5"));
    }

    private Vector parseSchema(String xsd) throws Exception {
        XMLGrammarPreparser preparser = new XMLGrammarPreparser();
        preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
        preparser.setGrammarPool(new XMLGrammarPoolImpl());
        final Vector keys = new Vector();
        preparser.setErrorHandler(new XMLErrorHandler() {
            public void warning(String domain, String key, XMLParseException e) {}
            public void error(String domain, String key, XMLParseException e) {
                keys.add(key);
            }
            public void fatalError(String domain, String key, XMLParseException e) throws XMLParseException {
                keys.add(key);
                throw e;
            }
        });
        preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA,
                new XMLInputSource(null, "test.xsd", null, new StringReader(xsd), "UTF-8"));
        return keys;
    }
}
