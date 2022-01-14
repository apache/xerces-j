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
import java.io.IOException;

/*
   Class providing support for determining "XSD Schema document" and
   "XML instance document" validity for Xerces-J, for W3C XML Schema 1.1
   test-suite.

   @author: Mukul Gandhi IBM

   @creation date: 2011-12-08
*/
public class XMLValidatorHelper2 {

    private static final String XSD10schemaLanguage = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    private static final String schema11Factory = "org.apache.xerces.jaxp.validation.XMLSchema11Factory";
    private static final String XSD11schemaLanguage = "http://www.w3.org/2001/XMLSchema/v1.1";
	private static final String CTA_FULL_XPATH = Constants.XERCES_FEATURE_PREFIX + Constants.CTA_FULL_XPATH_CHECKING_FEATURE;

	/*
	   Method to check for validity of an XML schema document.
	*/
    public static boolean isSchemaValid(String schemaUri) throws IOException, SAXNotRecognizedException, SAXNotSupportedException {

		boolean isSchemaDocValid = true;
		SchemaFactory schemaFactory = null;

		System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema/v1.1", schema11Factory);
        schemaFactory = SchemaFactory.newInstance(XSD11schemaLanguage);
		//schemaFactory.setFeature(CTA_FULL_XPATH, true);

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
    public static String getInstanceDocValidity(String xmlDocUri, String schemaUri) throws IOException, SAXNotRecognizedException, SAXNotSupportedException {

	   SchemaFactory schemaFactory = null;
	   Schema schema = null;
	   String validityStatus = "valid";

       System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema/v1.1", schema11Factory);
       schemaFactory = SchemaFactory.newInstance(XSD11schemaLanguage);
	   //schemaFactory.setFeature(CTA_FULL_XPATH, true);

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