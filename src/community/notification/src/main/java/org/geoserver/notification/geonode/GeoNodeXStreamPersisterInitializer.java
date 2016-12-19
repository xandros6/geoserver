/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import org.geoserver.notification.FanoutRabbitMQSender;
import org.geoserver.notification.NotificationEncoder;
import org.geoserver.notification.NotificationSender;
import org.geoserver.notification.NotificationXStreamDefaultInitializer;

import com.thoughtworks.xstream.XStream;

public class GeoNodeXStreamPersisterInitializer extends NotificationXStreamDefaultInitializer {

    @Override
    public void init(XStream xs) {
        super.init(xs);
        xs.alias("encoder", GeoNodeJsonEncoder.class);
        xs.alias("sender", FanoutRabbitMQSender.class);
        xs.addDefaultImplementation(GeoNodeJsonEncoder.class, NotificationEncoder.class);
        xs.addDefaultImplementation(FanoutRabbitMQSender.class, NotificationSender.class);
    }

}
