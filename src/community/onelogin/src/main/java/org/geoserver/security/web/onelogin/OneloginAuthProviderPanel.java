/* (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.onelogin;

import org.apache.wicket.model.IModel;
import org.geoserver.security.onelogin.OneloginSecurityServiceConfig;
import org.geoserver.security.web.auth.AuthenticationProviderPanel;

/**
 * Configuration panel for {@link JDBCConnectAuthProvider}.
 *  
 */
public class OneloginAuthProviderPanel extends AuthenticationProviderPanel<OneloginSecurityServiceConfig> {

    /** serialVersionUID */
    private static final long serialVersionUID = 7353031770013140878L;

    public OneloginAuthProviderPanel(String id, IModel<OneloginSecurityServiceConfig> model) {
        super(id, model);
    }

}
