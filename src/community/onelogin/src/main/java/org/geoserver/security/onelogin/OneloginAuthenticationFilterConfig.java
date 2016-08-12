package org.geoserver.security.onelogin;

import org.geoserver.security.config.SecurityAuthFilterConfig;
import org.geoserver.security.config.SecurityFilterConfig;

public class OneloginAuthenticationFilterConfig extends SecurityFilterConfig implements SecurityAuthFilterConfig  {

    /** serialVersionUID */
    private static final long serialVersionUID = 1199751476823173800L;
    
    @Override
    public boolean providesAuthenticationEntryPoint() {
        return true;
    }

}
