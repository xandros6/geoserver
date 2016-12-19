/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import java.io.Serializable;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.notification.Notification;
import org.geoserver.notification.NotificationEncoder;
import org.geoserver.notification.geonode.kombu.KombuMessage;
import org.geoserver.notification.geonode.kombu.KombuNamespaceInfo;
import org.geotools.util.logging.Logging;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoNodeJsonEncoder implements NotificationEncoder, Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -4363676711490507482L;

    static Logger LOGGER = Logging.getLogger(GeoNodeJsonEncoder.class);

    @Override
    public byte[] encode(Notification notification) {
        byte[] ret = null;
        ObjectMapper mapper = new ObjectMapper();
        DateFormat isodf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        mapper.setDateFormat(isodf);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.enableDefaultTyping(); // default to using DefaultTyping.OBJECT_AND_NON_CONCRETE
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        KombuMessage message = new KombuMessage();
        try {
            message.setId(notification.getHandle());
            message.setType(notification.getType().name());
            message.setAction(notification.getAction().name());
            message.setTimestamp(new Date());
            message.setUser(notification.getUser());
            message.setOriginator(InetAddress.getLocalHost().getHostAddress());
            if (notification.getObject() instanceof NamespaceInfo) {
                NamespaceInfo obj = (NamespaceInfo) notification.getObject();
                KombuNamespaceInfo source = new KombuNamespaceInfo();
                source.setId(notification.getHandle());
                source.setType("NamespaceInfo");
                source.setName(obj.getName());
                source.setNamespaceURI(obj.getURI());
                message.setSource(source);
            }
            // mapper.writeValueAsString(message)
            ret = mapper.writeValueAsBytes(message);
        } catch (Exception e) {
            LOGGER.log(Level.FINER, e.getMessage(), e);
        }

        return ret;

    }

}
