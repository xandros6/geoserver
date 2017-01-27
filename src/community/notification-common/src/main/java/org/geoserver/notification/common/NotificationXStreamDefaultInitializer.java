/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

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
        configure(xs);
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
     * 
     */
    protected void configure(XStream xs) {
        xs.alias(getEncoderName(), NotificationEncoder.class, getEncoderClass());
        xs.alias(getSenderName(), NotificationSender.class, getSenderClass());
        xs.aliasField(getEncoderName(), DefaultNotificationProcessor.class, "encoder");
        xs.aliasField(getSenderName(), DefaultNotificationProcessor.class, "sender");
    }

    /**
     * Alias for encoder tag of in xml configuration
     */
    public abstract String getEncoderName();

    /**
     * Class to use for encoder with filed name specified in encoderName
     */
    public abstract Class<? extends NotificationEncoder> getEncoderClass();

    /**
     * Alias for sender tag of in xml configuration
     */
    public abstract String getSenderName();

    /**
     * Class to use for sender with filed name specified in senderName
     */
    public abstract Class<? extends NotificationSender> getSenderClass();

}
