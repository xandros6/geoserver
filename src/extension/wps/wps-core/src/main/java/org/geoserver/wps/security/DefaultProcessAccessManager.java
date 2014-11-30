package org.geoserver.wps.security;

import static org.geoserver.security.impl.DataAccessRule.ANY;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.security.AccessMode;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.CoverageAccessLimits;
import org.geoserver.security.DataAccessLimits;
import org.geoserver.security.VectorAccessLimits;
import org.geoserver.security.WMSAccessLimits;
import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.impl.DataAccessRuleDAO;
import org.geoserver.security.impl.SecureTreeNode;
import org.geotools.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.springframework.security.core.Authentication;

public class DefaultProcessAccessManager implements ProcessAccessManager{

    private static final Logger LOGGER = Logging.getLogger(DefaultProcessAccessManager.class);
    
    private SecureTreeNode root;
    
    private WpsAccessRuleDAO dao;
    
    long lastLoaded = Long.MIN_VALUE;

    public DefaultProcessAccessManager(WpsAccessRuleDAO dao) {
        this.dao = dao;
        this.root = buildAuthorizationTree(dao);
    }

    public CatalogMode getMode() {
        return dao.getMode();
    }
    
    @Override
    public ProcessAccessLimits getAccessLimits(Authentication user, String namespace) {
        checkPropertyFile();        
        SecureTreeNode node = root.getDeepestNode(new String[] { namespace });        
        return new ProcessAccessLimits(dao.getMode(), node.canAccess(user, AccessMode.READ));
    }

    @Override
    public ProcessAccessLimits getAccessLimits(Authentication user, Name process) {
        checkPropertyFile();
        SecureTreeNode node = root.getDeepestNode(new String[] { process.getNamespaceURI(), process.getLocalPart() });
        return new ProcessAccessLimits(dao.getMode(), node.canAccess(user, AccessMode.READ));
    }
    
    private void checkPropertyFile() {
        long daoLastModified = dao.getLastModified();
        if(lastLoaded < daoLastModified) {
            root = buildAuthorizationTree(dao);
            lastLoaded = daoLastModified;
        }
    }
    
    private SecureTreeNode buildAuthorizationTree(WpsAccessRuleDAO dao) {
        SecureTreeNode root = new SecureTreeNode();
        List<WpsAccessRule> rules = dao.getRules();
        for(WpsAccessRule rule : rules) {
            String group = rule.getGroupName();
            String name = rule.getWpsName();
            
            // look for the node where the rules will have to be set
            SecureTreeNode node;

            // check for the * group definition
            if (ANY.equals(group)) {
                node = root;
            } else {
                // get or create the group
                SecureTreeNode ws = root.getChild(group);
                if (ws == null) {
                    ws = root.addChild(group);
                }

                // if WPS is "*" the rule applies to the group, otherwise
                // get/create the WPS
                if ("*".equals(name)) {
                    node = ws;
                } else {
                    SecureTreeNode layerNode = ws.getChild(name);
                    if (layerNode == null)
                        layerNode = ws.addChild(name);
                    node = layerNode;
                }

            }

            // actually set the rule, but don't complain for the default root contents
            if (node != root) {
                LOGGER.warning("Rule " + rule
                        + " is overriding another rule targetting the same resource");
            }
            node.setAuthorizedRoles(AccessMode.READ, rule.getRoles());
            node.setAuthorizedRoles(AccessMode.WRITE,  Collections.singleton("NO_ONE"));
            node.setAuthorizedRoles(AccessMode.ADMIN,  Collections.singleton("NO_ONE"));
        }
        root.setAuthorizedRoles(AccessMode.READ, Collections.singleton("*"));
        root.setAuthorizedRoles(AccessMode.WRITE,  Collections.singleton("NO_ONE"));
        root.setAuthorizedRoles(AccessMode.ADMIN,  Collections.singleton("NO_ONE"));
        return root;
    }

}
