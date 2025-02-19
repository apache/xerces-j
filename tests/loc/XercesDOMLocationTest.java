package loc;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.DOMLocatorImpl;
import junit.framework.*;

import static org.apache.xerces.parsers.XML11Configuration.LOCATION_INFO_FEATURE;
import org.w3c.dom.*;

import java.io.File;

public class XercesDOMLocationTest extends TestCase {

    private static final String TEST_XML_PATH = "test.xml";

    public XercesDOMLocationTest(String testName) {
        super(testName);
    }

    public static TestSuite suite() {
        return new TestSuite(XercesDOMLocationTest.class);
    }

    private Document parseXML(boolean enableLocationFeature) throws Exception {
        DOMParser parser = new DOMParser();
        parser.setFeature(LOCATION_INFO_FEATURE, enableLocationFeature);
        parser.parse(new File(TEST_XML_PATH).toURI().toString());
        return parser.getDocument();
    }

    public void testFeatureDisabledByDefault() throws Exception {
        Document doc = parseXML(false); // Feature disabled

        Element root = doc.getDocumentElement();
        Object locationData = root.getUserData("location");

        assertNull("Location data should not be present when feature is disabled.", locationData);
    }

    public void testFeatureEnabledLocationPresent() throws Exception {
        Document doc = parseXML(true); // Feature enabled

        Element root = doc.getDocumentElement();
        Object locationData = root.getUserData("location");

        assertNotNull("Location data should be present when feature is enabled.", locationData);
        assertTrue("Location data should be an instance of DOMLocatorImpl.", locationData instanceof DOMLocatorImpl);
    }

    public void testRootElementLocation() throws Exception {
        Document doc = parseXML(true);
        Element root = doc.getDocumentElement();

        DOMLocatorImpl location = (DOMLocatorImpl) root.getUserData("location");
        assertNotNull("Root element should have location data.", location);

        System.out.println("Root Location: " + location);
        assertEquals("File name should match.", "test.xml", new File(location.getUri()).getName());
        assertEquals("Root element should be on line 2.", 2, location.getLineNumber());
    }

    public void testChildElementLocation() throws Exception {
        Document doc = parseXML(true);
        Element child = (Element) doc.getDocumentElement().getElementsByTagName("child").item(0);

        DOMLocatorImpl location = (DOMLocatorImpl) child.getUserData("location");
        assertNotNull("Child element should have location data.", location);

        System.out.println("Child Location: " + location);
        assertTrue("Child element should be after root (line 3).", location.getLineNumber() > 2);
    }

    public void testSelfClosingElementLocation() throws Exception {
        Document doc = parseXML(true);
        Element selfClosing = (Element) doc.getDocumentElement().getElementsByTagName("selfClosing").item(0);

        DOMLocatorImpl location = (DOMLocatorImpl) selfClosing.getUserData("location");
        assertNotNull("Self-closing element should have location data.", location);

        System.out.println("SelfClosing Element Location: " + location);
        assertEquals("Self-closing element should be on line 4.", 4, location.getLineNumber());
    }

    public void testFeatureDisabledNoLocationStored() throws Exception {
        Document doc = parseXML(false);
        Element child = (Element) doc.getDocumentElement().getElementsByTagName("child").item(0);

        Object locationData = child.getUserData("location");
        assertNull("Location data should not be present when feature is disabled.", locationData);
    }
}
