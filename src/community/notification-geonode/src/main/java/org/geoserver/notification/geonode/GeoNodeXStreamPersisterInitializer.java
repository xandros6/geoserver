/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.geonode;

import org.geoserver.notification.common.NotificationEncoder;
import org.geoserver.notification.common.NotificationXStreamDefaultInitializer;
import org.geoserver.notification.common.sender.FanoutRabbitMQSender;
import org.geoserver.notification.common.sender.NotificationSender;

public class GeoNodeXStreamPersisterInitializer extends NotificationXStreamDefaultInitializer {

    @Override
    public String getEncoderName() {
        return "geonodeEncoder";
    }

    @Override
    public Class<? extends NotificationEncoder> getEncoderClass() {
        return GeoNodeJsonEncoder.class;
    }

    @Override
    public String getSenderName() {
        return "fanoutSender";
    }

    @Override
    public Class<? extends NotificationSender> getSenderClass() {
        return FanoutRabbitMQSender.class;
    }

}
