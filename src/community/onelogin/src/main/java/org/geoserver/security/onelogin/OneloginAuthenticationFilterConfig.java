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

    /** serialVersionUID */
    private static final long serialVersionUID = 1199751476823173800L;

    @Override
    public boolean providesAuthenticationEntryPoint() {
        return true;
    }

}
