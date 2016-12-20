/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.HashMap;
import java.util.Map;

import net.opengis.wfs.TransactionResponseType;
import net.opengis.wfs.TransactionType;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.wfs.TransactionEvent;
import org.geoserver.wfs.TransactionEventType;
import org.geoserver.wfs.WFSException;
import org.opengis.feature.type.FeatureType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class NotificationTransactionListener extends NotificationListener implements
        INotificationTransactionListener {

    private static final String INSERTED = "inserted";

    private static final String DELETED = "deleted";

    private static final String UPDATED = "updated";

    private static final String TYPE = "type";

    private Catalog catalog;

    private ThreadLocal<Map<String, Map<String, Object>>> layersChangesResume = new ThreadLocal<Map<String, Map<String, Object>>>();

    public NotificationTransactionListener(Catalog catalog) {
        super();
        this.catalog = catalog;
    }

    @Override
    public NotificationConfiguration getNotificationConfiguration() {
        return notifierConfig;
    }

    @Override
    public void setNotificationConfiguration(NotificationConfiguration ncfg) {
        this.notifierConfig = ncfg;
    }

    @Override
    public TransactionType beforeTransaction(TransactionType request) throws WFSException {
        layersChangesResume = new ThreadLocal<Map<String, Map<String, Object>>>() {
            @Override
            protected Map<String, Map<String, Object>> initialValue() {
                return new HashMap<String, Map<String, Object>>();
            }
        };
        return null;
    }

    @Override
    public void beforeCommit(TransactionType request) throws WFSException {

    }

    @Override
    public void afterTransaction(TransactionType request, TransactionResponseType result,
            boolean committed) {
        if (committed) {
            String handle = request.getHandle();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = (auth != null) ? auth.getName() : null;
            Map<String, Map<String, Object>> lcrs = layersChangesResume.get();
            for (String layer : lcrs.keySet()) {
                Map<String, Object> prop = lcrs.get(layer);
                Object ft = prop.remove(TYPE);
                Notification n = new NotificationImpl(Notification.Type.Data, handle, null, ft,
                        prop, user);
                notify(n);
            }
        }

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void dataStoreChange(TransactionEvent event) throws WFSException {
        TransactionEventType eventType = event.getType();
        Integer affectedFeatures = event.getAffectedFeatures().size();
        FeatureType featureType = event.getAffectedFeatures().getSchema();
        FeatureTypeInfo fti = this.catalog.getFeatureTypeByName(featureType.getName());
        CatalogInfo info = ModificationProxy.unwrap(fti);
        String featureTypeName = featureType.getName().getURI();
        Map<String, Map<String, Object>> map = layersChangesResume.get();
        Map<String, Object> properties = map.get(featureTypeName);
        if (properties == null) {
            properties = new HashMap<String, Object>();
            properties.put(TYPE, info);
            map.put(featureTypeName, properties);
        }
        if (eventType == TransactionEventType.POST_INSERT) {
            Integer inserted = properties.get(INSERTED) != null ? (Integer) properties
                    .get(INSERTED) : 0;
            properties.put(INSERTED, inserted + affectedFeatures);
        }
        if (eventType == TransactionEventType.POST_UPDATE) {
            Integer inserted = properties.get(UPDATED) != null ? (Integer) properties.get(UPDATED)
                    : 0;
            properties.put(UPDATED, inserted + affectedFeatures);
        }
        if (eventType == TransactionEventType.PRE_DELETE) {
            Integer inserted = properties.get(DELETED) != null ? (Integer) properties.get(DELETED)
                    : 0;
            properties.put(DELETED, inserted + affectedFeatures);
        }
    }

}
