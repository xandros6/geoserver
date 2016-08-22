/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.onelogin;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.security.impl.GeoServerRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

/**
 * Assigns {@link GeoServerRole#AUTHENTICATED_ROLE} to user after successful authentication
 */

public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {

    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {

        // The method is supposed to identify local account of user referenced by
        // data in the SAML assertion and return UserDetails object describing the user.

        String userID = credential.getNameID().getValue();

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        GrantedAuthority authority = GeoServerRole.AUTHENTICATED_ROLE;
        authorities.add(authority);

        return new User(userID, "", true, true, true, true, authorities);
    }

}
