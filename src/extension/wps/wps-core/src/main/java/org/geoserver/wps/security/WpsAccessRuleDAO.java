/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.security;

import static org.geoserver.security.impl.DataAccessRule.ANY;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.PropertyFileWatcher;
import org.geoserver.security.impl.AbstractAccessRuleDAO;
import org.geoserver.wps.ProcessAccessInfo;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.process.GeoServerProcessors;
import org.geotools.process.ProcessFactory;
import org.geotools.util.logging.Logging;
import org.opengis.feature.type.Name;

/**
 * Allows one to manage the rules used by the per layer security subsystem
 * TODO: consider splitting the persistence of properties into two strategies,
 * and in memory one, and a file system one (this class is so marginal that
 * I did not do so right away, in memory access is mostly handy for testing)
 */
public class WpsAccessRuleDAO extends AbstractAccessRuleDAO<WpsAccessRule> {
    private final static Logger LOGGER = Logging.getLogger(WpsAccessRuleDAO.class);

    /**
     * property file name
     */
    static final String WPS_PROP_FILE = "wps.xml";

    private GeoServer gs;
    private WPSInfo wps;

    /**
     * Default to the highest security mode
     */
    CatalogMode catalogMode = CatalogMode.HIDE;


    public static WpsAccessRuleDAO get() {
        return GeoServerExtensions.bean(WpsAccessRuleDAO.class); 
    }


    public WpsAccessRuleDAO(GeoServerDataDirectory dd, GeoServer gs) throws IOException {
        super(dd, WPS_PROP_FILE);
        this.wps = gs.getService(WPSInfo.class);
        this.gs = gs;
    }

    WpsAccessRuleDAO(GeoServer gs, File securityDir) {
        super(securityDir, WPS_PROP_FILE);
        this.wps = gs.getService(WPSInfo.class);
        this.gs = gs;
    }

    public CatalogMode getMode() {
        checkPropertyFile(false);
        return catalogMode;
    }

    @Override
    protected void checkPropertyFile(boolean force) {
        if (rules == null || force) {
            loadRules(null);
            lastModified = System.currentTimeMillis();
        }
    }
    
    @Override
    public boolean isModified() {
        // TODO Auto-generated method stub
        return super.isModified();
    }

    @Override
    protected void loadRules(Properties props) {    
        this.wps = this.gs.getService(WPSInfo.class);
        TreeSet<WpsAccessRule> result = new TreeSet<WpsAccessRule>();
        catalogMode = CatalogMode.HIDE;
        if(this.wps.getCatalogMode() != null){
            catalogMode = this.wps.getCatalogMode();
        }
        for(ProcessGroupInfo group : this.wps.getProcessGroups()){
            Set<String> prefixes = new HashSet<String>();
            ProcessFactory pf = GeoServerProcessors.getProcessFactory(group.getFactoryClass(), false);
            if(pf != null) {
                Set<Name> names = pf.getNames();
                for (Name name : names) {
                    prefixes.add(name.getNamespaceURI());
                }
            }

            for(String prefix: prefixes){
                if(group.getRoles()!=null && !group.getRoles().isEmpty()){                
                    result.add(new WpsAccessRule(prefix,ANY,new HashSet<String>(group.getRoles())));
                }
            }
            for(ProcessAccessInfo process : group.getFilteredProcesses()){
                if(process.getRoles()!=null && !process.getRoles().isEmpty()){
                    result.add(new WpsAccessRule(process.getName().getNamespaceURI(),process.getName().getLocalPart(),new HashSet<String>(process.getRoles())));
                }
            }
        }
        // make sure the two basic rules if the set is empty
        if(result.size() == 0) {
            result.add(new WpsAccessRule(WpsAccessRule.EXECUTE_ALL));
        }

        rules = result;
    }

    @Override
    protected Properties toProperties() {
        Properties props = new Properties();
        props.put("mode", catalogMode.toString());
        for (WpsAccessRule rule : rules) {
            String key = rule.getGroupName().replaceAll("\\.", "\\\\.") + "." 
                    + rule.getWpsName().replaceAll("\\.", "\\\\.");
            props.put(key, rule.getValue());
        }
        return props;
    }

}
