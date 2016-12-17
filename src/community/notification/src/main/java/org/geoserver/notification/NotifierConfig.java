/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;

public class NotifierConfig {

    static Logger LOGGER = Logging.getLogger(NotifierConfig.class);

    public static final String PROPERTYFILENAME = "notifier.xml";

    private GeoServerResourceLoader loader;

    private XStreamPersisterFactory xstreamPersisterFactory;

    public NotifierConfig(GeoServerResourceLoader loader,
            XStreamPersisterFactory xstreamPersisterFactory) throws IOException {
        this.loader = loader;
        this.xstreamPersisterFactory = xstreamPersisterFactory;
    }

    public NotificationConfiguration getConfiguration() {
        NotificationConfiguration nc = null;
        try {
            Resource f = this.loader.get(Paths.path("notifier", NotifierConfig.PROPERTYFILENAME));
            if (Resources.exists(f)) {
                nc = xstreamPersisterFactory.createXMLPersister().load(f.in(),
                        NotificationConfiguration.class);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return nc;
    }
}
