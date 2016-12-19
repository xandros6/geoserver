/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInitializer;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;

import com.thoughtworks.xstream.XStream;

public class NotifierInitializer implements GeoServerInitializer {

    static Logger LOGGER = Logging.getLogger(NotifierInitializer.class);

    public static final String PROPERTYFILENAME = "notifier.xml";

    private GeoServerResourceLoader loader;

    public NotifierInitializer(GeoServerResourceLoader loader) {
        this.loader = loader;
    }

    public void initialize(GeoServer geoServer) throws Exception {

        XStream xs = new XStream();

        List<NotificationXStreamInitializer> xstreamInitializers = GeoServerExtensions
                .extensions(NotificationXStreamInitializer.class);
        for (NotificationXStreamInitializer ni : xstreamInitializers) {
            ni.init(xs);
        }

        NotificationConfiguration cfg = getConfiguration(xs);

        List<INotificationCatalogListener> catalogListeners = GeoServerExtensions
                .extensions(INotificationCatalogListener.class);
        for (INotificationCatalogListener cl : catalogListeners) {
            cl.setNotificationConfiguration(cfg);
            geoServer.getCatalog().addListener(cl);
        }

        List<INotificationTransactionListener> transactionListeners = GeoServerExtensions
                .extensions(INotificationTransactionListener.class);
        for (INotificationTransactionListener tl : transactionListeners) {
            tl.setNotificationConfiguration(cfg);
        }
    }

    private NotificationConfiguration getConfiguration(XStream xs) {
        NotificationConfiguration nc = null;
        try {
            Resource f = this.loader.get(Paths.path("notifier", PROPERTYFILENAME));
            if (Resources.exists(f)) {
                nc = (NotificationConfiguration) xs.fromXML(f.in());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return nc;
    }

}
