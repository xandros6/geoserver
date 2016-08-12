package org.geoserver.security.onelogin;

import java.util.List;

import org.geoserver.config.util.XStreamPersister;
import org.geoserver.security.ConstantFilterChain;
import org.geoserver.security.GeoServerAuthenticationProvider;
import org.geoserver.security.GeoServerSecurityFilterChain;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.RequestFilterChain;
import org.geoserver.security.SecurityManagerListener;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.AbstractFilterProvider;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

public class OneloginSecurityProvider extends AbstractFilterProvider implements SecurityManagerListener{

    private SAMLEntryPoint samlEntryPoint;
    private MetadataGeneratorFilter metadataGeneratorFilter;
    private OneloginAuthenticationProvider samlAuthenticationProvider;

    public OneloginSecurityProvider(GeoServerSecurityManager securityManager, SAMLUserDetailsService samlUserDetailsServiceImpl, SAMLEntryPoint samlEntryPoint, MetadataGeneratorFilter metadataGeneratorFilter, SAMLAuthenticationProvider samlAuthenticationProvider) {
        this.samlEntryPoint = samlEntryPoint;
        this.metadataGeneratorFilter = metadataGeneratorFilter;
        this.samlAuthenticationProvider = new OneloginAuthenticationProvider(samlAuthenticationProvider);
        securityManager.addListener(this);
    }
    
    @Override
    public void handlePostChanged(GeoServerSecurityManager securityManager) {
        List<GeoServerAuthenticationProvider> aps = securityManager.getAuthenticationProviders();
        if(aps != null && !aps.contains(this.samlAuthenticationProvider)){
            //aps.add(new OneloginAuthenticationProvider(samlAuthenticationProvider));
            securityManager.getProviders().add(samlAuthenticationProvider);
        }
    }
    
    @Override
    public void configure(XStreamPersister xp) {
        super.configure(xp);
        xp.getXStream().alias("oneloginAuthentication", OneloginAuthenticationFilterConfig.class);
    }
    /*
    @Override
    public Class<OneloginAuthenticationProvider> getAuthenticationProviderClass() {
        return OneloginAuthenticationProvider.class;
    }
    
    @Override
    public GeoServerAuthenticationProvider createAuthenticationProvider(
            SecurityNamedServiceConfig config) {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setUserDetails(samlUserDetailsServiceImpl);
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return new OneloginAuthenticationProvider(samlAuthenticationProvider);
    }*/
    
    @Override
    public Class<? extends GeoServerSecurityFilter> getFilterClass() {
        return OneloginAuthenticationFilter.class;
    }
    
    @Override
    public GeoServerSecurityFilter createFilter(SecurityNamedServiceConfig config) {
        return new OneloginAuthenticationFilter(this.samlEntryPoint,this.metadataGeneratorFilter);
    }

    @Override
    public void configureFilterChain(GeoServerSecurityFilterChain filterChain) {
        if ( filterChain.getRequestChainByName("samlChain") != null)
            return;
        
        RequestFilterChain samlChain =  new ConstantFilterChain("/saml/SSO/**");
        samlChain.setFilterNames("samlWebSSOProcessingFilter");
        samlChain.setName("samlChain");
        filterChain.getRequestChains().add(0,samlChain);

    }
}
