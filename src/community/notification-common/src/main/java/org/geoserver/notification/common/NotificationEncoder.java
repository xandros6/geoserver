/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

/**
 * Encodes a notification into some paylad format
 */
public interface NotificationEncoder {

    public byte[] encode(Notification notification) throws Exception;
}
