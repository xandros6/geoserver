package org.geoserver.notification.common;

import com.thoughtworks.xstream.XStream;

public class EncoderXStreamInitializer implements NotificationXStreamInitializer {

    /**
     * Alias for encoder tag of in xml configuration
     */
    public String name;

    /**
     * Class to use for encoder with filed 'name'
     */
    public Class<? extends NotificationEncoder> clazz;

    /**
     * Define an alias for the {@link DefaultNotificationProcessor#encoder encoder}<br>
     * Define a class for the {@link NotificationEncoder}<br>
     * An example of encoder configuration section in notifier.xml is:
     * 
     * <pre>
     *  {@code
     *  <genericProcessor>
     *           <geonodeEncoder>
     *           ...
     *           </geonodeEncoder>
     * </genericProcessor>
     *  }
     * </pre>
     * 
     * @param xs XStream object
     * 
     */
    public EncoderXStreamInitializer(String name, Class<? extends NotificationEncoder> clazz) {
        super();
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public void init(XStream xs) {
        xs.alias(name, NotificationEncoder.class, clazz);
        xs.aliasField(name, DefaultNotificationProcessor.class, "encoder");
    }

}
