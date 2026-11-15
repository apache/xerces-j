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

package dom.range;

import java.io.StringReader;

import junit.framework.TestCase;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ranges.Range;
import org.xml.sax.InputSource;

/** 
 * This RangeTest tests all of the cases delineated as examples
 * in the DOM Level 2 Range specification, and a few others.
 * <p>These do not by any means completely test the API and 
 * corner cases.
 * 
 * @version $Id$
 */
public class Test extends TestCase {
    
    static final boolean DEBUG = false;
    
    static final String [] tests = {
        "<FOO>AB<MOO>CD</MOO>CD</FOO>",
        "<FOO>A<MOO>BC</MOO>DE</FOO>",
        "<FOO>XY<BAR>ZW</BAR>Q</FOO>",
        "<FOO><BAR1>AB</BAR1><BAR2/><BAR3>CD</BAR3></FOO>",
        "<A><B><M/><C><D/><E/><F/><HELLO/></C><N/><O/></B>"+
        "<Z><X/><Y/></Z>"+
        "<G/><Q><V/><W/></Q></A>"
    };
    static final String [] deleteResult = {
        "<FOO>ACD</FOO>",
        "<FOO>A<MOO>B</MOO>E</FOO>",
        "<FOO>X<BAR>W</BAR>Q</FOO>",
        "<FOO><BAR1>A</BAR1><BAR3>D</BAR3></FOO>",
        "<A><B><M></M><C><D></D></C></B><Q><W></W></Q></A>"
    };
    static final String [] extractResult = {
        "B<MOO>CD</MOO>",
        "<MOO>C</MOO>D",
        "Y<BAR>Z</BAR>",
        "<BAR1>B</BAR1><BAR2></BAR2><BAR3>C</BAR3>",
        "<B><C><E></E><F></F><HELLO></HELLO></C>"+
        "<N></N><O></O></B><Z><X></X><Y></Y></Z><G></G><Q><V></V></Q>"
    };
    
    static final String INSERT="***";
    static final String [] insertResult = {
"<FOO>A"+INSERT+"B<MOO>CD</MOO>CD</FOO>",
"<FOO>A<MOO>B"+INSERT+"C</MOO>DE</FOO>",
"<FOO>X"+INSERT+"Y<BAR>ZW</BAR>Q</FOO>",
"<FOO><BAR1>A"+INSERT+"B</BAR1><BAR2></BAR2><BAR3>CD</BAR3></FOO>",
"<A><B><M></M><C><D></D>"+INSERT+"<E></E><F></F><HELLO></HELLO></C>"+
"<N></N><O></O></B><Z><X></X><Y></Y></Z><G></G><Q><V></V><W></W></Q></A>"
    };
    

    static final String SURROUND="SURROUND";

    static final String [] surroundResult = {
"<FOO>A<"+SURROUND+">B<MOO>CD</MOO>C</"+SURROUND+">D</FOO>",
"<FOO>A<MOO>B<"+SURROUND+">C</"+SURROUND+"></MOO>DE</FOO>",
"<FOO>X<"+SURROUND+">Y<BAR>ZW</BAR></"+SURROUND+">Q</FOO>",
"<FOO><BAR1>AB</BAR1><"+SURROUND+"><BAR2></BAR2></"+SURROUND+"><BAR3>CD</BAR3></FOO>",
"<A><B><M></M><C><D></D><E></E><F></F><HELLO></HELLO></C>"+
"<N></N><O></O></B><Z><"+SURROUND+"><X></X><Y></Y></"+SURROUND+"></Z>"+
"<G></G><Q><V></V><W></W></Q></A>"
    };
    
    static final String [] rangeDelete = {
    "<P>Abcd efgh The Range ijkl</P>",
    "<p>Abcd efgh The Range ijkl</p>",
    "<P>ABCD efgh The <EM>Range</EM> ijkl</P>",
    "<P>Abcd efgh The Range ijkl</P>",
    "<P>Abcd <EM>efgh The Range ij</EM>kl</P>"
    };
    //
    static final String [] rangeDeleteResult = {
    "<P>Abcd ^Range ijkl</P>",
    "<p>Abcd ^kl</p>",
    "<P>ABCD ^<EM>ange</EM> ijkl</P>",
    "<P>Abcd ^he Range ijkl</P>",
    "<P>Abcd ^kl</P>"
    };
    
    static final String INSERT2="<P>Abcd efgh XY blah ijkl</P>";
    static final String INSERTED_TEXT = "INSERTED TEXT";
    
    static final String [] rangeInsertResult = {
    "<P>Abcd efgh INSERTED TEXTXY blah ijkl</P>",
    "<P>Abcd efgh XINSERTED TEXTY blah ijkl</P>",
    "<P>Abcd efgh XYINSERTED TEXT blah ijkl</P>",
    "<P>Abcd efgh XY blahINSERTED TEXT ijkl</P>"
    };
    
    private DOMParser parser;
    
    protected void setUp() throws Exception {
        parser = new DOMParser();
    }
    
    public void testDelete() throws Exception {
        for (int i = 0; i < tests.length; i++) {
            parser.parse(new InputSource(new StringReader(tests[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Node root = document.getDocumentElement();
            
            if (i == 0) { 
                range.setStart(root.getFirstChild(), 1);
                range.setEndBefore(root.getLastChild());
            }
            else if (i == 1) {
                Node n1 = root.getFirstChild().getNextSibling().
                getFirstChild();
                range.setStart(n1, 1);
                range.setEnd(root.getLastChild(), 1);
            }
            else if (i == 2) {
                range.setStart(root.getFirstChild(), 1);
                Node n2 = root.getFirstChild().getNextSibling().getFirstChild();
                range.setEnd(n2, 1);
            }
            else if (i == 3) {
                Node n3 = root.getFirstChild().getFirstChild();
                range.setStart(n3, 1);
                range.setEnd(root.getLastChild().getFirstChild(), 1);
            }
            else if (i == 4) {
                Node n4 = root.getFirstChild().getFirstChild().getNextSibling().getFirstChild();
                range.setStartAfter(n4);
                range.setEndAfter(root.getLastChild().getFirstChild());
            }
            
            range.deleteContents();
            String result = toString(document);
            assertEquals("Delete test[" + i + "]", deleteResult[i], result);
        }
    }
    
    public void testExtract() throws Exception {
        for (int i = 0; i < tests.length; i++) {
            parser.parse(new InputSource(new StringReader(tests[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Node root = document.getDocumentElement();
            
            if (i == 0) { 
                range.setStart(root.getFirstChild(), 1);
                range.setEndBefore(root.getLastChild());
            }
            else if (i == 1) {
                Node n1 = root.getFirstChild().getNextSibling().
                getFirstChild();
                range.setStart(n1, 1);
                range.setEnd(root.getLastChild(), 1);
            }
            else if (i == 2) {
                range.setStart(root.getFirstChild(), 1);
                Node n2 = root.getFirstChild().getNextSibling().getFirstChild();
                range.setEnd(n2, 1);
            }
            else if (i == 3) {
                Node n3 = root.getFirstChild().getFirstChild();
                range.setStart(n3, 1);
                range.setEnd(root.getLastChild().getFirstChild(), 1);
            }
            else if (i == 4) {
                Node n4 = root.getFirstChild().getFirstChild().getNextSibling().getFirstChild();
                range.setStartAfter(n4);
                range.setEndAfter(root.getLastChild().getFirstChild());
            }
            
            DocumentFragment frag = range.extractContents();
            String result = toString(document);
            assertEquals("Extract document test[" + i + "]", deleteResult[i], result);
            String fragResult = toString(frag);
            assertEquals("Extract fragment test[" + i + "]", extractResult[i], fragResult);
        }
    }
    
    public void testClone() throws Exception {
        for (int i = 0; i < tests.length; i++) {
            parser.parse(new InputSource(new StringReader(tests[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Node root = document.getDocumentElement();
            
            if (i == 0) { 
                range.setStart(root.getFirstChild(), 1);
                range.setEndBefore(root.getLastChild());
            }
            else if (i == 1) {
                Node n1 = root.getFirstChild().getNextSibling().
                getFirstChild();
                range.setStart(n1, 1);
                range.setEnd(root.getLastChild(), 1);
            }
            else if (i == 2) {
                range.setStart(root.getFirstChild(), 1);
                Node n2 = root.getFirstChild().getNextSibling().getFirstChild();
                range.setEnd(n2, 1);
            }
            else if (i == 3) {
                Node n3 = root.getFirstChild().getFirstChild();
                range.setStart(n3, 1);
                range.setEnd(root.getLastChild().getFirstChild(), 1);
            }
            else if (i == 4) {
                Node n4 = root.getFirstChild().getFirstChild().getNextSibling().getFirstChild();
                range.setStartAfter(n4);
                range.setEndAfter(root.getLastChild().getFirstChild());
            }
            
            DocumentFragment frag = range.cloneContents();
            String fragResult = toString(frag);
            assertEquals("Clone test[" + i + "]", extractResult[i], fragResult);
        }
    }
    
    public void testInsert() throws Exception {
        for (int i = 0; i < tests.length; i++) {
            parser.parse(new InputSource(new StringReader(tests[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Node root = document.getDocumentElement();
            
            if (i == 0) { 
                range.setStart(root.getFirstChild(), 1);
                range.setEndBefore(root.getLastChild());
            }
            else if (i == 1) {
                Node n1 = root.getFirstChild().getNextSibling().
                getFirstChild();
                range.setStart(n1, 1);
                range.setEnd(root.getLastChild(), 1);
            }
            else if (i == 2) {
                range.setStart(root.getFirstChild(), 1);
                Node n2 = root.getFirstChild().getNextSibling().getFirstChild();
                range.setEnd(n2, 1);
            }
            else if (i == 3) {
                Node n3 = root.getFirstChild().getFirstChild();
                range.setStart(n3, 1);
                range.setEnd(root.getLastChild().getFirstChild(), 1);
            }
            else if (i == 4) {
                Node n4 = root.getFirstChild().getFirstChild().getNextSibling().getFirstChild();
                range.setStartAfter(n4);
                range.setEndAfter(root.getLastChild().getFirstChild());
            }
            
            range.insertNode(document.createTextNode(INSERT));
            String result = toString(document);
            assertEquals("Insert test[" + i + "]", insertResult[i], result);
        }
    }
    
    public void testSurround() throws Exception {
        for (int i = 0; i < tests.length; i++) {
            parser.parse(new InputSource(new StringReader(tests[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Node root = document.getDocumentElement();
            Node surroundNode = document.createElement(SURROUND);
            
            if (i == 0) { 
                range.setStart(root.getFirstChild(), 1);
                range.setEnd(root.getLastChild(), 1);
            }
            else if (i == 1) {
                Node n1 = root.getFirstChild().getNextSibling().
                getFirstChild();
                range.setStart(n1, 1);
                range.setEnd(n1, 2);
            }
            else if (i == 2) {
                range.setStart(root.getFirstChild(), 1);
                Node n2 = root.getFirstChild().getNextSibling().getFirstChild();
                range.setEnd(n2, 1);
                range.setEndBefore(root.getLastChild());
            }
            else if (i == 3) {
                Node n3 = root.getFirstChild().getFirstChild();
                range.setStart(n3, 1);
                range.setEnd(root.getLastChild().getFirstChild(), 1);
                range.selectNode(root.getFirstChild().getNextSibling());
            }
            else if (i == 4) {
                Node n4 = root.getFirstChild().getFirstChild().getNextSibling().getFirstChild();
                range.setStartAfter(n4);
                range.setEndAfter(root.getLastChild().getFirstChild());
                range.selectNodeContents(root.getFirstChild().getNextSibling());
            }
            
            // TODO: surroundContents may throw RangeException for some cases
            try {
                range.surroundContents(surroundNode);
            } catch (org.w3c.dom.ranges.RangeException e) {
            }
            String result = toString(document);
            assertEquals("Surround test[" + i + "]", surroundResult[i], result);
        }
    }
    
    public void testInsert2() throws Exception {
        for (int i = 0; i < 4; i++) {
            parser.parse(new InputSource(new StringReader(INSERT2)));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Node root = document.getDocumentElement();
            Range range = document.createRange();
            range.setStart(root.getFirstChild(), 11);
            range.setEnd(root.getFirstChild(), 18);
            Range rangei = document.createRange();
            if (i == 0) { 
                rangei.setStart(root.getFirstChild(), 10);
                rangei.setEnd(root.getFirstChild(), 10);
            }
            if (i == 1) { 
                rangei.setStart(root.getFirstChild(), 11);
                rangei.setEnd(root.getFirstChild(), 11);
            }
            if (i == 2) { 
                rangei.setStart(root.getFirstChild(), 12);
                rangei.setEnd(root.getFirstChild(), 12);
            }
            if (i == 3) { 
                rangei.setStart(root.getFirstChild(), 17);
                rangei.setEnd(root.getFirstChild(), 17);
            }
            
            rangei.insertNode(document.createTextNode(INSERTED_TEXT));
            
            String result = toString(document);
            assertEquals("Insert2 test[" + i + "]", rangeInsertResult[i], result);
        }
    }
    
    public void testDelete2() throws Exception {
        for (int i = 0; i < rangeDelete.length; i++) {
            parser.parse(new InputSource(new StringReader(rangeDelete[i])));
            DocumentImpl document = (DocumentImpl)parser.getDocument();
            Range range = document.createRange();
            Range ranged = document.createRange();
            Node root = document.getDocumentElement();
            
            if (i == 0) { 
                ranged.setStart(root.getFirstChild(), 5);
                ranged.setEnd(root.getFirstChild(), 14);
                
                range.setStart(root.getFirstChild(), 11);
                range.setEnd(root.getFirstChild(), 19);
            }
            else if (i == 1) {
                ranged.setStart(root.getFirstChild(), 5);
                ranged.setEnd(root.getFirstChild(), 22);
                
                range.setStart(root.getFirstChild(), 11);
                range.setEnd(root.getFirstChild(), 21);
            }
            else if (i == 2) {
                ranged.setStart(root.getFirstChild(), 5);
                ranged.setEnd(root.getFirstChild().getNextSibling()
                    .getFirstChild(), 1);
                    
                range.setStart(root.getFirstChild(), 11);
                
                range.setEndAfter(root.getFirstChild().getNextSibling()
                    .getFirstChild());
            }
            else if (i == 3) {
                ranged.setStart(root.getFirstChild(), 5);
                ranged.setEnd(root.getFirstChild(), 11);
                
                range.setStart(root.getFirstChild(), 11);
                range.setEnd(root.getFirstChild(), 21);
            }
            else if (i == 4) {
                ranged.selectNode(root.getFirstChild().getNextSibling());
                
                range.setStart(root.getFirstChild().getNextSibling()
                    .getFirstChild(), 6);
                range.setEnd(root.getFirstChild().getNextSibling()
                    .getFirstChild(), 15);
            }
            
            ranged.deleteContents();
            ranged.insertNode(document.createTextNode("^"));
            
            String result = toString(document);
            assertEquals("Delete2 test[" + i + "]", rangeDeleteResult[i], result);
        }
    }
    
    StringBuffer sb;
    boolean canonical = true;
    
    String toString(Node node) {
        sb = new StringBuffer();
        return print(node);
    }
   
   /** Prints the specified node, recursively. */
   public String print(Node node) {

      // is there anything to do?
      if ( node == null ) {
         return sb.toString();
      }

      int type = node.getNodeType();
      switch ( type ) {
         // print document
         case Node.DOCUMENT_NODE: {
               return print(((Document)node).getDocumentElement());
            }

            // print element with attributes
         case Node.ELEMENT_NODE: {
               sb.append('<');
               sb.append(node.getNodeName());
               Attr attrs[] = sortAttributes(node.getAttributes());
               for ( int i = 0; i < attrs.length; i++ ) {
                  Attr attr = attrs[i];
                  sb.append(' ');
                  sb.append(attr.getNodeName());
                  sb.append("=\"");
                  sb.append(normalize(attr.getNodeValue()));
                  sb.append('"');
               }
               sb.append('>');
               NodeList children = node.getChildNodes();
               if ( children != null ) {
                  int len = children.getLength();
                  for ( int i = 0; i < len; i++ ) {
                     print(children.item(i));
                  }
               }
               break;
            }

            // handle entity reference nodes
         case Node.ENTITY_REFERENCE_NODE: {
               if ( canonical ) {
                  NodeList children = node.getChildNodes();
                  if ( children != null ) {
                     int len = children.getLength();
                     for ( int i = 0; i < len; i++ ) {
                        print(children.item(i));
                     }
                  }
               } else {
                  sb.append('&');
                  sb.append(node.getNodeName());
                  sb.append(';');
               }
               break;
            }

            // print cdata sections
         case Node.CDATA_SECTION_NODE: {
               if ( canonical ) {
                  sb.append(normalize(node.getNodeValue()));
               } else {
                  sb.append("<![CDATA[");
                  sb.append(node.getNodeValue());
                  sb.append("]]>");
               }
               break;
            }

            // print text
         case Node.TEXT_NODE: {
               sb.append(normalize(node.getNodeValue()));
               break;
            }

            // print processing instruction
         case Node.PROCESSING_INSTRUCTION_NODE: {
               sb.append("<?");
               sb.append(node.getNodeName());
               String data = node.getNodeValue();
               if ( data != null && data.length() > 0 ) {
                  sb.append(' ');
                  sb.append(data);
               }
               sb.append("?>");
               break;
            }
            // handle entity reference nodes
         case Node.DOCUMENT_FRAGMENT_NODE: {
            NodeList children = node.getChildNodes();
            if ( children != null ) {
                int len = children.getLength();
                for ( int i = 0; i < len; i++ ) {
                     print(children.item(i));
                }
            }
               break;
            }
      }

      if ( type == Node.ELEMENT_NODE ) {
         sb.append("</");
         sb.append(node.getNodeName());
         sb.append('>');
      }

      return sb.toString();

   } // print(Node)

   /** Returns a sorted list of attributes. */
   protected Attr[] sortAttributes(NamedNodeMap attrs) {

      int len = (attrs != null) ? attrs.getLength() : 0;
      Attr array[] = new Attr[len];
      for ( int i = 0; i < len; i++ ) {
         array[i] = (Attr)attrs.item(i);
      }
      for ( int i = 0; i < len - 1; i++ ) {
         String name  = array[i].getNodeName();
         int    index = i;
         for ( int j = i + 1; j < len; j++ ) {
            String curName = array[j].getNodeName();
            if ( curName.compareTo(name) < 0 ) {
               name  = curName;
               index = j;
            }
         }
         if ( index != i ) {
            Attr temp    = array[i];
            array[i]     = array[index];
            array[index] = temp;
         }
      }

      return (array);

   } // sortAttributes(NamedNodeMap):Attr[]
    
   /** Normalizes the given string. */
   protected String normalize(String s) {
      StringBuffer str = new StringBuffer();

      int len = (s != null) ? s.length() : 0;
      for ( int i = 0; i < len; i++ ) {
         char ch = s.charAt(i);
         switch ( ch ) {
            case '<': {
                  str.append("&lt;");
                  break;
               }
            case '>': {
                  str.append("&gt;");
                  break;
               }
            case '&': {
                  str.append("&amp;");
                  break;
               }
            case '"': {
                  str.append("&quot;");
                  break;
               }
            case '\r':
            case '\n': {
                  if ( canonical ) {
                     str.append("&#");
                     str.append(Integer.toString(ch));
                     str.append(';');
                     break;
                  }
               }
            default: {
                  str.append(ch);
               }
         }
      }

      return (str.toString());

   } // normalize(String):String
    
     
}
