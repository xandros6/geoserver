package org.geoserver.security.onelogin;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.GeoServerAuthenticationFilter;
import org.geoserver.security.filter.GeoServerCompositeFilter;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.AuthenticationEntryPoint;

public class OneloginAuthenticationFilter extends GeoServerCompositeFilter implements GeoServerAuthenticationFilter{

    protected SAMLEntryPoint samlEntryPoint;
    
    public OneloginAuthenticationFilter(SAMLEntryPoint samlEntryPoint, MetadataGeneratorFilter metadataGeneratorFilter) {
        this.samlEntryPoint = samlEntryPoint;
        nestedFilters.add(metadataGeneratorFilter);
        //nestedFilters.add(this.samlEntryPoint);
        
    }

    @Override
    public void initializeFromConfig(SecurityNamedServiceConfig config) throws IOException {
        super.initializeFromConfig(config);
    }
   
    @Override
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.samlEntryPoint;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setAttribute(GeoServerSecurityFilter.AUTHENTICATION_ENTRY_POINT_HEADER,  this.samlEntryPoint);
        super.doFilter(req, res, chain);
    }

    @Override
    public boolean applicableForHtml() {
        return true;
    }

    @Override
    public boolean applicableForServices() {
        return true;
    }

}
