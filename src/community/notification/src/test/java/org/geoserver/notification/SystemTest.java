/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.io.File;
import java.io.IOException;

import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wfs.TransactionPlugin;
import org.junit.Test;

import static org.junit.Assert.*;

public class SystemTest extends GeoServerSystemTestSupport {

    @Override
    protected void setUpTestData(SystemTestData testData) throws Exception {
        // TODO Auto-generated method stub
        super.setUpTestData(testData);
        new File(testData.getDataDirectoryRoot(), "notifier").mkdir();
        testData.copyTo(getClass().getClassLoader().getResourceAsStream(NotifierConfig.PROPERTYFILENAME), "notifier/" + NotifierConfig.PROPERTYFILENAME);
    }

    @Test
    public void testCatalogNotifierIntialization() throws IOException {
        NotificationConfiguration cfg = null;
        for(CatalogListener listener : getGeoServer().getCatalog().getListeners()){
            if(listener instanceof INotificationCatalogListener){
                INotificationCatalogListener notifierListener = (INotificationCatalogListener) listener;
                cfg = notifierListener.getNotificationConfiguration();
                break;
            }
        }
        assertNotNull(cfg);
        assertTrue(cfg.getNotificators().size() == 1);
    }

    @Test
    public void testTransactionNotifierIntialization() throws IOException {
        NotificationConfiguration cfg = null;
        for(TransactionPlugin listener : GeoServerExtensions.extensions(TransactionPlugin.class)){
            if(listener instanceof INotificationTransactionListener){
                INotificationTransactionListener notifierListener = (INotificationTransactionListener) listener;
                cfg = notifierListener.getNotificationConfiguration();
                break;
            }
        }
        assertNotNull(cfg);
        assertTrue(cfg.getNotificators().size() == 1);
    }


}
