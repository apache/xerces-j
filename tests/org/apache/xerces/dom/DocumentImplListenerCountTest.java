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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class DocumentImplListenerCountTest extends TestCase {

    public static class TestListener implements EventListener, Serializable {

        private static final long serialVersionUID = 1L;

        static int eventCount;

        public void handleEvent(Event event) {
            ++eventCount;
        }
    }

    public void testListenerCountsSurviveDeserialization() throws Exception {
        TestListener.eventCount = 0;

        DocumentImpl document = new DocumentImpl();
        Element root = document.createElement("root");
        document.appendChild(root);
        ((EventTarget) root).addEventListener("DOMAttrModified", new TestListener(), false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(document);
        out.close();

        clearLegacyGlobalListenerCounts();

        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        DocumentImpl deserialized = (DocumentImpl) in.readObject();
        in.close();

        Element deserializedRoot = deserialized.getDocumentElement();
        deserializedRoot.setAttribute("name", "value");

        assertEquals(1, TestListener.eventCount);
    }

    private void clearLegacyGlobalListenerCounts() throws Exception {
        Class<?> lcountClass = Class.forName("org.apache.xerces.dom.LCount");
        try {
            Field field = lcountClass.getDeclaredField("lCounts");
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                ((Hashtable<?, ?>) field.get(null)).clear();
            }
        }
        catch (NoSuchFieldException expected) {
            // Listener counts are local to each document.
        }
    }
}
