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

package dom.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class Test extends TestCase
{
    EventReporter sharedReporter=new EventReporter();
    
    public void testRemovingAndAddingListenersDuringDispatch() {
        DocumentImpl document = new DocumentImpl();
        Element parent = document.createElement("parent");
        Element child = document.createElement("child");
        document.appendChild(parent);
        parent.appendChild(child);

        List<String> calls = new ArrayList<>();
        checkListenerMutation(document, (EventTarget) child, (EventTarget) parent, true, "capture", calls);
        checkListenerMutation(document, (EventTarget) child, (EventTarget) child, false, "target", calls);
        checkListenerMutation(document, (EventTarget) child, (EventTarget) parent, false, "bubble", calls);
    }

    private void checkListenerMutation(DocumentImpl document, EventTarget target, final EventTarget current,
                                       final boolean capture, final String type, final List<String> calls) {
        final EventListener replaced = recordingListener(calls, "replaced");
        EventListener retained = recordingListener(calls, "retained");
        EventListener mutator = new EventListener() {
            public void handleEvent(Event event) {
                calls.add("mutator");
                current.removeEventListener(type, replaced, capture);
                current.addEventListener(type, replaced, capture);
            }
        };
        current.addEventListener(type, mutator, capture);
        current.addEventListener(type, replaced, capture);
        current.addEventListener(type, retained, capture);

        dispatch(document, target, type);
        assertEquals(Arrays.asList("mutator", "retained"), calls);
        calls.clear();

        current.removeEventListener(type, mutator, capture);
        dispatch(document, target, type);
        assertEquals(Arrays.asList("retained", "replaced"), calls);
        calls.clear();
        current.removeEventListener(type, retained, capture);
        current.removeEventListener(type, replaced, capture);
    }

    public void testRemovingListenerFromRenamedNodeKeepsOriginalRegistration() {
        DocumentImpl document = new DocumentImpl();
        Element original = document.createElement("original");
        final List<EventTarget> calls = new ArrayList<>();
        EventListener listener = new EventListener() {
            public void handleEvent(Event event) {
                calls.add(event.getCurrentTarget());
            }
        };
        String type = "rename-listener-copy";

        EventTarget oldTarget = (EventTarget) original;
        oldTarget.addEventListener(type, listener, false);
        EventTarget newTarget = (EventTarget) document.renameNode(original, "urn:test", "renamed");
        assertNotSame(oldTarget, newTarget);

        dispatch(document, newTarget, type);
        newTarget.removeEventListener(type, listener, false);
        dispatch(document, oldTarget, type);
        dispatch(document, newTarget, type);
        assertEquals(Arrays.asList(newTarget, oldTarget), calls);

        oldTarget.removeEventListener(type, listener, false);
    }

    private EventListener recordingListener(final List<String> calls, final String name) {
        return new EventListener() {
            public void handleEvent(Event event) {
                calls.add(name);
            }
        };
    }

    private void dispatch(DocumentImpl document, EventTarget target, String type) {
        Event event = document.createEvent("Event");
        event.initEvent(type, true, false);
        target.dispatchEvent(event);
    }

    public void testMutationEvents()
    {
        Document doc=new org.apache.xerces.dom.DocumentImpl();
        reportAllMutations(doc);
        
        Element root=addNoisyElement(doc,doc,0);
        Element e=null;
        int i;

        // Individual nodes
        e=addNoisyElement(doc,root,0);
        Attr a=addNoisyAttr(doc,e,0);
        a.setNodeValue("Updated A0 of E0, prepare to be acidulated.");
        NamedNodeMap nnm=e.getAttributes();
        nnm.removeNamedItem(a.getName());
        nnm.setNamedItem(a);

        // InsertedInto/RemovedFrom tests.
        // ***** These do not currently cross the Attr/Element barrier.
        // DOM spec is pretty clear on that, but this may not be the intent.
        sharedReporter.off();
        Element lateAdd=doc.createElement("lateAdd");
        reportAllMutations(lateAdd);
        e=lateAdd;
        for(i=0;i<2;++i)
        {
            e=addNoisyElement(doc,e,i);
            addNoisyAttr(doc,e,i);
        }
        sharedReporter.on();
        root.appendChild(lateAdd);
        root.removeChild(lateAdd);

        sharedReporter.off();
        Node e0=root.replaceChild(lateAdd,root.getFirstChild());
        sharedReporter.on();
        root.replaceChild(e0,lateAdd);

        sharedReporter.off();
        Text t = addNoisyText(doc, root.getFirstChild(), "fo");
        sharedReporter.on();
        t.insertData(1, "o");

        root.setAttribute("foo", "bar");
    }
    
    Element addNoisyElement(Document doc,Node parent,int index)
    {
        String nodeName="Root";
        if(parent!=doc)
            nodeName=parent.getNodeName()+"_E"+index;
        Element e=doc.createElement(nodeName);
        reportAllMutations(e);
        parent.appendChild(e);
        return e;
    }

    Attr addNoisyAttr(Document doc,Element parent,int index)
    {
        String attrName=parent.getNodeName()+"_A"+index;
        Attr a=doc.createAttribute(attrName);
        reportAllMutations(a);
        a.setNodeValue("Initialized A"+index+" of "+parent.getNodeName());
        parent.setAttributeNode(a);
        return a;
    }

    Text addNoisyText(Document doc, Node parent, String data)
    {
        Text t = doc.createTextNode(data);
        reportAllMutations(t);
        parent.appendChild(t);
        return t;
    }

    void reportAllMutations(Node n)
    {
        String[] evtNames={
            "DOMSubtreeModified","DOMAttrModified","DOMCharacterDataModified",
            "DOMNodeInserted","DOMNodeRemoved",
            "DOMNodeInsertedIntoDocument","DOMNodeRemovedFromDocument",
            };
            
        EventTarget t=(EventTarget)n;
        
        for(int i=evtNames.length-1;
            i>=0;
            --i)
        {
            t.addEventListener(evtNames[i], sharedReporter, true);
            t.addEventListener(evtNames[i], sharedReporter, false);
        }

    }
}
