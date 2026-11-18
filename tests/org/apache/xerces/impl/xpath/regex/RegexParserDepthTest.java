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

package org.apache.xerces.impl.xpath.regex;

import junit.framework.TestCase;

/**
 * Tests that deeply nested regular expressions do not exhaust the stack.
 */
public class RegexParserDepthTest extends TestCase {

    private static final int DEPTH = 5000;

    public void testDeeplyNestedParenthesesDoNotOverflowStack()
            throws Exception {
        String pattern = createPattern(DEPTH);
        new RegularExpression(pattern);
        new RegularExpression(pattern, "X");
    }

    private String createPattern(int depth) {
        StringBuilder pattern = new StringBuilder(depth * 2 + 1);
        for (int i = 0; i < depth; i++) {
            pattern.append('(');
        }
        pattern.append('a');
        for (int i = 0; i < depth; i++) {
            pattern.append(')');
        }
        return pattern.toString();
    }
}
