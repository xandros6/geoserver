package org.geoserver.notification;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.util.logging.Logging;

public class NotificationListener {

    protected static Logger LOGGER = Logging.getLogger(NotificationListener.class);

    protected NotificationConfiguration notifierConfig;

    protected void notify(Notification notification) {
        for (Notificator notificator : this.notifierConfig.getNotificators()) {
            try {
                notificator.getGenericProcessor().process(notification);
            } catch (Exception e) {
                LOGGER.log(Level.FINER, e.getMessage(), e);
            }
        }
    }

}
