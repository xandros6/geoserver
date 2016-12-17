/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterInitializer;

import com.thoughtworks.xstream.XStream;

public abstract class NotificationXStreamPersisterDefaultInitializer implements
        XStreamPersisterInitializer {

    @Override
    public void init(XStreamPersister persister) {
        XStream xs = persister.getXStream();
        xs.alias("notificationConfiguration", NotificationConfiguration.class);
        xs.alias("notificator", Notificator.class);
        xs.alias("genericProcessor", NotificationProcessor.class);
        xs.alias("encoder", NotificationEncoder.class);
        xs.alias("sender", NotificationSender.class);
        xs.addDefaultImplementation(DefaultNotificationProcessor.class, NotificationProcessor.class);
        xs.addImplicitCollection(NotificationConfiguration.class, "notificators");
        xs.allowTypes(new Class[] { NotificationConfiguration.class, Notificator.class,
                NotificationProcessor.class, NotificationEncoder.class, NotificationSender.class });
        persister.registerBreifMapComplexType("notificationConfiguration",
                NotificationConfiguration.class);
    }

}
