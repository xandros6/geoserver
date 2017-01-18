/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common.sender;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.notification.common.CustomSaslConfig;
import org.geoserver.notification.common.Notification;
import org.geotools.util.logging.Logging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class RabbitMQSender implements NotificationSender, Serializable {

    private static Logger LOGGER = Logging.getLogger(RabbitMQSender.class);

    /** serialVersionUID */
    private static final long serialVersionUID = 1370640635300148935L;

    protected String host;

    protected String virtualHost;

    protected int port;

    protected String username;

    protected String password;

    protected String uri;

    protected Connection conn;

    protected Channel channel;

    public void initialize() throws Exception {

        if (uri == null) {
            if (this.username != null && !this.username.isEmpty() && this.password != null
                    && !this.password.isEmpty()) {
                this.uri = "amqp://" + this.username + ":" + this.password + "@" + this.host + ":"
                        + this.port + "/" + this.virtualHost;
            } else {
                this.uri = "amqp://" + this.host + ":" + this.port + "/" + this.virtualHost;
            }

        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.uri);
        factory.setSaslConfig(new CustomSaslConfig());
        conn = factory.newConnection();
        channel = conn.createChannel();
    }

    public void close() throws Exception {

        if (this.channel != null) {
            this.channel.close();
        }

        if (this.conn != null) {
            this.conn.close();
        }

    }

    // Prepare Connection Channel
    public void send(Notification notification, byte[] payload) throws Exception {
        try {
            this.initialize();
            this.sendMessage(notification, payload);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            this.close();
        }
    }

    // Send message to the Queue by using Channel
    public abstract void sendMessage(Notification notification, byte[] payload) throws IOException;

}
