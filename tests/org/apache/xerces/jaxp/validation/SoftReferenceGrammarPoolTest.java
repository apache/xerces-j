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

package org.apache.xerces.jaxp.validation;

import junit.framework.TestCase;

import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;

/**
 * Tests cleanup of queued grammar references.
 */
public class SoftReferenceGrammarPoolTest extends TestCase {

    private static final class TestGrammar implements Grammar {

        private final XMLGrammarDescription fDescription;

        TestGrammar(XMLGrammarDescription description) {
            fDescription = description;
        }

        public XMLGrammarDescription getGrammarDescription() {
            return fDescription;
        }
    }

    private static XSDDescription createDescription() {
        XSDDescription description = new XSDDescription();
        description.setTargetNamespace("urn:test");
        return description;
    }

    private static SoftReferenceGrammarPool.Entry firstEntry(SoftReferenceGrammarPool pool) {
        for (int i = 0; i < pool.fGrammars.length; i++) {
            if (pool.fGrammars[i] != null) {
                return pool.fGrammars[i];
            }
        }
        fail("No grammar pool entry found.");
        return null;
    }

    public void testClearWithQueuedReference() {
        SoftReferenceGrammarPool pool = new SoftReferenceGrammarPool();
        XSDDescription description = createDescription();
        Grammar grammar = new TestGrammar(description);
        pool.putGrammar(grammar);

        SoftReferenceGrammarPool.Entry entry = firstEntry(pool);
        assertTrue(entry.grammar.enqueue());

        pool.clear();
        assertNull(pool.getGrammar(description));
        assertEquals(0, pool.fGrammarCount);
    }

    public void testReplaceQueuedReference() {
        SoftReferenceGrammarPool pool = new SoftReferenceGrammarPool();
        XSDDescription description = createDescription();
        Grammar first = new TestGrammar(description);
        Grammar second = new TestGrammar(description);
        pool.putGrammar(first);

        SoftReferenceGrammarPool.Entry entry = firstEntry(pool);
        assertTrue(entry.grammar.enqueue());

        pool.putGrammar(second);
        assertSame(second, pool.getGrammar(description));
        assertEquals(1, pool.fGrammarCount);
    }
}
