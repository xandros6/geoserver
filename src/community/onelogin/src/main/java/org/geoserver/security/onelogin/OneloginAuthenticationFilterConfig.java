/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.onelogin;

import org.geoserver.security.config.SecurityAuthFilterConfig;
import org.geoserver.security.config.SecurityFilterConfig;

/**
 * Configuration for OneLogin authentication
 */

public class OneloginAuthenticationFilterConfig extends SecurityFilterConfig implements
        SecurityAuthFilterConfig {

    private static final long serialVersionUID = 1199751476823173800L;

    private String entityId;

    private String metadataURL;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getMetadataURL() {
        return metadataURL;
    }

    public void setMetadataURL(String metadataURL) {
        this.metadataURL = metadataURL;
    }

    @Override
    public boolean providesAuthenticationEntryPoint() {
        return true;
    }

}
