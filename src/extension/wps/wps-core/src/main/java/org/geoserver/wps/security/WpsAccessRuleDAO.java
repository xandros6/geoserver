/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.security;

import static org.geoserver.security.impl.DataAccessRule.ANY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.impl.AbstractAccessRuleDAO;
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
    static final String WPS_PROP_FILE = "wps.properties";


    protected GeoServerInfo gs;
    private WPSInfo wps;

    /**
     * Default to the highest security mode
     */
    CatalogMode catalogMode = CatalogMode.HIDE;


    /**
     * Returns the instanced contained in the Spring context for the UI to use
     * @return
     */
    public static WpsAccessRuleDAO get() {
        return GeoServerExtensions.bean(WpsAccessRuleDAO.class); 
    }

    /**
     * Builds a new dao
     * 
     * @param rawCatalog
     */
    public WpsAccessRuleDAO(GeoServerDataDirectory dd, GeoServer gs) throws IOException {
        super(dd, WPS_PROP_FILE);
        this.wps = gs.getService(WPSInfo.class);
        this.gs = gs.getGlobal();
    }

    /**
     * Builds a new dao with a custom security dir. Used mostly for testing purposes
     * 
     * @param rawCatalog
     */
    WpsAccessRuleDAO(GeoServer gs, File securityDir) {
        super(securityDir, WPS_PROP_FILE);
        this.wps = gs.getService(WPSInfo.class);
        this.gs = gs.getGlobal();
    }

    /**
     * The way the catalog should react to unauthorized access
     * 
     * @return
     */
    public CatalogMode getMode() {
        checkPropertyFile(false);
        return catalogMode;
    }

    /**
     * Parses the rules contained in the property file
     * 
     * @param props
     * @return
     */
    protected void loadRules(Properties props) {
        TreeSet<WpsAccessRule> result = new TreeSet<WpsAccessRule>();
        catalogMode = CatalogMode.HIDE;
        for (Map.Entry<Object,Object> entry : props.entrySet()) {
            String ruleKey = (String) entry.getKey();
            String ruleValue = (String) entry.getValue();

            // check for the mode
            if ("mode".equalsIgnoreCase(ruleKey)) {
                try {
                    catalogMode = CatalogMode.valueOf(ruleValue.toUpperCase());
                } catch (Exception e) {
                    LOGGER.warning("Invalid security mode " + ruleValue + " acceptable values are "
                            + Arrays.asList(CatalogMode.values()));
                }
            } else {
                WpsAccessRule rule = parseDataAccessRule(ruleKey, ruleValue);
                if (rule != null) {
                    if (result.contains(rule))
                        LOGGER.warning("Rule " + ruleKey + "." + ruleValue
                                + " overwrites another rule on the same path");
                    result.add(rule);
                }
            }
        }

        // make sure the two basic rules if the set is empty
        if(result.size() == 0) {
            result.add(new WpsAccessRule(WpsAccessRule.EXECUTE_ALL));
        }

        rules = result;
    }

    /**
     * Parses a single layer.properties line into a {@link WpsAccessRule}, returns false if the
     * rule is not valid
     * 
     * @return
     */
    WpsAccessRule parseDataAccessRule(String ruleKey, String ruleValue) {
        final String rule = ruleKey + "=" + ruleValue;

        // parse
        String[] elements = parseElements(ruleKey);
        if(elements.length != 2) {
            LOGGER.warning("Invalid rule " + rule + ", the expected format is group.wps=role1,role2,...");
            return null;
        }
        String groupName = elements[0];
        String wpsName = elements[1];
        Set<String> roles = parseRoles(ruleValue);

        // perform basic checks on the elements
        if (elements.length != 2) {
            LOGGER.warning("Invalid rule '" + rule
                    + "', the standard form is [group].[wps]=[role]+ "
                    + "Rule has been ignored");
            return null;
        }
        // emit warnings for unknown workspaces, layers, but don't skip the rule,
        // people might be editing the catalog structure and will edit the access rule
        // file afterwards
        boolean groupFound = false;
        boolean wpsFound = false;
        if (!ANY.equals(groupName)){
            List<ProcessGroupInfo> groups = this.wps.getProcessGroups();
            ext:for(ProcessGroupInfo group : groups){
                ProcessFactory pf = GeoServerProcessors.getProcessFactory(group.getFactoryClass(), false);
                Set<Name> processes = pf.getNames();
                for(Name process : processes){
                    if(!groupFound && groupName.equals(process.getNamespaceURI())){
                        groupFound = true;
                    }
                    if(!wpsFound){
                        if(!ANY.equals(wpsName)){
                            if(wpsName.equals(process.getLocalPart())){
                                wpsFound = true;
                            }
                        }else{
                            wpsFound = true;
                        }
                    }
                    if(groupFound && wpsFound){
                        break ext;
                    }
                }
            }
        }else{
            groupFound = true;
            wpsFound = true;
        }
        if(!groupFound){
            LOGGER.warning("WPS group " + groupName + " is unknown in rule " + rule);
        }
        if(!wpsFound){
            LOGGER.warning("WPS name " + wpsName + " is unknown in rule " + rule);
        }
        if (ANY.equals(groupName)) {
            if (!ANY.equals(wpsName)) {
                LOGGER.warning("Invalid rule " + rule + ", when WPS group "
                        + "is * then also WPS name must be *. Skipping rule " + rule);
                return null;
            }
        }
        return new WpsAccessRule(groupName, wpsName, roles);

    }

    /**
     * Turns the rules list into a property bag
     * @return
     */
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

    /**
     * Parses workspace.layer.mode into an array of strings
     * 
     * @param path
     * @return
     */
    static String[] parseElements(String path) {
        String[] rawParse = path.trim().split("\\s*\\.\\s*");
        List<String> result = new ArrayList<String>();
        String prefix = null;
        for (String raw : rawParse) {
            if(prefix != null)
                raw = prefix + "."  + raw;
            // just assume the escape is invalid char besides \. and check it once only
            if (raw.endsWith("\\")) {
                prefix = raw.substring(0, raw.length() - 1);
            } else {
                result.add(raw);
                prefix = null;
            }
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    public void setCatalogMode(CatalogMode catalogMode) {
        this.catalogMode = catalogMode;
    }

    public static CatalogMode getByAlias(String alias){
        for(CatalogMode mode: CatalogMode.values()){
            if(mode.name().equals(alias)){
                return mode;
            }
        }
        return null;
    }

    /**
     * Returns a sorted set of rules associated to the role
     * 
     * @param role
     * @return
     */
    public SortedSet<WpsAccessRule> getRulesAssociatedWithRole(String role) {
        SortedSet<WpsAccessRule> result = new TreeSet<WpsAccessRule>();
        for (WpsAccessRule rule: getRules())
            if (rule.getRoles().contains(role))
                result.add(rule);
        return result;
    }		
}
