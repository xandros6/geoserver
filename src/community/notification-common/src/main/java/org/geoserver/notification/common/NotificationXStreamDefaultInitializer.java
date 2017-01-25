/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

import org.geoserver.notification.common.sender.FanoutRabbitMQSender;
import org.geoserver.notification.common.sender.NotificationSender;

import com.thoughtworks.xstream.XStream;

/**
 * Base class for notifier Xstream configuration mapper
 * 
 * @author Xandros
 *
 */
public abstract class NotificationXStreamDefaultInitializer implements
        NotificationXStreamInitializer {

    @Override
    public void init(XStream xs) {
        xs.alias("notificationConfiguration", NotificationConfiguration.class);
        xs.alias("notificator", Notificator.class);
        xs.alias("genericProcessor", NotificationProcessor.class);
        xs.addDefaultImplementation(DefaultNotificationProcessor.class, NotificationProcessor.class);
        xs.addImplicitCollection(NotificationConfiguration.class, "notificators");
        xs.allowTypes(new Class[] { NotificationConfiguration.class, Notificator.class,
                NotificationProcessor.class, NotificationEncoder.class, NotificationSender.class });
    }

    /**
     * Define an alias for the {@link DefaultNotificationProcessor#encoder encoder}<br>
     * Define an alias for the {@link DefaultNotificationProcessor#sender sender}<br>
     * Define a class for the {@link NotificationEncoder}<br>
     * Define a class for the{@link NotificationSender}<br>
     * An example of configuration section in notifier.xml is:
     * 
     * <pre>
     *  {@code
     *  <genericProcessor>
     *           <geonodeEncoder />
     *           <fanoutSender>
     *                   ...
     *           </fanoutSender>
     * </genericProcessor>
     *  }
     * </pre>
     * 
     * @param xs XStream object
     * @param encoderName alias for encoder field of in xml configuration
     * @param encoderClass class to use for encoder with filed name specified in encoderName param
     * @param senderName alias for sender field of in xml configuration
     * @param senderClass class to use for sender with filed name specified in senderName param
     * 
     */
    protected void configure(XStream xs, String encoderName,
            Class<? extends NotificationEncoder> encoderClass, String senderName,
            Class<? extends NotificationSender> senderClass) {
        xs.alias(encoderName, NotificationEncoder.class, encoderClass);
        xs.alias(senderName, NotificationSender.class, senderClass);
        xs.aliasField(encoderName, DefaultNotificationProcessor.class, "encoder");
        xs.aliasField(senderName, DefaultNotificationProcessor.class, "sender");
    }

}
