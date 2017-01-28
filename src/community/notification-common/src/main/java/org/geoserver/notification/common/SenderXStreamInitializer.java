package org.geoserver.notification.common;

import org.geoserver.notification.common.sender.NotificationSender;

import com.thoughtworks.xstream.XStream;

public class SenderXStreamInitializer implements NotificationXStreamInitializer {

    /**
     * Alias for sender tag of in xml configuration
     */
    public String name;

    /**
     * Class to use for sender with filed 'name'
     */
    public Class<? extends NotificationSender> clazz;

    /**
     * Define an alias for the {@link DefaultNotificationProcessor#sender sender}<br>
     * Define a class for the {@link NotificationSender}<br>
     * An example of sender configuration section in notifier.xml is:
     * 
     * <pre>
     *  {@code
     *  <genericProcessor>
     *          <fanoutSender>
     *          ...
     *          </fanoutSender>
     *  </genericProcessor>
     *  }
     * </pre>
     * 
     * @param xs XStream object
     * 
     */
    public SenderXStreamInitializer(String name, Class<? extends NotificationSender> clazz) {
        super();
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public void init(XStream xs) {
        xs.alias(name, NotificationSender.class, clazz);
        xs.aliasField(name, DefaultNotificationProcessor.class, "sender");
    }
}
