/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.util.logging.Logging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Receiver {

    protected static Logger LOGGER = Logging.getLogger(Receiver.class);

    private static final String BROKER_URI = "amqp://guest:guest@localhost:4432";

    private final static String QUEUE_NAME = "jms/queue";

    private ReceiverService service;

    private Connection connection;

    private Channel channel;

    public Receiver(ReceiverService service) {
        this.service = service;
    }

    public void receive() throws Exception {
        // let's setup evrything and start listening
        ConnectionFactory factory = createConnectionFactory();

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare("testExchange", "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, "testExchange", "testRouting");
        channel.basicConsume(QUEUE_NAME, true, newConsumer(channel));
    }

    protected ConnectionFactory createConnectionFactory() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(BROKER_URI);
        return factory;
    }

    private DefaultConsumer newConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                service.manage(body);
            }
        };
    }

    public void close() {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
                LOGGER.log(Level.FINER, e.getMessage(), e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                LOGGER.log(Level.FINER, e.getMessage(), e);
            }
        }
    }

}
