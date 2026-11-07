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

package dom.rename;

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dom.ParserWrapper;

/**
 * A simple program to test Document.getElementById() and the management
 * of ID attributes. Originally based on dom.Counter.
 * This test takes as input input.xml file
 *
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 *
 * @version $Id$
 */
public class Test extends TestCase implements UserDataHandler {

    //
    // Constants
    //

    // feature ids

    protected static final String NAMESPACES_FEATURE_ID =
        "http://xml.org/sax/features/namespaces";

    protected static final String VALIDATION_FEATURE_ID =
        "http://xml.org/sax/features/validation";

    protected static final String SCHEMA_VALIDATION_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema";

    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema-full-checking";

    protected static final String DEFERRED_DOM_FEATURE_ID =
        "http://apache.org/xml/features/dom/defer-node-expansion";

    // default settings

    protected static final String DEFAULT_PARSER_NAME = "dom.wrappers.Xerces";

    protected static final boolean DEFAULT_NAMESPACES = true;

    protected static final boolean DEFAULT_VALIDATION = false;

    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;

    // Xerces specific feature
    protected static final boolean DEFAULT_DEFERRED_DOM = true;

    private Document document;

    protected void setUp() throws Exception {
        ParserWrapper parser = (ParserWrapper)
            Class.forName(DEFAULT_PARSER_NAME).newInstance();
        try {
            parser.setFeature(NAMESPACES_FEATURE_ID, DEFAULT_NAMESPACES);
        }
        catch (SAXException e) {
            // ignore
        }
        try {
            parser.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);
        }
        catch (SAXException e) {
            // ignore
        }
        try {
            parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID,
                              DEFAULT_SCHEMA_VALIDATION);
        }
        catch (SAXException e) {
            // ignore
        }
        try {
            parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID,
                              DEFAULT_SCHEMA_FULL_CHECKING);
        }
        catch (SAXException e) {
            // ignore
        }
        if (parser instanceof dom.wrappers.Xerces) {
            try {
                parser.setFeature(DEFERRED_DOM_FEATURE_ID,
                                  DEFAULT_DEFERRED_DOM);
            }
            catch (SAXException e) {
                // ignore
            }
        }
        try {
            document = parser.parse("tests/dom/rename/input.xml");
        }
        catch (SAXParseException e) {
            fail("Parse error: " + e.getMessage());
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (e instanceof SAXException) {
                msg = ((SAXException)e).getException().getMessage();
            }
            fail("Parse error: " + msg);
        }
    }

    public void testRename() {
        doTestRename(document);
    }

    /** Performs the actual test. */
    public void doTestRename(Document doc) {

        System.out.println("DOM rename Test...");

	// getting the first "email" element
	NodeList elements = doc.getElementsByTagName("email");
	Element child = (Element) elements.item(0);
	assertTrue("child should not be null", child != null);
	assertEquals("nodeName should be email", "email", child.getNodeName());

	// default must be there
	Attr at = child.getAttributeNode("defaultEmailAttr");
	assertTrue("defaultEmailAttr should not be null", at != null);
	assertEquals("default value of defaultEmailAttr", "defaultEmailValue", at.getValue());
	assertTrue("defaultEmailAttr should not be specified", !at.getSpecified());

	// attach some data
	child.setUserData("mydata", "yo", this);
	assertEquals("user data should be 'yo'", "yo", (String) child.getUserData("mydata"));

	// renaming an element without a url
	Element newChild = (Element) doc.renameNode(child, null, "url");

	assertEquals("renamed node name should be url", "url", newChild.getNodeName());
	assertTrue("namespace should be null", newChild.getNamespaceURI() == null);

	// old default must no longer be there
	assertTrue("should not have defaultEmailAttr after rename",
                   !newChild.hasAttribute("defaultEmailAttr"));
	assertTrue("defaultEmailAttr should now be specified", at.getSpecified());

	// new default must be there
	at = newChild.getAttributeNode("defaultUrlAttr");
	assertTrue("defaultUrlAttr should not be null", at != null);
	assertEquals("default value of defaultUrlAttr", "defaultUrlValue", at.getValue());
	assertTrue("defaultUrlAttr should not be specified", !at.getSpecified());

	// data must still be there
	assertEquals("user data should still be 'yo'", "yo", (String) newChild.getUserData("mydata"));
	// and handler must have been called if new node was created
	if (newChild != child) {
	    assertTrue("operation should be NODE_RENAMED",
                       lastOperation == UserDataHandler.NODE_RENAMED);
	    assertEquals("key should be 'mydata'", "mydata", lastKey);
	    assertEquals("data should be 'yo'", "yo", (String) lastData);
	    assertTrue("source should be original child", lastSource == child);
	    assertTrue("destination should be new child", lastDestination == newChild);
	    resetHandlerData();
	}

	// renaming an element with a url
	Element newChild2 = (Element) doc.renameNode(newChild, "ns1", "foo");

	assertEquals("renamed node name should be foo", "foo", newChild2.getNodeName());
	assertEquals("namespace should be ns1", "ns1", newChild2.getNamespaceURI());
	assertTrue("should not have defaultUrlAttr after rename",
                   !newChild2.hasAttribute("defaultUrlAttr"));
	// data must still be there
	assertEquals("user data should still be 'yo'",
                     "yo", (String) newChild2.getUserData("mydata"));
	// and handler must have been called if new node was created
	if (newChild2 != newChild) {
	    assertTrue("operation should be NODE_RENAMED",
                       lastOperation == UserDataHandler.NODE_RENAMED);
	    assertEquals("key should be 'mydata'", "mydata", lastKey);
	    assertEquals("data should be 'yo'", "yo", (String) lastData);
	    assertTrue("source should be previous child", lastSource == newChild);
	    assertTrue("destination should be new child2", lastDestination == newChild2);
	    resetHandlerData();
	}

	// getting the second "email" element
	child = (Element) elements.item(1);
	assertTrue("second child should not be null", child != null);
	assertEquals("second child nodeName should be email", "email", child.getNodeName());

	// default must be there
	at = child.getAttributeNode("defaultEmailAttr");
	assertTrue("second child defaultEmailAttr should not be null", at != null);
	assertEquals("second child default value", "defaultEmailValue", at.getValue());
	assertTrue("second child defaultEmailAttr should not be specified", !at.getSpecified());

	// attach some data
	at.setUserData("mydata", "yo", this);
	assertEquals("attr user data should be 'yo'", "yo", (String) at.getUserData("mydata"));

	// renaming an attribute without a url
	Attr newAt = (Attr) doc.renameNode(at, null, "foo");
	assertTrue("renamed attr should not be null", newAt != null);
	assertEquals("renamed attr name should be foo", "foo", newAt.getNodeName());
	assertTrue("renamed attr namespace should be null", newAt.getNamespaceURI() == null);
	assertEquals("renamed attr value should remain", "defaultEmailValue", newAt.getValue());
	assertTrue("renamed attr should be specified", newAt.getSpecified());
	assertTrue("child should have 'foo' attribute", child.hasAttribute("foo"));
	// default must be back
	assertTrue("defaultEmailAttr should be present", child.hasAttribute("defaultEmailAttr"));
	// data must still be there
	assertEquals("attr user data should still be 'yo'",
                     "yo", (String) newAt.getUserData("mydata"));
	// and handler must have been called if new node was created
	if (newAt != at) {
	    assertTrue("operation should be NODE_RENAMED",
                       lastOperation == UserDataHandler.NODE_RENAMED);
	    assertEquals("key should be 'mydata'", "mydata", lastKey);
	    assertEquals("data should be 'yo'", "yo", (String) lastData);
	    assertTrue("source should be original attr", lastSource == at);
	    assertTrue("destination should be new attr", lastDestination == newAt);
	    resetHandlerData();
	}

	// renaming an attribute with a url
	Attr newAt2 = (Attr) doc.renameNode(newAt, "ns1", "bar");
	assertTrue("renamed attr2 should not be null", newAt2 != null);
	assertEquals("renamed attr2 name should be bar", "bar", newAt2.getNodeName());
	assertEquals("renamed attr2 namespace should be ns1", "ns1", newAt2.getNamespaceURI());
	assertEquals("renamed attr2 value should remain", "defaultEmailValue", newAt2.getValue());
	assertTrue("renamed attr2 should be specified", newAt2.getSpecified());
	assertTrue("child should have ns1:bar attribute",
                   child.hasAttributeNS("ns1", "bar"));
	// data must still be there
	assertEquals("attr2 user data should still be 'yo'",
                     "yo", (String) newAt2.getUserData("mydata"));
	// and handler must have been called if new node was created
	if (newAt2 != newAt) {
	    assertTrue("operation should be NODE_RENAMED",
                       lastOperation == UserDataHandler.NODE_RENAMED);
	    assertEquals("key should be 'mydata'", "mydata", lastKey);
	    assertEquals("data should be 'yo'", "yo", (String) lastData);
	    assertTrue("source should be previous attr", lastSource == newAt);
	    assertTrue("destination should be new attr2", lastDestination == newAt2);
	    resetHandlerData();
	}


        System.out.println("done.");

    } // doTestRename(Document)

    // UserDataHandler related data
    short lastOperation = -1;
    String lastKey;
    Object lastData;
    Node lastSource;
    Node lastDestination;

    void resetHandlerData() {
	lastOperation = -1;
	lastKey = null;
	lastData = null;
	lastSource = null;
	lastDestination = null;
    }

    /**
     * This method is called whenever the node for which this handler is 
     * registered is imported, cloned, or renamed.
     * @param operation Specifies the type of operation that is being 
     *   performed on the node.
     * @param key Specifies the key for which this handler is being called. 
     * @param data Specifies the data for which this handler is being called. 
     * @param src Specifies the node being cloned, imported, or renamed. This 
     *   is <code>null</code> when the node is being deleted.
     * @param dst Specifies the node newly created if any, or 
     *   <code>null</code>.
     */
    public void handle(short operation, String key, Object data,
		       Node src, Node dst) {
	lastOperation = operation;
	lastKey = key;
	lastData = data;
	lastSource = src;
	lastDestination = dst;
    }

} // class Test
