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
import org.geoserver.notification.support.SimpleCache;
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

    private SimpleCache cache = new SimpleCache();

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

        new Receiver(event).receive();

        String json = "{'namespace':{ 'prefix':'foo', 'uri':'http://foo.com' }}";
        MockHttpServletResponse response = postAsServletResponse("/rest/namespaces", json,
                "text/json");
        assertEquals(201, response.getStatus());

        verify(event, times(1)).manage(argThat(new ArgumentMatcher<String>() {

            @Override
            public boolean matches(Object argument) {
                // Assert
                return true;
            }

        }));

    }

}
