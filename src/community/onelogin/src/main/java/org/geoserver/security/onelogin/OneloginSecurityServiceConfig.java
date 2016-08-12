package org.geoserver.security.onelogin;

import org.geoserver.security.config.BaseSecurityNamedServiceConfig;
import org.geoserver.security.config.SecurityAuthProviderConfig;

public class OneloginSecurityServiceConfig extends BaseSecurityNamedServiceConfig implements SecurityAuthProviderConfig {

    /** serialVersionUID */
    private static final long serialVersionUID = -6139176073683370473L;

    @Override
    public String getUserGroupServiceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUserGroupServiceName(String userGroupServiceName) {
        // TODO Auto-generated method stub
        
    }

}
