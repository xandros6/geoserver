/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

import java.io.Serializable;

public class DefaultNotificationProcessor implements NotificationProcessor, Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -981618390262055505L;

    private NotificationEncoder encoder;

    private NotificationSender sender;

    public DefaultNotificationProcessor() {
        super();
    }

    public DefaultNotificationProcessor(NotificationEncoder encoder, NotificationSender sender) {
        super();
        this.encoder = encoder;
        this.sender = sender;
    }

    @Override
    public void process(Notification notification) throws Exception {
        byte[] payload = encoder.encode(notification);
        sender.send(notification, payload);
    }

    public NotificationEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(NotificationEncoder encoder) {
        this.encoder = encoder;
    }

    public NotificationSender getSender() {
        return sender;
    }

    public void setSender(NotificationSender sender) {
        this.sender = sender;
    }

}
