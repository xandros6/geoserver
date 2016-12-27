/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import java.net.InetAddress;
import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.notification.common.Bounds;
import org.geoserver.notification.common.Notification;
import org.geoserver.notification.common.NotificationEncoder;
import org.geoserver.notification.geonode.kombu.KombuFeatureTypeInfo;
import org.geoserver.notification.geonode.kombu.KombuMessage;
import org.geoserver.notification.geonode.kombu.KombuNamespaceInfo;
import org.geoserver.notification.geonode.kombu.KombuWorkspaceInfo;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class GeoNodeJsonEncoder implements NotificationEncoder {

    @Override
    public byte[] encode(Notification notification) throws Exception {
        byte[] ret = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"));
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        KombuMessage message = new KombuMessage();

        message.setId(new UID().toString());
        message.setType(notification.getType() != null ? notification.getType().name() : null);
        message.setAction(notification.getAction() != null ? notification.getAction().name() : null);
        message.setTimestamp(new Date());
        message.setUser(notification.getUser());
        message.setOriginator(InetAddress.getLocalHost().getHostAddress());
        message.setProperties(notification.getActionProperties());
        if (notification.getObject() instanceof NamespaceInfo) {
            NamespaceInfo obj = (NamespaceInfo) notification.getObject();
            KombuNamespaceInfo source = new KombuNamespaceInfo();
            source.setId(obj.getId());
            source.setType("NamespaceInfo");
            source.setName(obj.getName());
            source.setNamespaceURI(obj.getURI());
            message.setSource(source);
        }
        if (notification.getObject() instanceof WorkspaceInfo) {
            WorkspaceInfo obj = (WorkspaceInfo) notification.getObject();
            KombuWorkspaceInfo source = new KombuWorkspaceInfo();
            source.setId(obj.getId());
            source.setType("WorkspaceInfo");
            source.setName(obj.getName());
            source.setNamespaceURI("");
            message.setSource(source);
        }
        if (notification.getObject() instanceof FeatureTypeInfo) {
            FeatureTypeInfo obj = (FeatureTypeInfo) notification.getObject();
            KombuFeatureTypeInfo source = new KombuFeatureTypeInfo();
            source.setId(obj.getId());
            source.setType("FeatureTypeInfo");
            source.setName(obj.getName());
            source.setWorkspace(obj.getStore().getWorkspace().getName());
            source.setNativeName(obj.getNativeName());
            source.setStore(obj.getStore().getName());
            if (obj.getNativeBoundingBox() != null) {
                source.setGeographicBunds(new Bounds(obj.getNativeBoundingBox()));
            }
            if (obj.boundingBox() != null) {
                source.setGeographicBunds(new Bounds(obj.boundingBox()));
            }
            message.setSource(source);
        }
        ret = mapper.writeValueAsBytes(message);
        return ret;

    }

}
