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

package org.apache.xerces.tests;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

public class SubstitutionGroupTests extends XercesAbstractTestCase {

	public SubstitutionGroupTests(String name) {
		super(name);
	}
	
	public void testSubstitutionGroup1() {
		String xmlfile = fDataDir+"/roger/substitutionGroup/transit.xml";
		String schemapath = fDataDir+"/roger/substitutionGroup/transit.xsd";	
		try {
		    Schema s = fSchemaFactory.newSchema(new StreamSource(schemapath));
            Validator v = s.newValidator();
		    v.setErrorHandler(this);
            v.validate(new StreamSource(xmlfile));
            assertNull(fErrSysId);
            assertNull(fFatErrSysId);
		} catch(Exception ex) {
		   ex.printStackTrace();
		   assertTrue(false);
		}
	}

}
