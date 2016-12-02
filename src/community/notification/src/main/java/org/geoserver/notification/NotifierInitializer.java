/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInitializer;
import org.geotools.util.logging.Logging;

public class NotifierInitializer  implements GeoServerInitializer {

    static Logger LOGGER = Logging.getLogger(NotifierInitializer.class);

    Notifier notifier;
    
    public NotifierInitializer( Notifier notifier) {
        this.notifier = notifier;
    }

    public void initialize(GeoServer geoServer) throws Exception {
        notifier.setServer(geoServer);
    }

}
