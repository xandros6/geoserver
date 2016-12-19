package org.geoserver.notification;

import com.thoughtworks.xstream.XStream;

public interface NotificationXStreamInitializer {

    public void init(XStream xs);

}
