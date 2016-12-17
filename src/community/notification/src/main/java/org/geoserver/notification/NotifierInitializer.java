/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.List;
import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInitializer;
import org.geoserver.platform.GeoServerExtensions;
import org.geotools.util.logging.Logging;

public class NotifierInitializer implements GeoServerInitializer {

    static Logger LOGGER = Logging.getLogger(NotifierInitializer.class);

    private NotifierConfig notifierConfig;

    public NotifierInitializer(NotifierConfig notifierConfig) {
        this.notifierConfig = notifierConfig;
    }

    public void initialize(GeoServer geoServer) throws Exception {
        List<INotificationCatalogListener> catalogListeners = GeoServerExtensions
                .extensions(INotificationCatalogListener.class);
        for (INotificationCatalogListener cl : catalogListeners) {
            cl.setNotificationConfiguration(this.notifierConfig.getConfiguration());
            geoServer.getCatalog().addListener(cl);
        }
        List<INotificationTransactionListener> transactionListeners = GeoServerExtensions
                .extensions(INotificationTransactionListener.class);
        for (INotificationTransactionListener tl : transactionListeners) {
            tl.setNotificationConfiguration(this.notifierConfig.getConfiguration());
        }
    }

}
