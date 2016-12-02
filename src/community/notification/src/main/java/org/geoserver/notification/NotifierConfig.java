/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.io.IOException;

import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.security.PropertyFileWatcher;
import org.geoserver.util.IOUtils;

public class NotifierConfig {

    protected static final String PROPERTYFILENAME = "notifier.properties";

    private GeoServerResourceLoader loader;
    
    private PropertyFileWatcher fw;

    public NotifierConfig(GeoServerResourceLoader loader) throws IOException {
        this.loader = loader;
        Resource f = getConfigurationFile(loader);
        fw = new PropertyFileWatcher(f);
    }

    public Resource getConfigurationFile(GeoServerResourceLoader loader) throws IOException {
        Resource f = loader.get(Paths.path("notifier", NotifierConfig.PROPERTYFILENAME));
        if (!Resources.exists(f)) {
            //IOUtils.copy(NotifierConfig.class.getResourceAsStream(NotifierConfig.PROPERTYFILENAME), 
            //        f.out());
        }
        return f;
    }
}
