/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

/**
 * Sends an encoded payload to some destination
 */
public interface NotificationSender {

    public void send(Notification notification, byte[] payload) throws Exception;

}
