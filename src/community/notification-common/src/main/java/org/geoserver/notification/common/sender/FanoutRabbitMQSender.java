/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common.sender;

import java.io.IOException;

import org.geoserver.notification.common.Notification;

public class FanoutRabbitMQSender extends RabbitMQSender {

    /** serialVersionUID */
    private static final long serialVersionUID = -1947966245086626842L;

    public static final String EXCHANGE_TYPE = "fanout";

    protected String exchangeName;

    protected String routingKey;

    @Override
    public void sendMessage(Notification notification, byte[] payload) throws IOException {

        channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);
        channel.basicPublish(exchangeName, routingKey, null, payload);

    }

}
