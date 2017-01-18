/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import org.geoserver.notification.common.DefaultNotificationProcessor;
import org.geoserver.notification.common.NotificationEncoder;
import org.geoserver.notification.common.NotificationXStreamDefaultInitializer;
import org.geoserver.notification.common.sender.FanoutRabbitMQSender;
import org.geoserver.notification.common.sender.NotificationSender;

import com.thoughtworks.xstream.XStream;

public class GeoNodeXStreamPersisterInitializer extends NotificationXStreamDefaultInitializer {

    @Override
    public void init(XStream xs) {
        super.init(xs);
        xs.aliasField("GeoNodeJsonEncoder", DefaultNotificationProcessor.class, "encoder");
        xs.aliasField("RabbitMQSender", DefaultNotificationProcessor.class, "sender");
        xs.addDefaultImplementation(GeoNodeJsonEncoder.class, NotificationEncoder.class);
        xs.addDefaultImplementation(FanoutRabbitMQSender.class, NotificationSender.class);
    }

}
