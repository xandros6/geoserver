/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import org.geoserver.wfs.TransactionPlugin;

public interface INotificationTransactionListener extends TransactionPlugin {

    public NotificationConfiguration getNotificationConfiguration();

    public void setNotificationConfiguration(NotificationConfiguration ncfg);

}
