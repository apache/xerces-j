/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $Id: DTDStreamReader.java 1157662 2011-08-14 20:46:48Z mrglavas $

package javax.xml.stream;

public interface DTDStreamReader {
    
    public static final int PROCESSING_INSTRUCTION = XMLStreamConstants.PROCESSING_INSTRUCTION;
    public static final int COMMENT = XMLStreamConstants.COMMENT;
    public static final int NOTATION_DECLARATION = XMLStreamConstants.NOTATION_DECLARATION;
    public static final int ENTITY_DECLARATION = XMLStreamConstants.ENTITY_DECLARATION;
    public static final int START_DTD = 1001;
    public static final int END_DTD = 1002;
    public static final int UNPARSED_ENTITY_DECLARATION = 1003;
    
    public int next() throws XMLStreamException;
    public boolean hasNext() throws XMLStreamException;
    public int getEventType();
    public String getText();
    public char[] getTextCharacters();
    public int getTextStart();
    public int getTextLength();
    public Location getLocation();
    public String getPITarget();
    public String getPIData();
    public String getName();
    public String getPublicIdentifier();
    public String getSystemIdentifier();
    public String getNotationName();
    public void close() throws XMLStreamException;
    
}
