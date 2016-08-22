/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.onelogin;

import org.apache.wicket.model.IModel;
import org.geoserver.security.onelogin.OneloginAuthenticationFilterConfig;
import org.geoserver.security.web.auth.AuthenticationFilterPanel;

/**
 * Configuration panel for {@link JDBCConnectAuthProvider}.
 * 
 */
public class OneloginAuthFilterPanel extends
        AuthenticationFilterPanel<OneloginAuthenticationFilterConfig> {

    private static final long serialVersionUID = 7353031770013140878L;
    
    public OneloginAuthFilterPanel(String id, IModel<OneloginAuthenticationFilterConfig> model) {
        super(id, model);
    }



}
