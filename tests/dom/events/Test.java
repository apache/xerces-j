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
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

public class Test extends TestCase
{
    public void testRemovingDetachedSubtreeDoesNotNotifyDocumentRemoval() {
        assertDocumentRemovalTargets(false);
    }

    public void testRemovingAttachedSubtreeNotifiesEachNode() {
        assertDocumentRemovalTargets(true);
    }

    private void assertDocumentRemovalTargets(boolean attached) {
        Document document = new DocumentImpl();
        Element parent = document.createElement("parent");
        Element child = document.createElement("child");
        Element descendant = document.createElement("descendant");
        if (attached) {
            document.appendChild(parent);
        }
        parent.appendChild(child);
        child.appendChild(descendant);

        final List<EventTarget> documentRemovalTargets = new ArrayList<>();
        EventListener documentRemovalListener = new EventListener() {
            public void handleEvent(Event event) {
                documentRemovalTargets.add(event.getTarget());
            }
        };
        ((EventTarget) parent).addEventListener("DOMNodeRemovedFromDocument", documentRemovalListener, false);
        ((EventTarget) child).addEventListener("DOMNodeRemovedFromDocument", documentRemovalListener, false);
        ((EventTarget) descendant).addEventListener("DOMNodeRemovedFromDocument", documentRemovalListener, false);
        final List<EventTarget> removalTargets = new ArrayList<>();
        ((EventTarget) child).addEventListener("DOMNodeRemoved",
                new EventListener() {
                    public void handleEvent(Event event) {
                        removalTargets.add(event.getTarget());
                    }
                }, false);

        parent.removeChild(child);

        assertEquals(Arrays.asList(child), removalTargets);
        if (attached) {
            assertEquals(2, documentRemovalTargets.size());
            assertTrue(documentRemovalTargets.contains(child));
            assertTrue(documentRemovalTargets.contains(descendant));
        }
        else {
            assertTrue(documentRemovalTargets.isEmpty());
        }
    }

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

    public void testNodeInsertionFiresAfterAttachment() {
        assertNodeMutation("DOMNodeInserted", true);
    }

    public void testNodeRemovalFiresBeforeDetachment() {
        assertNodeMutation("DOMNodeRemoved", false);
    }

    private void assertNodeMutation(String type, boolean insertion) {
        Document document = new DocumentImpl();
        Element parent = document.createElement("parent");
        Element child = document.createElement("child");
        document.appendChild(parent);
        if (!insertion) {
            parent.appendChild(child);
        }

        MutationLog log = new MutationLog();
        log.listen(document, type, true);
        log.listen(parent, type, true);
        log.listen(child, type, false);
        log.listen(parent, type, false);
        log.listen(document, type, false);

        if (insertion) {
            parent.appendChild(child);
            assertSame(parent, child.getParentNode());
        }
        else {
            parent.removeChild(child);
            assertNull(child.getParentNode());
        }

        assertEquals(5, log.events.size());
        Node[] route = {document, parent, child, parent, document};
        short[] phases = {Event.CAPTURING_PHASE, Event.CAPTURING_PHASE, Event.AT_TARGET,
                Event.BUBBLING_PHASE, Event.BUBBLING_PHASE};
        for (int i = 0; i < route.length; i++) {
            MutationSnapshot event = log.events.get(i);
            assertSame(child, event.target);
            assertSame(route[i], event.currentTarget);
            assertEquals(phases[i], event.phase);
            assertSame(parent, event.relatedNode);
            assertSame(parent, event.parentAtDispatch);
            assertTrue(event.bubbles);
        }
    }

    public void testSubtreeInsertionNotifiesEachNodeWithoutBubbling() {
        Document document = new DocumentImpl();
        Element parent = document.createElement("parent");
        Element child = document.createElement("child");
        Element descendant = document.createElement("descendant");
        document.appendChild(parent);

        MutationLog log = new MutationLog();
        String type = "DOMNodeInsertedIntoDocument";
        log.listen(parent, type, false);
        log.listen(child, type, false);
        log.listen(descendant, type, false);

        child.appendChild(descendant);
        assertTrue(log.events.isEmpty());
        parent.appendChild(child);

        assertEquals(2, log.events.size());
        for (Node target : Arrays.asList(child, descendant)) {
            int notifications = 0;
            for (MutationSnapshot event : log.events) {
                if (event.target == target) {
                    notifications++;
                    assertSame(target, event.currentTarget);
                    assertEquals(Event.AT_TARGET, event.phase);
                    assertFalse(event.bubbles);
                }
            }
            assertEquals(1, notifications);
        }
    }

    public void testReplacingChildReportsBothMutations() {
        Document document = new DocumentImpl();
        Element parent = document.createElement("parent");
        Element original = document.createElement("original");
        Element replacement = document.createElement("replacement");
        document.appendChild(parent);
        parent.appendChild(original);

        MutationLog removals = new MutationLog();
        removals.listen(original, "DOMNodeRemoved", false);
        MutationLog insertions = new MutationLog();
        insertions.listen(replacement, "DOMNodeInserted", false);
        parent.replaceChild(replacement, original);

        assertEquals(1, removals.events.size());
        assertSame(original, removals.events.get(0).target);
        assertSame(parent, removals.events.get(0).parentAtDispatch);
        assertEquals(1, insertions.events.size());
        assertSame(replacement, insertions.events.get(0).target);
        assertSame(parent, insertions.events.get(0).parentAtDispatch);
        assertSame(replacement, parent.getFirstChild());
        assertNull(original.getParentNode());
    }

    public void testAttributeMutationReportsChangeKindAndValues() {
        Document document = new DocumentImpl();
        Element root = document.createElement("root");
        document.appendChild(root);

        MutationLog log = new MutationLog();
        log.listen(root, "DOMAttrModified", false);
        root.setAttribute("name", "before");
        Attr attribute = root.getAttributeNode("name");
        attribute.setValue("after");
        root.removeAttributeNode(attribute);

        assertEquals(3, log.events.size());
        assertAttributeMutation(log.events.get(0), root, attribute, MutationEvent.ADDITION, null, "before");
        assertAttributeMutation(log.events.get(1), root, attribute, MutationEvent.MODIFICATION, "before", "after");
        assertAttributeMutation(log.events.get(2), root, attribute, MutationEvent.REMOVAL, "after", null);
    }

    private void assertAttributeMutation(MutationSnapshot event, Element target, Attr attribute,
                                         short change, String previousValue, String newValue) {
        assertSame(target, event.target);
        assertSame(attribute, event.relatedNode);
        assertEquals(attribute.getName(), event.attributeName);
        assertEquals(change, event.attributeChange);
        assertEquals(previousValue, event.previousValue);
        assertEquals(newValue, event.newValue);
    }

    public void testTextMutationReportsOldAndNewValues() {
        Document document = new DocumentImpl();
        Element root = document.createElement("root");
        Text text = document.createTextNode("fo");
        document.appendChild(root);
        root.appendChild(text);

        MutationLog log = new MutationLog();
        log.listen(text, "DOMCharacterDataModified", false);
        text.insertData(1, "o");

        assertEquals(1, log.events.size());
        MutationSnapshot event = log.events.get(0);
        assertSame(text, event.target);
        assertEquals("fo", event.previousValue);
        assertEquals("foo", event.newValue);
    }

    public void testSubtreeModifiedFollowsSpecificMutationEvent() {
        Document document = new DocumentImpl();
        Element root = document.createElement("root");
        document.appendChild(root);

        MutationLog log = new MutationLog();
        log.listen(root, "DOMAttrModified", false);
        log.listen(root, "DOMSubtreeModified", false);
        root.setAttribute("name", "value");

        assertTrue("Expected a subtree notification", log.events.size() > 1);
        assertEquals("DOMAttrModified", log.events.get(0).type);
        for (MutationSnapshot event : log.events.subList(1, log.events.size())) {
            assertEquals("DOMSubtreeModified", event.type);
            assertSame(root, event.target);
        }
    }

    private static class MutationLog implements EventListener {
        final List<MutationSnapshot> events = new ArrayList<>();

        void listen(Node node, String type, boolean capture) {
            ((EventTarget) node).addEventListener(type, this, capture);
        }

        @Override
        public void handleEvent(Event event) {
            events.add(new MutationSnapshot((MutationEvent) event));
        }
    }

    private static class MutationSnapshot {
        final String type;
        final EventTarget target;
        final EventTarget currentTarget;
        final short phase;
        final boolean bubbles;
        final Node relatedNode;
        final Node parentAtDispatch;
        final String attributeName;
        final short attributeChange;
        final String previousValue;
        final String newValue;

        MutationSnapshot(MutationEvent event) {
            type = event.getType();
            target = event.getTarget();
            currentTarget = event.getCurrentTarget();
            phase = event.getEventPhase();
            bubbles = event.getBubbles();
            relatedNode = event.getRelatedNode();
            parentAtDispatch = ((Node) target).getParentNode();
            attributeName = event.getAttrName();
            attributeChange = event.getAttrChange();
            previousValue = event.getPrevValue();
            newValue = event.getNewValue();
        }
    }
}
