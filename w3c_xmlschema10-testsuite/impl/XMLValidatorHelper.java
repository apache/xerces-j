import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.apache.xerces.impl.Constants;

/*
   Class providing support for determining "XSD Schema document" and
   "XML instance document" validity for Xerces-J, for W3C XML Schema 1.0
   test-suite.

   @author: Mukul Gandhi, mukulg@apache.org

   @creation date: 2022-01-21
*/
public class XMLValidatorHelper {

	/*
	   Method to check for validity of an XML schema document.
	*/
    public static boolean isSchemaValid(String schemaUri, String testSetName) throws SAXNotRecognizedException, SAXNotSupportedException {

		boolean isSchemaDocValid = true;
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		if ("XmlVersions".equals(testSetName)) {
		   System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XML11Configuration");
		}

		try {
		   Schema schema = schemaFactory.newSchema(new StreamSource(schemaUri));
		}
		catch(SAXException saxException) {
		   isSchemaDocValid = false;
		   saxException.printStackTrace();
		}

		return isSchemaDocValid;

    }


    /*
	   Method to check for validity of an XML document with an XML schema.
	*/
    public static String getInstanceDocValidity(String xmlDocUri, String schemaUri, String vendorId, String testSetName) throws SAXNotRecognizedException, SAXNotSupportedException {

	   SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	   Schema schema = null;
	   String validityStatus = "valid";

	   if ("XmlVersions".equals(testSetName)) {
		  System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XML11Configuration");
	   }

       try {
	        if ("null".equals(schemaUri)) {
			   schemaFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
			   schema = schemaFactory.newSchema();
			}
			else {
               schema = schemaFactory.newSchema(new StreamSource(schemaUri));
			}
            Validator validator = schema.newValidator();
            XSDTestSuiteErrorHandler errorHandler = new XSDTestSuiteErrorHandler();
            errorHandler.reset();
			validator.setErrorHandler(errorHandler);
            validator.validate(new StreamSource(xmlDocUri));
            validityStatus =  errorHandler.getValidityStatus();
        }
        catch (Exception ex) {
            validityStatus = "notKnown";
        }

		return validityStatus;

    }

	/*
	   Class implementing an Error Handler for a schema validation episode.
	*/
	static class XSDTestSuiteErrorHandler implements ErrorHandler {

		private String validityStatus = "notKnown";

		public void error(SAXParseException exception) throws SAXException {
	       validityStatus = "invalid";
        }

	    public void fatalError(SAXParseException exception) throws SAXException {
	       validityStatus = "fatalerror";
        }

		public void warning(SAXParseException exception) throws SAXException {
           // NO OP
	    }

		public String getValidityStatus() {
		   return validityStatus;
		}

		public void reset() {
		   validityStatus = "valid";
		}
	}

}