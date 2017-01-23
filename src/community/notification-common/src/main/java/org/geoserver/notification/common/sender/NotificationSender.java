/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common.sender;

import org.geoserver.notification.common.Notification;

/**
 * Sends an encoded payload to some destination
 */
public interface NotificationSender {

    public void send(Notification notification, byte[] payload) throws Exception;

}