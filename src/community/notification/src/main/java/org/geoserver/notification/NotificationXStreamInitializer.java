/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import com.thoughtworks.xstream.XStream;

public interface NotificationXStreamInitializer {

    public void init(XStream xs);

}
