/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.onelogin;

import java.io.IOException;
import java.util.Timer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.GeoServerAuthenticationFilter;
import org.geoserver.security.filter.GeoServerCompositeFilter;
import org.geoserver.security.filter.GeoServerLogoutFilter;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.xml.parse.ParserPool;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.key.EmptyKeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * OneLogin Authentication filter that configures SP metadata discovery filter
 * and delegates to {@link #SAMLEntryPoint} the SAML authentication process
 */

public class OneloginAuthenticationFilter extends GeoServerCompositeFilter implements
        GeoServerAuthenticationFilter, LogoutHandler {

    protected SAMLEntryPoint samlEntryPoint;

    private static ApplicationContext context;

    public OneloginAuthenticationFilter(ApplicationContext ctx) {
        context = ctx;
        this.samlEntryPoint = context.getBean(SAMLEntryPoint.class);
    }

    @Override
    public void initializeFromConfig(SecurityNamedServiceConfig config) throws IOException {
        super.initializeFromConfig(config);
        OneloginAuthenticationFilterConfig authConfig = (OneloginAuthenticationFilterConfig) config;

        try {

            /*
             * Create metadata filter
             */
            MetadataGenerator generator = new MetadataGenerator();
            generator.setEntityId(authConfig.getEntityId());
            generator.setIncludeDiscoveryExtension(false);
            generator.setKeyManager(new EmptyKeyManager());
            generator.setRequestSigned(false);
            generator.setExtendedMetadata(new ExtendedMetadata());
            MetadataGeneratorFilter metadataGeneratorFilter = new MetadataGeneratorFilter(generator);

            /*
             * Create metadata provider
             */
            ParserPool parserPool = context.getBean(ParserPool.class);
            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setSoTimeout(5000);
            HttpClient httpClient = new HttpClient(clientParams);
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            HTTPMetadataProvider pro = new HTTPMetadataProvider(new Timer(true), httpClient,
                    authConfig.getMetadataURL());
            pro.setParserPool(parserPool);
            ExtendedMetadataDelegate emd = new ExtendedMetadataDelegate(pro, new ExtendedMetadata());

            /*
             * Set metadata provider and add filter to chain
             */
            MetadataManager metadata = context.getBean(MetadataManager.class);
            metadata.addMetadataProvider(emd);
            metadata.refreshMetadata();
            metadataGeneratorFilter.setManager(metadata);
            nestedFilters.add(metadataGeneratorFilter);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.samlEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setAttribute(GeoServerSecurityFilter.AUTHENTICATION_ENTRY_POINT_HEADER,
                this.samlEntryPoint);
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

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        request.setAttribute(GeoServerLogoutFilter.LOGOUT_REDIRECT_ATTR, SAMLLogoutFilter.FILTER_URL);
        
    }

}
