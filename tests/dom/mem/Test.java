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

//
//  Various DOM tests.
//     Contents include
//       1.  Basic functionality for DOMString
//       2.  Regression tests for bugs fixed.
//     All individual are wrapped in a memory leak checker.
//
//     DOM Level 3:
//     1. textContent 
//     2. userData
//     3. isEqualNode
//
//     This is NOT a complete test of DOM functionality.
//

package dom.mem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import junit.framework.TestCase;
import junit.framework.Assert;



public class Test extends TestCase {

    /**
     * version 3.0 01/25/99
     * 
     * @return boolean
     * @param node java.lang.Object
     * @param mNameIndex int
     * @param signatureIndex int
     * @param parameters java.lang.Object[]
     * @param code short
     *
     * @author Philip W. Davis
     */
    public static boolean DOMExceptionsTest(Object node,
					    String methodName,
					    Class[] methodSignature,
					    Object[] parameters,
					    short code)
    {
	boolean asExpected = false;
	Method method;
	try {
	    method = node.getClass().getMethod(methodName,methodSignature);
	    method.invoke(node, parameters);
	} catch(InvocationTargetException exc) {
	    Throwable realE = exc.getTargetException(); 
	    if(realE instanceof DOMException) {
		asExpected = (((DOMException)realE).code== code);
	    }
	} catch(Exception exc) {
	}
	return (asExpected);
    }

    public void testDOMOperations() throws Exception
    {

    //
    //  Test Doc01      Create a new empty document
    //
    {
        Document    doc;
        doc = new DocumentImpl();
    }
    

    //
    //  Test Doc02      Create one of each kind of node using the
    //                  document createXXX methods.
    //                  Watch for memory leaks.
    //
    {
        //  Do all operations in a preconditioning step, to force the
        //  creation of implementation objects that are set up on first use.
        //  Don't watch for leaks in this block (no  / )
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
        DocumentFragment frag = doc.createDocumentFragment ();
        Text  text = doc.createTextNode("Doc02TextNode");
        Comment comment = doc.createComment("Doc02Comment");
        CDATASection  cdataSec = doc.createCDATASection("Doc02CDataSection");
        DocumentType  docType = doc.getImplementation().createDocumentType("Doc02DocumentType", null, null);
        Notation notation = ((DocumentImpl) doc).createNotation("Doc02Notation");
        ProcessingInstruction pi = doc.createProcessingInstruction("Doc02PITarget",
                                    "Doc02PIData");
        NodeList    nodeList = doc.getElementsByTagName("*");
    }


    
    {
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
    }
    

    
    {
        Document    doc = new DocumentImpl();
        DocumentFragment frag = doc.createDocumentFragment ();
    };
    


    
    {
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Text  text = doc.createTextNode("Doc02TextNode");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Comment comment = doc.createComment("Doc02Comment");
    }
    

    
    {
        Document doc = new DocumentImpl();
        CDATASection  cdataSec = doc.createCDATASection("Doc02CDataSection");
    }
    


    
    {
        Document doc = new DocumentImpl();
        DocumentType  docType = doc.getImplementation().createDocumentType("Doc02DocumentType", null, null);
    }
    


    
    {
        Document doc = new DocumentImpl();
        Notation notation = ((DocumentImpl)doc).createNotation("Doc02Notation");
    }
    


    
    {
        Document doc = new DocumentImpl();
        ProcessingInstruction pi = doc.createProcessingInstruction("Doc02PITarget",
                                    "Doc02PIData");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Attr  attribute = doc.createAttribute("Doc02Attribute");
    }
    


    
    {
        Document doc = new DocumentImpl();
        EntityReference  er = doc.createEntityReference("Doc02EntityReference");
    }
    

    
    {
        Document doc = new DocumentImpl();
        NodeList    nodeList = doc.getElementsByTagName("*");
    }
    

    
    //
    //  Doc03 - Create a small document tree
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl = doc.createElement("Doc03RootElement");
        doc.appendChild(rootEl);

        Text        textNode = doc.createTextNode("Doc03 text stuff");
        assertNull(rootEl.getFirstChild());
        assertNull(rootEl.getLastChild());
        rootEl.appendChild(textNode);
        assertSame(rootEl.getFirstChild(), textNode);
        assertSame(rootEl.getLastChild(), textNode);

        assertNull(textNode.getNextSibling());
        assertNull(textNode.getPreviousSibling());
        Text        textNode2 = doc.createTextNode("Doc03 text stuff");
        rootEl.appendChild(textNode2);
        assertSame(textNode.getNextSibling(), textNode2);
        assertNull(textNode2.getNextSibling());
        assertNull(textNode.getPreviousSibling());
        assertSame(textNode2.getPreviousSibling(), textNode);

        assertSame(rootEl.getFirstChild(), textNode);
        assertSame(rootEl.getLastChild(), textNode2);

        NodeList    nodeList = doc.getElementsByTagName("*");
    };
    


    //
    //  Attr01
    //
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        {
            Attr        attr01  = doc.createAttribute("Attr01");
            rootEl.setAttributeNode(attr01);
        }
        
        
        {
            Attr attr02 = doc.createAttribute("Attr01");
            rootEl.setAttributeNode(attr02);  
        }
        
    };

    //
    //  Attr02
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        Attr        attr01  = doc.createAttribute("Attr02");
        rootEl.setAttributeNode(attr01);
        Attr        attr02 = doc.createAttribute("Attr02");
        rootEl.setAttributeNode(attr02);  
    }
    


    //
    //  Attr03
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        Attr        attr01  = doc.createAttribute("Attr03");
        rootEl.setAttributeNode(attr01);

        attr01.setValue("Attr03Value1");
        attr01.setValue("Attr03Value2");
    }
    



    //
    //  Text01
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);


        Text        txt1 = doc.createTextNode("Hello Goodbye");
        rootEl.appendChild(txt1);

        txt1.splitText(6);
        rootEl.normalize();

    }
    


    //
    //  Notation01
    //
    
    { 
	/*
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        DocumentType    dt  =
	  impl.createDocumentType("DocType_for_Notation01", null, null, null);
        doc.appendChild(dt);


        NamedNodeMap notationMap = dt.getNotations();
        Notation    nt1 = ((DocumentImpl) doc).createNotation("Notation01");
        ((NotationImpl) nt1).setPublicId("Notation01PublicId");
        notationMap.setNamedItem (nt1);
        Notation    nt2 = (Notation)notationMap.getNamedItem("Notation01");
        Assertion.assert(nt1==nt2);
        nt2 = new NotationImpl((DocumentImpl)doc, null);
        nt1 = null;
        nt2 = (Notation)notationMap.getNamedItem("Notation01");
      
    */
    }
    


    //
    //  NamedNodeMap01 - comparison operators.
    //
    
    {
        NamedNodeMap    nnm = null;
        assertNull(nnm);

        Document        doc = new DocumentImpl();
        nnm = doc.getAttributes();    // Should be null, because node type
                                      //   is not Element.
        assertNull(nnm);
        assertNull(nnm);

        Element el = doc.createElement("NamedNodeMap01");
        NamedNodeMap nnm2 = el.getAttributes();    // Should be an empty, but non-null map.
        assertNotNull(nnm2);
        assertNotSame(nnm, nnm2);
        nnm = nnm2;
        assertSame(nnm, nnm2);
    }
    


    //
    //  importNode quick test
    //
    
    {
        Document    doc1 = new DocumentImpl();
        Document    doc2 = new DocumentImpl();
        
        Element     el1  = doc1.createElement("abc");
        doc1.appendChild(el1);
        assertNotNull(el1.getParentNode());
        el1.setAttribute("foo", "foovalue");
        Node        el2  = doc2.importNode(el1, true);
        assertNull(el2.getParentNode());
        String       tagName = el2.getNodeName();
        assertEquals("abc", tagName);
        assertSame(el2.getOwnerDocument(), doc2);
        assertEquals("foovalue", ((Element) el2).getAttribute("foo"));
        assertNotSame(doc1, doc2);
    }
    

    //
    //  getLength() tests.  Both Node CharacterData and NodeList implement
    //                  getLength().  Early versions of the DOM had a clash
    //                  between the two, originating in the implementation class
    //                  hirearchy, which has NodeList as a (distant) base class
    //                  of CharacterData.  This is a regression test to verify
    //                  that the problem stays fixed.
    //
    
    {
        Document     doc = new DocumentImpl();
        Text          tx = doc.createTextNode("Hello");
        Element       el = doc.createElement("abc");
        el.appendChild(tx);

        int     textLength = tx.getLength();
        assertEquals(5, textLength);

        NodeList      nl = tx.getChildNodes();
        int      nodeListLen = nl.getLength();
        assertEquals(0, nodeListLen);

        nl = el.getChildNodes();
        nodeListLen = nl.getLength();
        assertEquals(1, nodeListLen);
    }


    //
    //  NodeList - comparison operators, basic operation.
    //
    
    {
        NodeList    nl = null;
        NodeList    nl2 = null;
        assertNull(nl);
        assertNull(nl);
        assertSame(nl, nl2);

        Document        doc = new DocumentImpl();
        nl = doc.getChildNodes();    // Should be non-null, but empty

        assertNotNull(nl);
        int len = nl.getLength();
        assertEquals(0, len);

        Element el = doc.createElement("NodeList01");
        doc.appendChild(el);
        len = nl.getLength();
        assertEquals(1, len);
        assertNotSame(nl, nl2);
        nl2 = nl;
        assertSame(nl, nl2);
    }
    


 
    //
    //  Name validity checking.
    //
    
    {
         Document        doc = new DocumentImpl();
         assertTrue(DOMExceptionsTest(doc, "createElement", new Class[]{String.class}, new Object[]{"!@@ bad element name"}, DOMException.INVALID_CHARACTER_ERR));
    }
    


    //
    //  Assignment ops return value
    //
    
    {
        Document        doc = new DocumentImpl();
        Element el = doc.createElement("NodeList01");
        doc.appendChild(el);
        
        Element n1, n2, n3;
        
        n1 = n2 = n3 = el;
        assertSame(n1, n2);
        assertSame(n1, n3);
        assertSame(n1, el);
        assertNotNull(n1);
        n1 = n2 = n3 = null;
        assertNull(n1);
    }
    


    //
    //  Cloning of a node with attributes. Regression test for a ref counting 
    //  bug in attributes of cloned nodes that occured when the "owned" flag
    //  was not set in the clone.
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     root = doc.createElement("CTestRoot");
        root.setAttribute("CTestAttr", "CTestAttrValue");

        String s = root.getAttribute("CTestAttr");
        assertEquals("CTestAttrValue", s);

        Element     cloned = (Element)root.cloneNode(true);
        Attr a = cloned.getAttributeNode("CTestAttr");
        assertNotNull(a);
        s = a.getValue();
        assertEquals("CTestAttrValue", s);
        a = null;

        a = cloned.getAttributeNode("CTestAttr");
        assertNotNull(a);
        s = a.getValue();
        assertEquals("CTestAttrValue", s);

    }
    

    //
    //  Cloning of default attributes.
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     root = doc.createElement("CTestRoot");
        root.setAttribute("attr", "attrValue");
        Attr attr = root.getAttributeNode("attr");
        // turn this into a default attribute
        ((org.apache.xerces.dom.AttrImpl)attr).setSpecified(false);
        // add another attribute (this one is specified)
        root.setAttribute("attr2", "attr2Value");

        Element     cloned = (Element)root.cloneNode(true);
        Attr a = cloned.getAttributeNode("attr");
        assertFalse(a.getSpecified());
        a = cloned.getAttributeNode("attr2");
        assertTrue(a.getSpecified());

        // now if we clone the default attribute by itself the clone should be
        // specified
        a = (Attr)attr.cloneNode(true);
        assertTrue(a.getSpecified());
    }


    //
    //  DOM Level 2 tests.  These should be split out as a separate test.
    //


    //
    // hasFeature.  The set of supported options tested here is for Xerces 1.1
    //
    
    {
        DOMImplementation  impl = DOMImplementationImpl.getDOMImplementation();
        assertTrue(impl.hasFeature("XML", "2.0"));
        assertTrue(impl.hasFeature("XML", null));
        //  We also support 1.0
        assertTrue(impl.hasFeature("XML", "1.0"));
        //assertFalse(impl.hasFeature("XML", "3.0"));
        assertTrue(impl.hasFeature("Traversal", null));


        assertFalse(impl.hasFeature("HTML", null));
        assertFalse(impl.hasFeature("Views", null));
        assertFalse(impl.hasFeature("StyleSheets", null));
        assertFalse(impl.hasFeature("CSS", null));
        assertFalse(impl.hasFeature("CSS2", null));
        assertTrue(impl.hasFeature("Events", null));
        assertFalse(impl.hasFeature("UIEvents", null));
        assertFalse(impl.hasFeature("MouseEvents", null));
        assertTrue(impl.hasFeature("MutationEvents", null));
        assertFalse(impl.hasFeature("HTMLEvents", null));
        assertTrue(impl.hasFeature("Range", null));
    }
    


    //
    // CreateDocumentType
    //
    
    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        assertNotNull(dt);
        assertEquals(Node.DOCUMENT_TYPE_NODE, dt.getNodeType());
        assertEquals(qName, dt.getNodeName());
        assertNull(dt.getNamespaceURI());
        assertNull(dt.getPrefix());
        assertNull(dt.getLocalName());
        assertEquals(pubId, dt.getPublicId());
        assertEquals(sysId, dt.getSystemId());
        assertNull(dt.getInternalSubset());
        assertNull(dt.getOwnerDocument());
        
        NamedNodeMap nnm = dt.getEntities();
        assertEquals(0, nnm.getLength());
        nnm = dt.getNotations();
        assertEquals(0, nnm.getLength());

        //
        // Qualified name without prefix should also work.
        //
        qName = "docName";
        dt = impl.createDocumentType(qName, pubId, sysId);

        assertNotNull(dt);
        assertEquals(Node.DOCUMENT_TYPE_NODE, dt.getNodeType());
        assertEquals(qName, dt.getNodeName());
        assertNull(dt.getNamespaceURI());
        assertNull(dt.getPrefix());
        assertNull(dt.getLocalName());
        assertEquals(pubId, dt.getPublicId());
        assertEquals(sysId, dt.getSystemId());
        assertNull(dt.getInternalSubset());
        assertNull(dt.getOwnerDocument());

        // Creating a DocumentType with invalid or malformed qName should fail.
        assertTrue(DOMExceptionsTest(impl, "createDocumentType", new Class[]{String.class, String.class, String.class}, new Object[]{"<docName", pubId, sysId}, DOMException.INVALID_CHARACTER_ERR));
        assertTrue(DOMExceptionsTest(impl, "createDocumentType", new Class[]{String.class, String.class, String.class}, new Object[]{":docName", pubId, sysId}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(impl, "createDocumentType", new Class[]{String.class, String.class, String.class}, new Object[]{"docName:", pubId, sysId}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(impl, "createDocumentType", new Class[]{String.class, String.class, String.class}, new Object[]{"<doc::Name", pubId, sysId}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(impl, "createDocumentType", new Class[]{String.class, String.class, String.class}, new Object[]{"<doc:N:ame", pubId, sysId}, DOMException.NAMESPACE_ERR));
    }

    //
    //  DOMImplementation.CreateDocument
    //
    
    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);

        assertSame(dt.getOwnerDocument(), doc);
        assertNull(doc.getOwnerDocument());

        assertEquals(Node.DOCUMENT_NODE, doc.getNodeType());
        assertSame(doc.getDoctype(), dt);
        assertEquals("#document", doc.getNodeName());
        assertNull(doc.getNodeValue());

        Element el = doc.getDocumentElement();

        assertEquals("docName", el.getLocalName());
        assertEquals(docNSURI, el.getNamespaceURI());
        assertEquals(qName, el.getNodeName());
        assertSame(el.getOwnerDocument(), doc);
        assertSame(el.getParentNode(), doc);
        assertEquals("foo", el.getPrefix());
        assertEquals(qName, el.getTagName());
        assertFalse(el.hasChildNodes());

        //
        // Creating a second document with the same docType object should fail.
        //
        assertTrue(DOMExceptionsTest(impl, "createDocument", new Class[]{String.class, String.class, DocumentType.class}, new Object[]{docNSURI, qName, dt}, DOMException.WRONG_DOCUMENT_ERR));

        // Namespace tests of createDocument are covered by createElementNS below
    }
    
    
    //
    //  CreateElementNS methods
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
	Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // CreateElementNS
        //
        Element ela = doc.createElementNS("http://nsa", "a:ela");  // prefix and URI
        Element elb = doc.createElementNS("http://nsb", "elb");    //  URI, no prefix.
        Element elc = doc.createElementNS(null, "elc");              // No URI, no prefix.

        rootEl.appendChild(ela);
        rootEl.appendChild(elb);
        rootEl.appendChild(elc);

        assertEquals("a:ela", ela.getNodeName());
        assertEquals("http://nsa", ela.getNamespaceURI());
        assertEquals("a", ela.getPrefix());
        assertEquals("ela", ela.getLocalName());
        assertEquals("a:ela", ela.getTagName());

        assertEquals("elb", elb.getNodeName());
        assertEquals("http://nsb", elb.getNamespaceURI());
        assertNull(elb.getPrefix());
        assertEquals("elb", elb.getLocalName());
        assertEquals("elb", elb.getTagName());

        assertEquals("elc", elc.getNodeName());
        assertNull(elc.getNamespaceURI());
        assertNull(elc.getPrefix());
        assertEquals("elc", elc.getLocalName());
        assertEquals("elc", elc.getTagName());

        // Badly formed qualified name
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "<a"}, DOMException.INVALID_CHARACTER_ERR));
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", ":a"}, DOMException.NAMESPACE_ERR));
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a:"}, DOMException.NAMESPACE_ERR));
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a::a"}, DOMException.NAMESPACE_ERR));
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a:a:a"}, DOMException.NAMESPACE_ERR));

        // xml:a must have namespaceURI == "http://www.w3.org/XML/1998/namespace"
	String xmlURI = "http://www.w3.org/XML/1998/namespace";
	assertEquals(xmlURI, doc.createElementNS(xmlURI, "xml:a").getNamespaceURI());
	
        assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "xml:a"}, DOMException.NAMESPACE_ERR));
        
        assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"", "xml:a"}, DOMException.NAMESPACE_ERR));

	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{null, "xml:a"}, DOMException.NAMESPACE_ERR));

        //xmlns prefix must be bound to the xmlns namespace
        assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "xmlns"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{xmlURI, "xmlns"}, DOMException.NAMESPACE_ERR));
        
        
    assertNull(doc.createElementNS(null, "noNamespace").getNamespaceURI());

	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{null, "xmlns:a"}, DOMException.NAMESPACE_ERR));

        //In fact, any prefix != null should have a namespaceURI != null
        assertEquals("http://nsa", doc.createElementNS("http://nsa", "foo:a").getNamespaceURI());
	assertTrue(DOMExceptionsTest(doc, "createElementNS", new Class[]{String.class, String.class}, new Object[]{null, "foo:a"}, DOMException.NAMESPACE_ERR));

        //Change prefix
        Element elem = doc.createElementNS("http://nsa", "foo:a");
        elem.setPrefix("bar");
        assertEquals("bar:a", elem.getNodeName());
        assertEquals("http://nsa", elem.getNamespaceURI());
        assertEquals("bar", elem.getPrefix());
        assertEquals("a", elem.getLocalName());
        assertEquals("bar:a", elem.getTagName());
        //The spec does not prevent us from setting prefix to a node without prefix
        elem = doc.createElementNS("http://nsa", "a");
        assertEquals(null, elem.getPrefix());
        elem.setPrefix("bar");
        assertEquals("bar:a", elem.getNodeName());
        assertEquals("http://nsa", elem.getNamespaceURI());
        assertEquals("bar", elem.getPrefix());
        assertEquals("a", elem.getLocalName());
        assertEquals("bar:a", elem.getTagName());
        //Special case for xml:a where namespaceURI must be xmlURI
        elem = doc.createElementNS(xmlURI, "foo:a");
        elem.setPrefix("xml");
        elem = doc.createElementNS("http://nsa", "foo:a");
        assertTrue(DOMExceptionsTest(elem, "setPrefix", new Class[]{String.class}, new Object[]{"xml"}, DOMException.NAMESPACE_ERR));
        //However, there is no restriction on prefix xmlns
        elem.setPrefix("xmlns");
        //Also an element can not have a prefix with namespaceURI == null
        elem = doc.createElementNS(null, "a");
        assertTrue(DOMExceptionsTest(elem, "setPrefix", new Class[]{String.class}, new Object[]{"foo"}, DOMException.NAMESPACE_ERR));

        //Only prefix of Element and Attribute can be changed
        assertTrue(DOMExceptionsTest(doc, "setPrefix", new Class[]{String.class}, new Object[]{"foo"}, DOMException.NAMESPACE_ERR));

        //Prefix of readonly Element can not be changed.
        //However, there is no way to create such Element for testing yet.
    }
    



    //
    //  CreateAttributeNS methods
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // CreateAttributeNS
        //
        Attr attra = doc.createAttributeNS("http://nsa", "a:attra");       // prefix and URI
        Attr attrb = doc.createAttributeNS("http://nsb", "attrb");         //  URI, no prefix.
        Attr attrc = doc.createAttributeNS(null, "attrc");    // No URI, no prefix.

        assertEquals("a:attra", attra.getNodeName());
        assertEquals("http://nsa", attra.getNamespaceURI());
        assertEquals("a", attra.getPrefix());
        assertEquals("attra", attra.getLocalName());
        assertEquals("a:attra", attra.getName());
        assertNull(attra.getOwnerElement());

        assertEquals("attrb", attrb.getNodeName());
        assertEquals("http://nsb", attrb.getNamespaceURI());
        assertEquals(null, attrb.getPrefix());
        assertEquals("attrb", attrb.getLocalName());
        assertEquals("attrb", attrb.getName());
        assertNull(attrb.getOwnerElement());

        assertEquals("attrc", attrc.getNodeName());
        assertNull(attrc.getNamespaceURI());
        assertNull(attrc.getPrefix());
        assertEquals("attrc", attrc.getLocalName());
        assertEquals("attrc", attrc.getName());
        assertNull(attrc.getOwnerElement());


        // Badly formed qualified name
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "<a"}, DOMException.INVALID_CHARACTER_ERR));
	assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", ":a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a:"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a::a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "a:a:a"}, DOMException.NAMESPACE_ERR));

        // xml:a must have namespaceURI == "http://www.w3.org/XML/1998/namespace"
        String xmlURI = "http://www.w3.org/XML/1998/namespace";

        assertEquals(xmlURI, doc.createAttributeNS(xmlURI, "xml:a").getNamespaceURI());
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "xml:a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"", "xml:a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{null,  "xml:a"}, DOMException.NAMESPACE_ERR));

        //xmlns must have namespaceURI == "http://www.w3.org/2000/xmlns/"
        String xmlnsURI = "http://www.w3.org/2000/xmlns/";
        assertEquals(xmlnsURI, doc.createAttributeNS(xmlnsURI, "xmlns").getNamespaceURI());
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "xmlns"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{xmlURI, "xmlns"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"", "xmlns"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{null,  "xmlns"}, DOMException.NAMESPACE_ERR));

        //xmlns:a must have namespaceURI == "http://www.w3.org/2000/xmlns/"
        assertEquals(xmlnsURI, doc.createAttributeNS(xmlnsURI, "xmlns:a").getNamespaceURI());
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"http://nsa", "xmlns:a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{xmlURI, "xmlns:a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{"", "xmlns:a"}, DOMException.NAMESPACE_ERR));
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{null,  "xmlns:a"}, DOMException.NAMESPACE_ERR));

        //In fact, any prefix != null should have a namespaceURI != null
        assertEquals("http://nsa", doc.createAttributeNS("http://nsa", "foo:a").getNamespaceURI());
        assertTrue(DOMExceptionsTest(doc, "createAttributeNS", new Class[]{String.class, String.class}, new Object[]{null,  "foo:a"}, DOMException.NAMESPACE_ERR));

        //Change prefix
        Attr attr = doc.createAttributeNS("http://nsa", "foo:a");
        attr.setPrefix("bar");
        assertEquals("bar:a", attr.getNodeName());
        assertEquals("http://nsa", attr.getNamespaceURI());
        assertEquals("bar", attr.getPrefix());
        assertEquals("a", attr.getLocalName());
        assertEquals("bar:a", attr.getName());
        //The spec does not prevent us from setting prefix to a node without prefix
        attr = doc.createAttributeNS("http://nsa", "a");
        assertNull(attr.getPrefix());
        attr.setPrefix("bar");
        assertEquals("bar:a", attr.getNodeName());
        assertEquals("http://nsa", attr.getNamespaceURI());
        assertEquals("bar", attr.getPrefix());
        assertEquals("a", attr.getLocalName());
        assertEquals("bar:a", attr.getName());
        //Special case for xml:a where namespaceURI must be xmlURI
        attr = doc.createAttributeNS(xmlURI, "foo:a");
        attr.setPrefix("xml");
        attr = doc.createAttributeNS("http://nsa", "foo:a");
        assertTrue(DOMExceptionsTest(attr, "setPrefix", new Class[]{String.class}, new Object[]{"xml"}, DOMException.NAMESPACE_ERR));
        //Special case for xmlns:a where namespaceURI must be xmlURI
        attr = doc.createAttributeNS("http://nsa", "foo:a");
        assertTrue(DOMExceptionsTest(attr, "setPrefix", new Class[]{String.class}, new Object[]{"xmlns"}, DOMException.NAMESPACE_ERR));
        //Special case for xmlns where no prefix can be set
        attr = doc.createAttributeNS(xmlnsURI, "xmlns");
        assertTrue(DOMExceptionsTest(attr, "setPrefix", new Class[]{String.class}, new Object[]{"xml"}, DOMException.NAMESPACE_ERR));
        //Also an attribute can not have a prefix with namespaceURI == null
        attr = doc.createAttributeNS(null, "a");
        assertTrue(DOMExceptionsTest(attr, "setPrefix", new Class[]{String.class}, new Object[]{"foo"}, DOMException.NAMESPACE_ERR));
        
        //Only prefix of Element and Attribute can be changed
        assertTrue(DOMExceptionsTest(attr, "setPrefix", new Class[]{String.class}, new Object[]{"foo"}, DOMException.NAMESPACE_ERR));

        //Prefix of readonly Attribute can not be changed.
        //However, there is no way to create such DOM_Attribute for testing yet.
    }
    

    //
    //  getElementsByTagName*
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
	Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // Populate the document
        //
        Element ela = doc.createElementNS("http://nsa", "a:ela");  
        rootEl.appendChild(ela);
        Element elb = doc.createElementNS("http://nsb", "elb");   
        rootEl.appendChild(elb);
        Element elc = doc.createElementNS(null,           "elc");  
        rootEl.appendChild(elc);
        Element eld = doc.createElementNS("http://nsa", "d:ela");
        rootEl.appendChild(eld);
        Element ele = doc.createElementNS("http://nse", "elb");   
        rootEl.appendChild(ele);


        // 
        // Access with DOM Level 1 getElementsByTagName
        //

        NodeList nl = doc.getElementsByTagName("a:ela");
        assertEquals(1, nl.getLength());
        assertSame(nl.item(0), ela);

        nl = doc.getElementsByTagName("elb");
        assertEquals(2, nl.getLength());
        assertSame(nl.item(0), elb);
        assertSame(nl.item(1), ele);

        nl = doc.getElementsByTagName("d:ela");
        assertEquals(1, nl.getLength());
        assertSame(nl.item(0), eld);

        //
        //  Access with DOM Level 2 getElementsByTagNameNS
        //

        nl = doc.getElementsByTagNameNS(null, "elc");
        assertEquals(1, nl.getLength());
        assertSame(nl.item(0), elc);
       
        nl = doc.getElementsByTagNameNS("http://nsa", "ela");
        assertEquals(2, nl.getLength());
        assertSame(nl.item(0), ela);
        assertSame(nl.item(1), eld);

        nl = doc.getElementsByTagNameNS(null, "elb");
        assertEquals(0, nl.getLength());

        nl = doc.getElementsByTagNameNS("http://nsb", "elb");
        assertEquals(1, nl.getLength());
        assertSame(nl.item(0), elb);

        nl = doc.getElementsByTagNameNS("*", "elb");
        assertEquals(2, nl.getLength());
        assertSame(nl.item(0), elb);
        assertSame(nl.item(1), ele);

        nl = doc.getElementsByTagNameNS("http://nsa", "*");
        assertEquals(2, nl.getLength());
        assertSame(nl.item(0), ela);
        assertSame(nl.item(1), eld);

        nl = doc.getElementsByTagNameNS("*", "*");
        assertEquals(6, nl.getLength());

        assertNull(nl.item(6));
        // Assertion.assert(nl.item(-1) == 0);

        nl = rootEl.getElementsByTagNameNS("*", "*");
        assertEquals(5, nl.getLength());


        nl = doc.getElementsByTagNameNS("http://nsa", "d:ela");
        assertEquals(0, nl.getLength());


        //
        // Node lists are Live
        //

        nl = doc.getElementsByTagNameNS("*", "*");
        NodeList nla = ela.getElementsByTagNameNS("*", "*");

        assertEquals(6, nl.getLength());
        assertEquals(0, nla.getLength());

        rootEl.removeChild(elc);
        assertEquals(5, nl.getLength());
        assertEquals(0, nla.getLength());

        ela.appendChild(elc);
        assertEquals(6, nl.getLength());
        assertEquals(1, nla.getLength());
    }


    //
    // Attributes and NamedNodeMaps.
    //
    {

        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // Create a set of attributes and hang them on the root element.
        //
        Attr attra = doc.createAttributeNS("http://nsa", "a:attra");  
        rootEl.setAttributeNodeNS(attra);
        Attr attrb = doc.createAttributeNS("http://nsb", "attrb");   
        rootEl.setAttributeNodeNS(attrb);
        Attr attrc = doc.createAttributeNS(null,           "attrc");  
        rootEl.setAttributeNodeNS(attrc);
        Attr attrd = doc.createAttributeNS("http://nsa", "d:attra");
        rootEl.setAttributeNodeNS(attrd);
        Attr attre = doc.createAttributeNS("http://nse", "attrb");   
        rootEl.setAttributeNodeNS(attre);

        //
        // Check that the attribute nodes were created with the correct properties.
        //
        assertEquals("a:attra", attra.getNodeName());
        assertEquals("http://nsa", attra.getNamespaceURI());
        assertEquals("attra", attra.getLocalName());
        assertEquals("a:attra", attra.getName());
        assertEquals(Node.ATTRIBUTE_NODE, attra.getNodeType());
        assertEquals("", attra.getNodeValue());
        assertEquals("a", attra.getPrefix());
        assertTrue(attra.getSpecified());
        assertEquals("", attra.getValue());
        assertNull(attra.getOwnerElement());

        // Test methods of NamedNodeMap
        NamedNodeMap nnm = rootEl.getAttributes();
        assertEquals(4, nnm.getLength());
        assertSame(nnm.getNamedItemNS("http://nsa", "attra"), attrd);
        assertSame(nnm.getNamedItemNS("http://nsb", "attrb"), attrb);
        assertSame(nnm.getNamedItemNS("http://nse", "attrb"), attre);
        assertSame(nnm.getNamedItemNS(null, "attrc"), attrc);
        assertNull(nnm.getNamedItemNS(null, "attra"));
        assertNull(nnm.getNamedItemNS("http://nsa", "attrb"));
    }



    //
    // Text Content and User Data
    //

    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        DocumentType dt = impl.createDocumentType("foo", "PubId", "SysId");

        Document doc = impl.createDocument(null, "foo", dt);
        assertNull(((NodeImpl) doc).getTextContent());
        assertNull(((NodeImpl) dt).getTextContent());
        // no-ops:
        ((NodeImpl) doc).setTextContent("foo");
        ((NodeImpl) dt).setTextContent("foo");

        NodeImpl el = (NodeImpl) doc.getDocumentElement();
        assertEquals("", ((NodeImpl) el).getTextContent());
        el.setTextContent("yo!");
        Node t = el.getFirstChild();
        assertTrue(t != null && t.getNodeType() == Node.TEXT_NODE && t.getNodeValue().equals("yo!"));
        assertEquals("yo!", el.getTextContent());

        Comment c = doc.createComment("dummy");
        el.appendChild(c);
        
        NodeImpl el2 = (NodeImpl) doc.createElement("bar");
        el2.setTextContent("bye now");
        el.appendChild(el2);
        assertEquals("yo!bye now", el.getTextContent());
        
        // check that empty element does not produce null value
		NodeImpl el3 = (NodeImpl) doc.createElement("test");
		el.appendChild(el3);
		NodeImpl empty = (NodeImpl) doc.createElement("empty");
		el3.appendChild(empty);
		assertNotNull(el3.getTextContent());
		
		empty.setTextContent("hello");
		assertEquals(1, empty.getChildNodes().getLength());
		// check that setting to empty string or null, does not produce
		// any text node
		empty.setTextContent(null);
		assertEquals(0, empty.getChildNodes().getLength());
		empty.setTextContent("");
		assertEquals(0, empty.getChildNodes().getLength());
		
		


        class MyHandler implements UserDataHandler {
            boolean fCalled;
            Node fNode;
            String fKey;
            Object fData;

            MyHandler(String key, Object data, Node node) {
                fCalled = false;
                fKey = key;
                fData = data;
                fNode = node;
            }
            public void handle(short operation, String key,
                               Object data, Node src, Node dst) {
                assertEquals(UserDataHandler.NODE_CLONED, operation);
                assertTrue(key == fKey && data == fData && src == fNode);
                assertTrue(dst != null && dst.getNodeType() == fNode.getNodeType());
                fCalled = true;
            }
        }

        el.setUserData("mykey", c, null);
        el.setUserData("mykey2", el2, null);
        assertSame(el.getUserData("mykey"), c);
        assertSame(el.getUserData("mykey2"), el2);
        el.setUserData("mykey", null, null);
        assertNull(el.getUserData("mykey"));
        el.setUserData("mykey2", null, null);
        assertNull(el.getUserData("mykey2"));
 
        MyHandler h = new MyHandler("mykey", c, el);
        el.setUserData("mykey", c, h);
        MyHandler h2 = new MyHandler("mykey2", el2, el);
        el.setUserData("mykey2", el2, h2);
        Node cl = el.cloneNode(false);
        assertTrue(h.fCalled);
        assertTrue(h2.fCalled);


        el.setTextContent("zapped!");
        Node t2 = el.getFirstChild();
        assertTrue(t2.getNodeValue().equals("zapped!"));
        assertNull(t2.getNextSibling());
    }


    //
    // isEqualNode
    // Note: we rely on setTextContent to work properly, in case of errors
    // make sure it is the case first.

    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();

        Document doc = impl.createDocument(null, "root", null);
        NodeImpl root = (NodeImpl) doc.getDocumentElement();

        NodeImpl n1 = (NodeImpl) doc.createElement("el");
        n1.setTextContent("yo!");

        NodeImpl n2 = (NodeImpl) doc.createElement("el");
        n2.setTextContent("yo!");

        assertTrue(n1.isEqualNode(n2));

        n2.setTextContent("yoyo!");
        assertFalse(n1.isEqualNode(n2));

        n1.setTextContent("yoyo!");
        ((Element) n1).setAttribute("a1", "v1");
        ((Element) n1).setAttributeNS("uri", "a2", "v2");
        ((Element) n2).setAttribute("a1", "v1");
        ((Element) n2).setAttributeNS("uri", "a2", "v2");
        assertTrue(n1.isEqualNode(n2));
        
        Element elem = doc.createElementNS(null, "e2");
        root.appendChild(elem);
        Attr attr = doc.createAttributeNS("http://attr", "attr1");
        elem.setAttributeNode(attr);
        
        // check that setAttribute sets both name and value
        elem.setAttributeNS("http://attr","p:attr1","v2");
        Attr attr2 = elem.getAttributeNodeNS("http://attr", "attr1");
        assertTrue(attr2.getNodeName().equals("p:attr1"));
        assertTrue(attr2.getNodeValue().equals("v2"));
        
        // check that prefix is not null
        elem.setAttributeNS("http://attr","attr1","v2");
        attr2 = elem.getAttributeNodeNS("http://attr", "attr1");
        assertTrue(attr2.getNodeName().equals("attr1"));
        

        ((Element) n2).setAttribute("a1", "v2");
        assertFalse(n1.isEqualNode(n2));

        root.appendChild(n1);
        root.appendChild(n2);

        NodeImpl clone = (NodeImpl) root.cloneNode(true);
        assertTrue(clone.isEqualNode(root));

    }

    }

}    