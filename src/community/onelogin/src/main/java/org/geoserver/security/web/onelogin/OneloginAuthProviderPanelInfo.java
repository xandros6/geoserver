/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.onelogin;

import org.geoserver.security.onelogin.OneloginAuthenticationProvider;
import org.geoserver.security.onelogin.OneloginSecurityServiceConfig;
import org.geoserver.security.web.auth.AuthenticationProviderPanelInfo;

/**
 * 
 * Configuration panel extension for {@link JDBCConnectAuthProvider}.
 *  
 */
public class OneloginAuthProviderPanelInfo extends AuthenticationProviderPanelInfo<OneloginSecurityServiceConfig, OneloginAuthProviderPanel>{

    /** serialVersionUID */
    private static final long serialVersionUID = 2786521869232176111L;

    public OneloginAuthProviderPanelInfo() {
        setComponentClass(OneloginAuthProviderPanel.class);
        setServiceClass(OneloginAuthenticationProvider.class);
        setServiceConfigClass(OneloginSecurityServiceConfig.class);
    }

}
