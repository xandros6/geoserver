/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

/**
 * Encodes a notification into some paylad format
 */
public interface NotificationEncoder {

    public byte[] encode(Notification notification);
}
