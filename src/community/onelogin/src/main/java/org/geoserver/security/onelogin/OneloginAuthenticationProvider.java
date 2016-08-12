package org.geoserver.security.onelogin;

import org.geoserver.security.DelegatingAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;

public class OneloginAuthenticationProvider extends DelegatingAuthenticationProvider {

    public OneloginAuthenticationProvider(AuthenticationProvider authProvider) {
        super(authProvider);
    }
    
    

}
