/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import org.geoserver.config.util.XStreamPersister;
import org.geoserver.notification.FanoutRabbitMQSender;
import org.geoserver.notification.NotificationEncoder;
import org.geoserver.notification.NotificationSender;
import org.geoserver.notification.NotificationXStreamPersisterDefaultInitializer;

import com.thoughtworks.xstream.XStream;

public class GeoNodeXStreamPersisterInitializer extends
        NotificationXStreamPersisterDefaultInitializer {

    @Override
    public void init(XStreamPersister persister) {
        super.init(persister);
        XStream xs = persister.getXStream();
        xs.addDefaultImplementation(GeoNodeJsonEncoder.class, NotificationEncoder.class);
        xs.addDefaultImplementation(FanoutRabbitMQSender.class, NotificationSender.class);
    }

}
