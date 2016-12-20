/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.notification.support.BrokerManager;
import org.geoserver.notification.support.Receiver;
import org.geoserver.notification.support.ReceiverService;
import org.geoserver.security.AccessMode;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.mock.web.MockHttpServletResponse;

public class IntegrationTest extends GeoServerSystemTestSupport {

    protected static Catalog catalog;

    protected static XpathEngine xp;

    private static BrokerManager brokerStarter;

    @BeforeClass
    public static void startup() throws Exception {
        brokerStarter = new BrokerManager();
        brokerStarter.startBroker();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        brokerStarter.stopBroker();
    }

    @Override
    protected void setUpTestData(SystemTestData testData) throws Exception {
        // TODO Auto-generated method stub
        super.setUpTestData(testData);
        new File(testData.getDataDirectoryRoot(), "notifier").mkdir();
        testData.copyTo(
                getClass().getClassLoader().getResourceAsStream(
                        NotifierInitializer.PROPERTYFILENAME), "notifier/"
                        + NotifierInitializer.PROPERTYFILENAME);
    }

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);

        addLayerAccessRule("*", "*", AccessMode.READ, "*");
        addLayerAccessRule("*", "*", AccessMode.WRITE, "*");

        catalog = getCatalog();

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("html", "http://www.w3.org/1999/xhtml");
        namespaces.put("sld", "http://www.opengis.net/sld");
        namespaces.put("ogc", "http://www.opengis.net/ogc");
        namespaces.put("atom", "http://www.w3.org/2005/Atom");

        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        xp = XMLUnit.newXpathEngine();
    }

    @Before
    public void login() throws Exception {
        login("admin", "geoserver", "ROLE_ADMINISTRATOR");
    }

    @Test
    public void catalogAddNamespaces() throws Exception {
        ReceiverService event = mock(ReceiverService.class);
        Receiver rc = new Receiver(event);
        rc.receive();

        String json = "{'namespace':{ 'prefix':'foo', 'uri':'http://foo.com' }}";
        MockHttpServletResponse response = postAsServletResponse("/rest/namespaces", json,
                "text/json");
        assertEquals(201, response.getStatus());

        verify(event, times(2)).manage(argThat(new ArgumentMatcher<String>() {

            @Override
            public boolean matches(Object argument) {
                // Assert
                return true;
            }

        }));
        rc.close();
    }

    @Test
    public void transactionDoubleAdd() throws Exception {
        ReceiverService event = mock(ReceiverService.class);
        Receiver rc = new Receiver(event);
        rc.receive();

        String xml = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" "
                + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" "
                + "xmlns:ogc=\"http://www.opengis.net/ogc\" "
                + "xmlns:wfs=\"http://www.opengis.net/wfs\" "
                + "xmlns:gml=\"http://www.opengis.net/gml\"> " + "<wfs:Insert handle='insert-1'> "
                + "<cgf:Lines>" + "<cgf:lineStringProperty>" + "<gml:LineString>"
                + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">" + "5,5 6,6"
                + "</gml:coordinates>" + "</gml:LineString>" + "</cgf:lineStringProperty>"
                + "<cgf:id>t0001</cgf:id>" + "</cgf:Lines>" + "</wfs:Insert>"
                + "<wfs:Insert handle='insert-2'> " + "<cgf:Lines>" + "<cgf:lineStringProperty>"
                + "<gml:LineString>" + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">"
                + "7,7 8,8" + "</gml:coordinates>" + "</gml:LineString>"
                + "</cgf:lineStringProperty>" + "<cgf:id>t0002</cgf:id>" + "</cgf:Lines>"
                + "</wfs:Insert>" + "</wfs:Transaction>";

        postAsDOM("wfs", xml);

        verify(event, times(1)).manage(argThat(new ArgumentMatcher<String>() {

            @Override
            public boolean matches(Object argument) {
                // Assert
                return true;
            }

        }));
        rc.close();
    }

    @Test
    public void transactionAddAndUpdate() throws Exception {

        String xml = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" "
                + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" "
                + "xmlns:ogc=\"http://www.opengis.net/ogc\" "
                + "xmlns:wfs=\"http://www.opengis.net/wfs\" "
                + "xmlns:gml=\"http://www.opengis.net/gml\"> " + "<wfs:Insert handle='insert-1'>"
                + "<sf:WithGMLProperties>" + "<gml:location>" + "<gml:Point>"
                + "<gml:coordinates>2,2</gml:coordinates>" + "</gml:Point>" + "</gml:location>"
                + "<gml:name>one</gml:name>" + "<sf:foo>1</sf:foo>" + "</sf:WithGMLProperties>"
                + "</wfs:Insert>" + " <wfs:Update typeName=\"sf:WithGMLProperties\">"
                + "   <wfs:Property>" + "     <wfs:ValueReference>gml:name</wfs:ValueReference>"
                + "     <wfs:Value>two</wfs:Value>" + "   </wfs:Property>" + "   <wfs:Property>"
                + "     <wfs:ValueReference>gml:location</wfs:ValueReference>" + "     <wfs:Value>"
                + "        <gml:Point>" + "          <gml:coordinates>7,7</gml:coordinates>"
                + "        </gml:Point>" + "     </wfs:Value>" + "   </wfs:Property>"
                + "   <wfs:Property>" + "     <wfs:ValueReference>sf:foo</wfs:ValueReference>"
                + "     <wfs:Value>2</wfs:Value>" + "   </wfs:Property>" + "   <fes:Filter>"
                + "     <fes:PropertyIsEqualTo>"
                + "       <fes:ValueReference>foo</fes:ValueReference>"
                + "       <fes:Literal>1</fes:Literal>" + "     </fes:PropertyIsEqualTo>"
                + "   </fes:Filter>" + " </wfs:Update>" + "</wfs:Transaction>";

        postAsDOM("wfs", xml);

    }
}
