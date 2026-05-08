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

package dom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import junit.framework.TestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * This class tests methods for XML DOM implementation.
 * DOMException errors are tested by calls to DOMExceptionsTest from: Main, docBuilder...
 *
 * @author Philip W. Davis
 */
public class DTest extends TestCase {

    /**
     * @return org.w3c.dom.Document
     */
    private static Document createDocument() {
        return new org.apache.xerces.dom.DocumentImpl();
    }

    private static DocumentType createDocumentType(Document doc, String name) {
        return ((org.apache.xerces.dom.DocumentImpl) doc).createDocumentType(name, null, null);
    }

    private static Entity createEntity(Document doc, String name) {
        return new org.apache.xerces.dom.EntityImpl((org.apache.xerces.dom.DocumentImpl) doc, name);
    }

    private static Notation createNotation(Document doc, String name) {
        return new org.apache.xerces.dom.NotationImpl((org.apache.xerces.dom.DocumentImpl) doc, name);
    }

    /**
     * This method builds test documents for the XML DOM implementation.
     *
     * @param doc org.w3c.dom.Document
     * @param name document's name
     */
    private static void docBuilder(org.w3c.dom.Document doc, String name) {            
        Element docFirstElement = doc.createElement(name + "FirstElement");
        doc.appendChild(docFirstElement);
        docFirstElement.setAttribute(name + "FirstElement", name + "firstElement");
        
        ProcessingInstruction docProcessingInstruction = doc.createProcessingInstruction(name +
                        "TargetProcessorChannel", "This is " + doc + "'s processing instruction");
        docFirstElement.appendChild(docProcessingInstruction);
        
        Element docBody = doc.createElement(name + "TestBody");
        docFirstElement.appendChild(docBody);
        
        Element docBodyLevel21 = doc.createElement(name + "BodyLevel21");
        Element docBodyLevel22 = doc.createElement(name + "BodyLevel22");
        Element docBodyLevel23 = doc.createElement(name + "BodyLevel23");
        Element docBodyLevel24 = doc.createElement(name + "BodyLevel24");
        docBody.appendChild(docBodyLevel21);
        docBody.appendChild(docBodyLevel22);
        docBody.appendChild(docBodyLevel23);
        docBody.appendChild(docBodyLevel24);
        
        Element docBodyLevel31 = doc.createElement(name + "BodyLevel31");
        Element docBodyLevel32 = doc.createElement(name + "BodyLevel32");
        Element docBodyLevel33 = doc.createElement(name + "BodyLevel33");
        Element docBodyLevel34 = doc.createElement(name + "BodyLevel34");
        docBodyLevel21.appendChild(docBodyLevel31);
        docBodyLevel21.appendChild(docBodyLevel32);
        docBodyLevel22.appendChild(docBodyLevel33);
        docBodyLevel22.appendChild(docBodyLevel34);
        
        Text docTextNode11 = doc.createTextNode(name + "BodyLevel31'sChildTextNode11");
        Text docTextNode12 = doc.createTextNode(name + "BodyLevel31'sChildTextNode12");
        Text docTextNode13 = doc.createTextNode(name + "BodyLevel31'sChildTextNode13");
        Text docTextNode2 = doc.createTextNode(name + "TextNode2");
        Text docTextNode3 = doc.createTextNode(name + "TextNode3");
        Text docTextNode4 = doc.createTextNode(name + "TextNode4");
        docBodyLevel31.appendChild(docTextNode11);
        docBodyLevel31.appendChild(docTextNode12);
        docBodyLevel31.appendChild(docTextNode13);
        docBodyLevel32.appendChild(docTextNode2);
        docBodyLevel33.appendChild(docTextNode3);
        docBodyLevel34.appendChild(docTextNode4);
        
        CDATASection docCDATASection = doc.createCDATASection("<![CDATA[<greeting>Hello, world!</greeting>]]>");
        docBodyLevel23.appendChild(docCDATASection);
        
        Comment docComment = doc.createComment("This should be a comment of some kind ");
        docBodyLevel23.appendChild(docComment);
        
        EntityReference docReferenceEntity = doc.createEntityReference("ourEntityNode");
        docBodyLevel24.appendChild(docReferenceEntity);
    
        Notation docNotation = createNotation(doc, "ourNotationNode");
        DocumentType docType = (DocumentType) doc.getFirstChild();
        docType.getNotations().setNamedItem(docNotation);
        
        DocumentFragment docDocFragment = doc.createDocumentFragment();    
    
        //***********Following are for errorTests
        Text docNode3 = doc.createTextNode(name + "docTextNode3");
        Text docNode4 = doc.createTextNode(name + "docTextNode4");
        
        Entity docEntity = (Entity) doc.getDoctype().getEntities().getNamedItem("ourEntityNode"); // Get the Entity node
        DocumentType docDocType = (DocumentType) doc.getFirstChild();   // Get the DocumentType node
        EntityReference entityReferenceText = (EntityReference) doc.getLastChild().getLastChild().getLastChild().getFirstChild();
        Text entityReferenceText2 = doc.createTextNode("entityReferenceText information");
        //************************************************* ERROR TESTS
     
        DOMExceptionsTest(doc, "appendChild", new Class[]{Node.class}, new Object[]{docBody}, DOMException.HIERARCHY_REQUEST_ERR); 
        DOMExceptionsTest(docNode3, "appendChild", new Class[]{Node.class}, new Object[]{docNode4}, DOMException.HIERARCHY_REQUEST_ERR); 
        DOMExceptionsTest(doc, "insertBefore", new Class[]{Node.class, Node.class}, new Object[]{docEntity, docFirstElement}, DOMException.HIERARCHY_REQUEST_ERR); 
        DOMExceptionsTest(doc, "replaceChild", new Class[]{Node.class, Node.class}, new Object[]{docCDATASection, docFirstElement}, DOMException.HIERARCHY_REQUEST_ERR); 
        docFirstElement.setNodeValue("This shouldn't do anything!");
        assertNull(docFirstElement.getNodeValue());
        docReferenceEntity.setNodeValue("This shouldn't do anything!");
        assertNull(docReferenceEntity.getNodeValue());
        docEntity.setNodeValue("This shouldn't do anything!");
        assertNull(docEntity.getNodeValue());
        doc.setNodeValue("This shouldn't do anything!");
        assertNull(doc.getNodeValue());
        docType.setNodeValue("This shouldn't do anything!");
        assertNull(docType.getNodeValue());
        docDocFragment.setNodeValue("This shouldn't do anything!");
        assertNull(docDocFragment.getNodeValue());
        docNotation.setNodeValue("This shouldn't do anything!");
        assertNull(docNotation.getNodeValue());
    
        DOMExceptionsTest(docReferenceEntity, "appendChild", new Class[]{Node.class}, new Object[]{entityReferenceText2}, DOMException.NO_MODIFICATION_ALLOWED_ERR);
        DOMExceptionsTest(docBodyLevel32, "insertBefore", new Class[]{Node.class, Node.class}, new Object[]{docTextNode11, docBody}, DOMException.NOT_FOUND_ERR);
        DOMExceptionsTest(docBodyLevel32, "removeChild", new Class[]{Node.class}, new Object[]{docFirstElement}, DOMException.NOT_FOUND_ERR);
        DOMExceptionsTest(docBodyLevel32, "replaceChild", new Class[]{Node.class, Node.class}, new Object[]{docTextNode11, docFirstElement}, DOMException.NOT_FOUND_ERR);
    
         //!! Throws a NOT_FOUND_ERR    ********
         // docBodyLevel32.getAttributes().removeNamedItem(testAttribute.getName());    16  // To test removeNamedItem         
    }

    private static void DOMExceptionsTest(Object node, String methodName, Class[] methodSignature, Object[] parameters, short code) {
        try {
            Method method = node.getClass().getMethod(methodName,methodSignature);
            method.invoke(node, parameters);
            fail("Expected exception " + code + " not thrown");
        } catch (InvocationTargetException exc) {
            Throwable realE = exc.getTargetException(); 
            if (realE instanceof DOMException) {
                assertEquals(code, ((DOMException) realE).code);
            }
            else {
              fail("Wrong Exception (" + realE.getClass().getName() + ")");
            }
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            fail();
        }
    }

    private Document document;

    protected void setUp() throws Exception {
        super.setUp();

        document = createDocument();
        DocumentType docDocType = createDocumentType(document, "testDocument1");
        document.appendChild(docDocType);
    
        Entity docEntity = createEntity(document, "ourEntityNode");
        Text entityChildText = document.createTextNode("entityChildText information"); // Build a branch for entityReference tests
            ((org.apache.xerces.dom.NodeImpl) docEntity).setReadOnly(false, true);
        docEntity.appendChild(entityChildText);
        // & for READONLY_ERR tests
        ((org.apache.xerces.dom.NodeImpl) docEntity).setReadOnly(true, true);
        docDocType.getEntities().setNamedItem(docEntity);

        docBuilder(document, "d");
    }
        
    // FIXME turn these into unit tests
    //!! Throws WRONG_DOCUMENT_ERR **********
            
        //  z.appendChild(d.createComment("Test doc d comment"));// Tries to append z document with document d comment
        //  d.getDocumentElement().appendChild(z.createElement("newZdocElement"));// Tries to append d document with document z Element
        //  d.getLastChild().getLastChild().insertBefore(z.createElement("newZdocElement"),d.getLastChild().getLastChild().getFirstChild());// Tries to insert into d document with document z Element
        //  d.replaceChild(z.createElement("newZdocElement"),d.getLastChild().getLastChild().getFirstChild());  // Tries to replace in d document with document z Element
    
        /*  Attribute newAttribute = d.createAttribute("newAttribute");
            d.getDocumentElement().setAttributeNode(newAttribute);
            d.getDocumentElement().getAttributes().setNamedItem(z.createAttribute("newzAttribute"));
        */
            
    //!! Throws INVALID_CHARACTER_ERR **********
        // ******This just gets us through each method. JKess has a comprehensive test of Invalid Names******
        //  d.createAttribute("Invalid Name"); // Name with blank space
        //  d.createElement("5InvalidName"); // Name starts with numeric
        //  d.createProcessingInstruction("This is the target processor channel","InvalidName>");// Name ends with >
        //  d.getDocumentElement().setAttribute("Invalid%Name",""); // Name contains %
            
    
    //!!    ******** NO_DATA_ALLOWED_ERR ********No cases to test as of 9/15
    
    
    //!!    ******** NO_MODIFICATION_ALLOWED_ERR ******** When read only exists
        /*
            
    
            
            //**** FOR Element when read only exists********
            .removeAttribute("aString");           //***** Not until read only exists.
            .removeAttributeNode(Attribute);       //***** Not until read only exists.
            .setAttribute("aString", "anotherString"); //***** Not until read only exists.
            
            
            //**** FOR Node when read only exists********
            .appendChild(aNode);            //***** Not until read only exists.
            .insertBefore(aNode, AnotherNode);  //***** Not until read only exists.
            .removeChild(aNode);            //***** Not until read only exists.
            .replaceChild(aNode);           //***** Not until read only exists.
            
            .splitText(2); //***** Not until read only exists.
            
            .setNamedItem(Node); //***** Not until read only exists.
        */
        
    
    //!!******** NOT_SUPPORTED_ERR  ********For HTML when implemented
        /*
            .createCDATASection("String stuff");
            .createEntityReference("String stuff");
            .createProcessingInstruction("String stuff", "Some more String stuff");
        */

    /**
     * This method tests Attr methods for the XML DOM implementation.
     */
    public void testAttr() {
        Attr testAttribute = document.createAttribute("testAttribute");
        testAttribute.setValue("testAttribute's value");
        Node node = document.getDocumentElement(); // node gets first element
        ((Element) node).setAttributeNode(testAttribute);
        Attr attributeNode = ((Element) node).getAttributeNode("testAttribute");
    
        assertEquals("testAttribute", attributeNode.getName());
        assertEquals("testAttribute's value", attributeNode.getNodeValue());
        assertTrue(attributeNode.getSpecified());
        
        attributeNode.setNodeValue("Reset Value");
        assertEquals("Reset Value", attributeNode.getNodeValue());
        ((org.apache.xerces.dom.AttrImpl) attributeNode).setSpecified(false); //***** How do we change this for external use??
        assertFalse(attributeNode.getSpecified());
        
        attributeNode.setValue(null);
        assertEquals(0, attributeNode.getValue().length());
        
        attributeNode.setValue("Another value ");
        assertEquals("Another value ", attributeNode.getValue());
    
        Attr cloneNode = (Attr) attributeNode.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the Attribute node name correctly", attributeNode.getName(), cloneNode.getName());
        assertEquals("'cloneNode' did not clone the Attribute node value correctly", attributeNode.getValue(), cloneNode.getValue());
        // Deep clone test comparison is in testNode & testDocument
    
        //************************************************* ERROR TESTS
        DOMExceptionsTest(document.getDocumentElement(),
                                      "appendChild",
                                      new Class[]{Node.class},
                                      new Object[]{attributeNode},
                                      DOMException.HIERARCHY_REQUEST_ERR);
    
        Attr attribute2 = document.createAttribute("testAttribute2");
        DOMExceptionsTest(document.getDocumentElement(),
                                      "removeAttributeNode",
                                      new Class[]{Attr.class},
                                      new Object[]{attribute2},
                                      DOMException.NOT_FOUND_ERR);
    
        Element element = (Element) document.getLastChild().getLastChild();
        // Tests setNamedItem
        DOMExceptionsTest(element,
                                  "setAttributeNode",
                                  new Class[]{Attr.class},
                                  new Object[]{testAttribute},
                                  DOMException.INUSE_ATTRIBUTE_ERR);
    }

    /**
     * This method tests CDATASection methods for the XML DOM implementation.
     */
    public void testCDATASection() {
        Node node = document.getDocumentElement().getElementsByTagName("dBodyLevel23").item(0).getFirstChild(); // node gets CDATASection node
        Node cloneNode = node.cloneNode(true);
        assertEquals("'cloneNode' did not clone the CDATA node name correctly", node.getNodeName(), cloneNode.getNodeName());
        assertEquals("'cloneNode' did not clone the CDATA node value correctly", node.getNodeValue(), cloneNode.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument            
    }

    /**
     * This method tests CharacterData methods for the XML DOM implementation.
     */
    public void testCharacterData() {
        CharacterData charData = (CharacterData) document.getDocumentElement().getElementsByTagName("dBodyLevel31").item(0).getFirstChild(); // charData gets textNode11
        String compareData = "dBodyLevel31'sChildTextNode11";
        assertEquals(compareData, charData.getData());
            
        String resetData = charData.getData();
    
        String newData = " This is new data for this node";
        compareData = charData.getData() + newData;
        charData.appendData(newData);
        assertEquals(compareData, charData.getData());
    
        compareData = "dBodyLevel";
        charData.deleteData(10, 100);
        assertEquals("CharacterData's 'deleteData' failed to work properly!", compareData, charData.getData());
    
        int length = 10;
        assertEquals("CharacterData's 'getLength' failed to work properly!", length, charData.getLength());
    
        compareData = "dBody' This is data inserted into this node'Level";
        charData.insertData(5, "' This is data inserted into this node'");
        assertEquals("CharacterData's 'insertData' failed to work properly!", compareData, charData.getData());

        compareData = "dBody' This is ' replacement data'ted into this node'Level";
        charData.replaceData(15, 10, "' replacement data'");
        assertEquals("CharacterData's 'replaceData' failed to work properly!", compareData, charData.getData());

        compareData = "New data A123456789B123456789C123456789D123456789E123456789";
        charData.setData("New data A123456789B123456789C123456789D123456789E123456789");
        assertEquals("CharacterData's 'setData' failed to work properly!", compareData, charData.getData());

        compareData = "123456789D123456789E123456789";
        assertEquals("CharacterData's 'substringData' failed to work properly!", compareData, charData.substringData(30, 30));
    
        compareData = "New data A123456789B12345";
        assertEquals("CharacterData's 'substringData' failed to work properly!", compareData, charData.substringData(0, 25));
    
        //************************************************* ERROR TESTS
    
        //!! Throws INDEX_SIZE_ERR ********************
        DOMExceptionsTest(charData, "deleteData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(-1),Integer.valueOf(5) }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "deleteData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(2),Integer.valueOf(-1) }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "deleteData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(100),Integer.valueOf(5) }, DOMException.INDEX_SIZE_ERR);
        
        DOMExceptionsTest(charData, "insertData", new Class[]{int.class, String.class}, 
                new Object[]{Integer.valueOf(-1),"Stuff inserted" }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "insertData", new Class[]{int.class, String.class}, 
                new Object[]{Integer.valueOf(100),"Stuff inserted" }, DOMException.INDEX_SIZE_ERR);
        
        DOMExceptionsTest(charData, "replaceData", new Class[]{int.class, int.class, String.class}, 
                new Object[]{Integer.valueOf(-1),Integer.valueOf(5),"Replacement stuff" }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "replaceData", new Class[]{int.class, int.class, String.class}, 
                new Object[]{Integer.valueOf(100),Integer.valueOf(5),"Replacement stuff" }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "replaceData", new Class[]{int.class, int.class, String.class}, 
                new Object[]{Integer.valueOf(2),Integer.valueOf(-1),"Replacement stuff" }, DOMException.INDEX_SIZE_ERR);
        
        DOMExceptionsTest(charData, "substringData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(-1),Integer.valueOf(5) }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "substringData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(100),Integer.valueOf(5) }, DOMException.INDEX_SIZE_ERR);
        DOMExceptionsTest(charData, "substringData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(2),Integer.valueOf(-1) }, DOMException.INDEX_SIZE_ERR);
        
        //!! Throws NO_MODIFICATION_ALLOWED_ERR ******** 
        Node node = document.getDocumentElement().getElementsByTagName("dBodyLevel24").item(0).getFirstChild().getChildNodes().item(0); // node gets ourEntityReference node's child text
    
        DOMExceptionsTest(node, "appendData", new Class[]{String.class}, 
                new Object[]{"new data" }, DOMException.NO_MODIFICATION_ALLOWED_ERR);
        DOMExceptionsTest(node, "deleteData", new Class[]{int.class, int.class}, 
                new Object[]{Integer.valueOf(5),Integer.valueOf(10) }, DOMException.NO_MODIFICATION_ALLOWED_ERR);
        DOMExceptionsTest(node, "insertData", new Class[]{int.class, String.class}, 
                new Object[]{Integer.valueOf(5),"Stuff inserted" }, DOMException.NO_MODIFICATION_ALLOWED_ERR);
        DOMExceptionsTest(node, "replaceData", new Class[]{int.class, int.class, String.class}, 
                new Object[]{Integer.valueOf(5),Integer.valueOf(10),"Replacementstuff" }, DOMException.NO_MODIFICATION_ALLOWED_ERR);
        DOMExceptionsTest(node, "setData", new Class[]{String.class}, 
                new Object[]{"New setdata stuff"}, DOMException.NO_MODIFICATION_ALLOWED_ERR);                    
     }

    /**
     * This method tests ChildNodeList methods for the XML DOM implementation.
     */
    public void testChildNodeList() {
        Node node = document.getDocumentElement().getLastChild(); // node gets doc's testBody element
        assertEquals(4, node.getChildNodes().getLength());

        Node node2 = node.getChildNodes().item(2);
        assertEquals("dBodyLevel23", node2.getNodeName());        
    }

    /**
     * This method tests Comment methods for the XML DOM implementation.
     */
     public void testComment() {
         Node parent = document.getDocumentElement().getElementsByTagName("dBodyLevel23").item(0);
         Node node = null;
         NodeList children = parent.getChildNodes();
         for (int i = 0; i < children.getLength(); i++) {
             Node child = children.item(i);
             if (child.getNodeType() == Node.COMMENT_NODE) {
                 node = child;
                 break;
             }
         }
         assertNotNull("Expected a Comment node under dBodyLevel23", node);
         assertEquals("'testComment' did not select a Comment node", Node.COMMENT_NODE, node.getNodeType());
         Node cloneNode = node.cloneNode(true);
         assertEquals("'cloneNode' did not clone a Comment node", Node.COMMENT_NODE, cloneNode.getNodeType());

         // Check nodes for equality, both their name and value
         assertEquals("'cloneNode' did not clone the Comment node name correctly", node.getNodeName(), cloneNode.getNodeName());
         assertEquals("'cloneNode' did not clone the Comment node value correctly", node.getNodeValue(), cloneNode.getNodeValue());
    }

    /**
     * This method tests DeepNodeList methods for the XML DOM implementation
     */
    public void testDeepNodeList() {  
        Node node = document.getLastChild().getLastChild(); // node gets docBody element
        assertEquals(8, ((Element) node).getElementsByTagName("*").getLength());

        Node dBodyLevel32 = ((Element) node).getElementsByTagName("*").item(2); //This also runs through 'nextMatchingElementAfter"
        assertEquals("dBodyLevel32", dBodyLevel32.getNodeName());

        Node dTestBody = document.getLastChild();
        assertEquals("dTestBody", ((Element) dTestBody).getElementsByTagName("dTestBody").item(0).getNodeName());
    }

    /**
     * This method tests Document methods for the XML DOM implementation.
     *
     * ALL Document create methods are run in docBuilder except createAttribute which is in testAttribute.
     */
    public void testDocument() {
        String[] elementNames =  {"dFirstElement", "dTestBody", "dBodyLevel21","dBodyLevel31","dBodyLevel32",
                       "dBodyLevel22", "dBodyLevel33", "dBodyLevel34", "dBodyLevel23", "dBodyLevel24"};
        String[] newElementNames = {"dFirstElement", "dTestBody", "dBodyLevel22", "dBodyLevel33", "dBodyLevel34", "dBodyLevel23"};
        
        DocumentType checkDocType = createDocumentType(document, "testDocument1");
        DocumentType docType = document.getDoctype();
        assertEquals(checkDocType.getNodeName(), docType.getNodeName());
        assertEquals(checkDocType.getNodeValue(), docType.getNodeValue());
            
        Node rootElement = document.getLastChild();
        assertEquals(rootElement.getNodeName(), document.getDocumentElement().getNodeName());
        assertEquals(rootElement.getNodeValue(), document.getDocumentElement().getNodeValue());
        
        NodeList docElements = document.getElementsByTagName("*");
        int docSize = docElements.getLength();
        for (int i = 0; i < docSize; i++) {
            Node n = (Node) docElements.item(i);
            assertEquals(elementNames[i], n.getNodeName());
        }

        assertFalse(document.equals(document.getImplementation()));
            
        Element newElement = document.createElement("NewElementTestsInsertBefore");
        //  doc.insertBefore(newElement,null);//!! Throws a HIERARCHY_REQUEST_ERR   ******* 
        //  doc.removeChild(docElements.item(9));//!! Throws a NOT_FOUND_ERR  ********
    
        DocumentFragment docFragment = document.createDocumentFragment();
        //Tests removeChild and stores removed branch for tree reconstruction
        docFragment.appendChild(docElements.item(1).removeChild(docElements.item(9)));
        DocumentFragment docFragment2 = document.createDocumentFragment();
        //Tests removeChild and stores removed branch for tree reconstruction
        docFragment2.appendChild(docElements.item(1).removeChild(docElements.item(2)));
        docSize = docElements.getLength();
        for (int i = 0; i < docSize; i++) {
            Node n = (Node) docElements.item(i);
            assertEquals(newElementNames[i], n.getNodeName());
        }
        docElements.item(1).insertBefore(docFragment, null); //Reattaches removed branch to restore tree to the original
        docElements.item(1).insertBefore(docFragment2, docElements.item(2)); //Reattaches removed branch to restore tree to the original
        
        docSize = docElements.getLength();
        for (int i = 0; i < docSize; i++) {
            Node n = (Node) docElements.item(i);
            assertEquals("Comparison of restored document's elements failed at element number " + i + " : " + n.getNodeName(), elementNames[i], n.getNodeName());
        }
    
    //  DTest tests = new DTest();
    //  Document z = tests.createDocument();
    //  tests.docBuilder(z, "z");
    
    //!! Throws WRONG_DOCUMENT_ERR **********
    //  OK &= Assertion.assert(tests.DOMExceptionsTest(z, "appendChild", new Class[]{Node.class}, new Object[]{doc.createComment("Test doc d comment")}, DOMException.HIERARCHY_REQUEST_ERR )); 
            
        //  z.appendChild(d.createComment("Test doc d comment"));// Tries to append z document with document d comment
        //  d.getDocumentElement().appendChild(z.createElement("newZdocElement"));// Tries to append d document with document z Element
        //  d.getLastChild().getLastChild().insertBefore(z.createElement("newZdocElement"),d.getLastChild().getLastChild().getFirstChild());// Tries to insert into d document with document z Element
        //  d.replaceChild(z.createElement("newZdocElement"),d.getLastChild().getLastChild().getFirstChild());  // Tries to replace in d document with document z Element
    
        //  doc.setNodeValue("This shouldn't work");//!! Throws a NO_MODIFICATION_ALLOWED_ERR ********
        
        Node node = document;
        Node node2 = document.cloneNode(true);
        boolean result = treeCompare(node, node2); // Deep clone test comparison of document cloneNode
        assertTrue("Deep clone of the document failed!", result);
    
        // check on the ownerDocument of the cloned nodes
        Document doc2 = (Document) node2;
        assertSame(doc2.getDocumentElement().getOwnerDocument(), doc2);
    
        // Deep clone test comparison is also in testNode
    
        // try adding a new element to the cloned document
        node2 = doc2.createElement("foo");
        doc2.getDocumentElement().appendChild(node2);        
    }

    /**
     * This method tests DocumentFragment methods for the XML DOM implementation     *
     * FIXME exists to throw NO_MODIFICATION_ALLOWED_ERR ********
     */
    public void testDocumentFragment() {
        DocumentFragment testDocFragment = document.createDocumentFragment();
            
        //  testDocFragment.setNodeValue("This is a document fragment!");//!! Throws a NO_MODIFICATION_ALLOWED_ERR ********        
    }

    /**
     * This method tests DocumentType methods for the XML DOM implementation
     */
    public void testDocumentType() {
        Node node = document.getFirstChild(); // node gets doc's docType node
        Node node2 = node.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the document type node name correctly", node.getNodeName(), node2.getNodeName());
        assertEquals("'cloneNode' did not clone the document type node value correctly", node.getNodeValue(), node2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        DocumentType docType = (DocumentType) document.getFirstChild();
        NamedNodeMap docEntityMap = docType.getEntities();
        assertEquals("ourEntityNode", docEntityMap.item(0).getNodeName());

        NamedNodeMap docNotationMap = docType.getNotations();
        assertEquals("ourNotationNode", docNotationMap.item(0).getNodeName());

        //  doc.appendChild(newDocumentTypeImpl);//!! Throws a HIERARCHY_REQUEST_ERR    ******* 
        DocumentType newDocumentType = createDocumentType(document, "TestDocument");
        document.removeChild(document.getFirstChild()); // Tests removeChild
        document.insertBefore(newDocumentType, document.getDocumentElement());
        //** Other aspects of insertBefore are tested in docBuilder through appendChild*   
    }

    public void testDOMErrors() {
        try {
            Element element = document.createElement("AnotherElement");
            document.appendChild(element);
            fail();
        } catch (DOMException expected) {
            assertEquals(DOMException.HIERARCHY_REQUEST_ERR, expected.code);
        }
        try {
            Text textNode = document.createTextNode("some more text");
            Text textNode2 = document.createTextNode("yet more text");
            textNode.appendChild(textNode2);
            fail();
        } catch (DOMException expected) {
            assertEquals(DOMException.HIERARCHY_REQUEST_ERR, expected.code);
        }

    //  OK &= Assertion.assert(tests.DOMExceptionsTest(document, "insertBefore", new Class[]{Node.class, Node.class}, new Object[]{document.getElementsByTagName("docEntity").item(0), document.getElementsByTagName("docFirstElement").item(0)}, DOMException.HIERARCHY_REQUEST_ERR )); 
    //  OK &= Assertion.assert(tests.DOMExceptionsTest(document, "replaceChild", new Class[]{Node.class, Node.class}, new Object[]{document.getElementsByTagName("docCDATASection").item(0), document.getElementsByTagName("docFirstElement").item(0)}, DOMException.HIERARCHY_REQUEST_ERR )); 
    
    //  OK &= Assertion.assert(tests.DOMExceptionsTest(document.getElementsByTagName("docFirstElement").item(0), "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR )); 
    /*  OK &= Assertion.assert(tests.DOMExceptionsTest(docReferenceEntity, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docEntity, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(document, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docDocType, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docDocFragment, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docNotation, "setNodeValue", new Class[]{String.class}, new Object[]{"This shouldn't work!" }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docReferenceEntity, "appendChild", new Class[]{Node.class}, new Object[]{entityReferenceText2 }, DOMException.NO_MODIFICATION_ALLOWED_ERR ));
    
    
        OK &= Assertion.assert(tests.DOMExceptionsTest(docBodyLevel32, "insertBefore", new Class[]{Node.class, Node.class}, new Object[]{docTextNode11,docBody }, DOMException.NOT_FOUND_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docBodyLevel32, "removeChild", new Class[]{Node.class}, new Object[]{docFirstElement}, DOMException.NOT_FOUND_ERR ));
        OK &= Assertion.assert(tests.DOMExceptionsTest(docBodyLevel32, "replaceChild", new Class[]{Node.class, Node.class}, new Object[]{docTextNode11,docFirstElement }, DOMException.NOT_FOUND_ERR ));
    */
    
    //!! Throws a NOT_FOUND_ERR ********
         
         // docBodyLevel32.getAttributes().removeNamedItem(testAttribute.getName());    16  // To test removeNamedItem
    }

    /**
     * This method tests DOMImplementation methods for the XML DOM implementation.
     */
    public void testDOMImplementation() {
        DOMImplementation implementation = document.getImplementation(); 
    
        assertTrue(implementation.hasFeature("XML", "1.0"));
        assertFalse(implementation.hasFeature("HTML", "4.0"));
    }

    /**
     * This method tests Element methods for the XML DOM implementation
     */
    public void testElement() {
        String[] attributeCompare = {"AnotherFirstElementAttribute", "dFirstElement", "testAttribute"};
        String[] elementNames =  {"dFirstElement", "dTestBody", "dBodyLevel21","dBodyLevel31","dBodyLevel32",
                       "dBodyLevel22","dBodyLevel33","dBodyLevel34","dBodyLevel23","dBodyLevel24"};
        String[] textCompare = {"dBodyLevel31'sChildTextNode11", "dBodyLevel31'sChildTextNode12", "dBodyLevel31'sChildTextNode13"};
        boolean OK = true;
        Node node = document.getDocumentElement(); // node gets doc's firstElement
        Node node2 = node.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the element node name correctly", node.getNodeName(), node2.getNodeName());
        assertEquals("'cloneNode' did not clone the element node value correctly", node.getNodeValue(), node2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument

        Element element = document.getDocumentElement(); // element gets doc's firstElement
        String compare = "";
        String attribute = element.getAttribute(document + "'s test attribute");
        assertEquals("", element.getAttribute(document + "'s test attribute"));
        assertNull(element.getAttributeNode(document + "FirstElement"));
        
        Attr newAttributeNode = document.createAttribute("AnotherFirstElementAttribute");
        newAttributeNode.setValue("A new attribute which helps test calls in Element");
        element.setAttributeNode(newAttributeNode);
        NamedNodeMap nodeMap = element.getAttributes();
        int size = nodeMap.getLength();
        for (int k = 0; k < size; k++) {
            Node n = (Node) nodeMap.item(k);
            assertEquals(attributeCompare[k], n.getNodeName());
        }
        NodeList docElements = document.getElementsByTagName("*");
        int docSize = docElements.getLength();
        for (int i = 0; i < docSize; i++) {
            Node n = (Node) docElements.item(i);
            assertEquals(elementNames[i], n.getNodeName());     
        }
        element = (Element) document.getElementsByTagName("dBodyLevel21").item(0); // element gets Element test BodyLevel21 
        Element element2 = (Element) document.getElementsByTagName("dBodyLevel31").item(0); // element2 gets Element test BodyLevel31 
        NodeList text = ((Node) element2).getChildNodes();
        int textSize = text.getLength();
        for (int j = 0; j < textSize; j++) {
            Node n = (Node) text.item(j);
            assertEquals(textCompare[j], n.getNodeValue());     
        }
        element = document.getDocumentElement(); // element gets doc's firstElement
        element.normalize(); // Concatenates all adjacent text nodes in this element's subtree
        NodeList text2 = ((Node) element2).getChildNodes();
        compare = "dBodyLevel31'sChildTextNode11dBodyLevel31'sChildTextNode12dBodyLevel31'sChildTextNode13";
        Node n = (Node) text2.item(0);
        assertEquals(compare, n.getNodeValue());
        
        element.setAttribute("FirstElementLastAttribute", "More attribute stuff for firstElement!!");
        element.removeAttribute("FirstElementLastAttribute");
        element.removeAttributeNode(newAttributeNode);
    
        //  doc.getLastChild().setNodeValue("This shouldn't work");//!! Throws a NO_MODIFICATION_ALLOWED_ERR***
    }

    /**
     * This method tests Entity methods for the XML DOM implementation.
     */
    public void testEntity() {
        boolean OK = true;
        Entity entity = (Entity) document.getDoctype().getEntities().getNamedItem("ourEntityNode");
        Node node = entity;
        Node node2 = entity.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the entity node name correctly", node.getNodeName(), node2.getNodeName());
        assertEquals("'cloneNode' did not clone the entity node value correctly", node.getNodeValue(), node2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        ((org.apache.xerces.dom.EntityImpl) entity).setNotationName("testNotationName");
        String compare = "testNotationName";

        assertEquals("testNotationName", entity.getNotationName());

        ((org.apache.xerces.dom.EntityImpl) entity).setPublicId("testPublicId");
        assertEquals("testPublicId", entity.getPublicId());
 
        ((org.apache.xerces.dom.EntityImpl) entity).setSystemId("testSystemId");
        assertEquals("testSystemId", entity.getSystemId());

        //  entity.setNodeValue("This shouldn't work");//!! Throws a NO_MODIFICATION_ALLOWED_ERR ********        
    }

    /**
     * This method tests EntityReference methods for the XML DOM implementation.
     */
    public void testEntityReference() {
        EntityReference entityReference = (EntityReference) document.getLastChild().getLastChild().getLastChild().getFirstChild();
        Node node = entityReference;
        Node node2 = node.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the entity reference node name correctly", node.getNodeName(), node2.getNodeName());
        assertEquals("'cloneNode' did not clone the entity reference node value correctly", node.getNodeValue(), node2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        //  entityReference.setNodeValue("This shouldn't work");//!! Throws a NO_MODIFICATION_ALLOWED_ERR ********        
    }

    /**
     * This method tests Node methods for the XML DOM implementation.
     ********* This is only for a test of cloneNode "deep"*******
     ********* And for error tests*********
     */
    public void testNode() {
        Node node = document.getDocumentElement();
        Node cloneNode = node.cloneNode(true);
        assertTrue(treeCompare(node, cloneNode)); // Deep clone test of cloneNode

        //!! The following gives a did not clone successfully message*********
        Node node2 = node.getFirstChild();
        assertFalse(treeCompare(node, node2));

        // Deep clone test also in testDocument
    }

    /**
     * This method tests Notation methods for the XML DOM implementation.
     */
    public void testNotation() {
        Notation notation = (Notation) document.getDoctype().getNotations().getNamedItem("ourNotationNode");
        Node cloneNode = notation.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the notation node name correctly", notation.getNodeName(), cloneNode.getNodeName());
        assertEquals("'cloneNode' did not clone the notation node value correctly", notation.getNodeValue(), cloneNode.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        ((org.apache.xerces.dom.NotationImpl) notation).setPublicId("testPublicId");
        ((org.apache.xerces.dom.NotationImpl) notation).setSystemId("testSystemId");
        assertEquals("testPublicId", notation.getPublicId());
        assertEquals("testSystemId", notation.getSystemId());

        //  notation.setNodeValue("This shouldn't work");//!! Throws a NO_MODIFICATION_ALLOWED_ERR ********
    }

    /**
     * This method tests ProcessingInstruction methods for the XML DOM implementation.
     */
    public void testPI() {
        ProcessingInstruction pI = (ProcessingInstruction) document.getDocumentElement().getFirstChild();// Get doc's ProcessingInstruction
        ProcessingInstruction pI2 = (org.apache.xerces.dom.ProcessingInstructionImpl) pI.cloneNode(true);
        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the PI node name correctly", pI.getNodeName(), pI2.getNodeName());
        assertEquals("'cloneNode' did not clone the PI node value correctly", pI.getNodeValue(), pI2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        String compare = "This is [#document: null]'s processing instruction";
        assertEquals("PI's 'getData' failed!", compare, pI.getData());
        
        pI.setData("PI's reset data");
        compare = "PI's reset data";
        assertEquals("PI's 'resetData' failed!", compare, pI.getData());
 
        compare = "dTargetProcessorChannel";
        assertEquals("PI's 'getTarget' failed!", compare, pI.getTarget());
    }

    /**
     * This method tests Text methods for the XML DOM implementation.
     */
    public void testText() {
        document.getDocumentElement().normalize();
        Node node = document.getDocumentElement().getElementsByTagName("dBodyLevel31").item(0).getFirstChild(); // charData gets textNode11
        Text text = (Text) node;
        Node node2 = node.cloneNode(true);

        // Check nodes for equality, both their name and value or lack thereof
        assertEquals("'cloneNode' did not clone the text node name correctly", text.getNodeName(), node2.getNodeName());
        assertEquals("'cloneNode' did not clone the text node value correctly", text.getNodeValue(), node2.getNodeValue());
        // Deep clone test comparison is in testNode & testDocument
    
        text.splitText(25);
        // Three original text nodes were concatenated by 'normalize'
        String compare = "dBodyLevel31'sChildTextNo"; 
        assertEquals("First part of Text's split text failed!", compare, text.getNodeValue());
        compare = "de11dBodyLevel31'sChildTextNode12dBodyLevel31'sChildTextNode13";
        assertEquals("Second part of Text's split text failed!", compare, text.getNextSibling().getNodeValue());

        // ERROR TESTS
        //!! Throws INDEX_SIZE_ERR ********************
        //  text.splitText(-1);
        //  text.splitText(100);        
    }

    private static boolean treeCompare(Node node, Node node2) {            
        // Check the subtree for equality
        Node kid = node.getFirstChild();
        Node kid2 = node2.getFirstChild();

        if (kid != null && kid2 != null) {
            if (!treeCompare(kid, kid2)) {
                return false;
            }
            else if (kid.getNextSibling() != null && kid2.getNextSibling() != null) {
                while (kid.getNextSibling() != null && kid2.getNextSibling() != null) {
                    if (!treeCompare(kid.getNextSibling(), kid2.getNextSibling())) {
                        return false;
                    }
                    else {
                        kid = kid.getNextSibling();
                        kid2 = kid2.getNextSibling();
                    }
                }
            }
            else if (!(kid.getNextSibling() == null && kid2.getNextSibling() == null)) {
                return false;
            }
        }
        else if (kid != kid2) {
            return false;
        }

        // Check nodes for equality, both their name and value or lack thereof
        if (!Objects.equals(node.getNodeName(), node2.getNodeName())) {
            return false;
        }
        if (!Objects.equals(node.getNodeValue(), node2.getNodeValue())) {
            return false;
        }

        return true;
    }
}
