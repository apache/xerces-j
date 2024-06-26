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

package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Mukul Gandhi <mukulg@apache.org>
 * @version $Id$
 */
public class XercesAbstractTestCase extends TestCase implements ErrorHandler {
	
	protected SchemaFactory fSchemaFactory = null;	
	protected String fErrSysId = null;
	protected String fFatErrSysId = null;
	protected String fWarningSysId = null;
	protected String fErrorMessage = null;
	// to maintain a collection of errors and/or warnings for ONE test case execution
	protected List failureList = null;
	protected List warningList = null; 
	
	protected boolean checkOnlyWarnings = false; 
	
	protected static final String DEFAULT_SCHEMA_LANGUAGE = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";	
	
	public XercesAbstractTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {		
		fSchemaFactory = SchemaFactory.newInstance(DEFAULT_SCHEMA_LANGUAGE);
		fSchemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
		failureList = new ArrayList();
		warningList = new ArrayList(); 
	}

	protected void tearDown() throws Exception {
		fErrSysId = null;
		fFatErrSysId = null;
		failureList = null;
		warningList = null;
		checkOnlyWarnings = false;		
	}
	
	public void error(SAXParseException exception) throws SAXException {
		fErrSysId = exception.getSystemId();
		fErrorMessage = exception.getMessage();
		failureList.add(new Error(fErrSysId, fErrorMessage));
    }

    public void fatalError(SAXParseException exception) throws SAXException {
    	fFatErrSysId = exception.getSystemId();
    	fErrorMessage = exception.getMessage();
    	failureList.add(new FatalError(fErrSysId, fErrorMessage));
    }

	public void warning(SAXParseException exception) throws SAXException {
		fWarningSysId = exception.getSystemId();
		fErrorMessage = exception.getMessage();
		warningList.add(new Warning(fWarningSysId, fErrorMessage));
	}	
	
	/*
	 * Are error messages generated during the validation episode, as specified in the test case.
	 */
	public boolean areErrorMessagesConsistent(List expectedMsgList) {		
		boolean isErrorMesgsOk = true;		
		for (int mesgIdx = 0; mesgIdx < expectedMsgList.size(); mesgIdx++) {
			FailureMesgFragments mesgFragments = (FailureMesgFragments) expectedMsgList.get(mesgIdx);
			if (!areMesgFragmentsOk(mesgFragments)) {
				isErrorMesgsOk = false;
				break;
			}
		}		
		return isErrorMesgsOk;		
	} // areErrorMessagesConsistent	
	
	/*
	 * Checks fragments of one error/failure message.
	 */
	private boolean areMesgFragmentsOk(FailureMesgFragments mesgFragments) {
		
		boolean areMesgFragsOk = false;
		
		List mesgFragmentItems = mesgFragments.getMessageFragments();
		Iterator iter = null;
		if (checkOnlyWarnings) {
			iter = warningList.iterator(); 
		}
		else {
			iter = failureList.iterator(); 
		}
        for ( ; iter.hasNext(); ) {
        	Object failureInstance = iter.next();
        	String failureMesg = "";
        	if (failureInstance instanceof Error) {
        		failureMesg = ((Error) failureInstance).getFailureMessage(); 
        	}
        	else if (failureInstance instanceof FatalError) {
        		failureMesg = ((FatalError) failureInstance).getFailureMessage();
        	}
        	else if (failureInstance instanceof Warning) {
        		failureMesg = ((Warning) failureInstance).getFailureMessage();
        	}
        	int matchCount = 0;
        	for (Iterator mesg_iter = mesgFragmentItems.iterator(); mesg_iter.hasNext(); ) {
        		String mesgFrag = (String) mesg_iter.next();
        		if (failureMesg.indexOf(mesgFrag) != -1) {
        			matchCount++;
        		}
        	}
        	if (matchCount == mesgFragmentItems.size()) {
        		areMesgFragsOk = true;
        		break;
        	}
        }


		return areMesgFragsOk;
		
	} // areMesgFragmentsOk
		
	class XercesFailure {
	   String systemId;
	   String failureMessage;
	   
	   public XercesFailure(String systemId, String failureMessage) {
		   this.systemId = systemId;
		   this.failureMessage = failureMessage;
	   }
	   
	   public String getFailureMessage() {
		  return failureMessage; 
	   }
	}
	
	class Error extends XercesFailure {		
		public Error(String systemId, String failureMessage) {
			super(systemId, failureMessage);
		}		
	}
	
	class FatalError extends XercesFailure {		
		public FatalError(String systemId, String failureMessage) {
			super(systemId, failureMessage);
		}		
	}
	
	class Warning extends XercesFailure {		
		public Warning(String systemId, String failureMessage) {
			super(systemId, failureMessage);
		}		
	}
	
} // XercesAbstractTestCase
