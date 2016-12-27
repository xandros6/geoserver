package org.geoserver.notification;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.geoserver.notification.common.Notification;
import org.geotools.filter.expression.PropertyAccessor;
import org.geotools.util.logging.Logging;

public class NotificationPropertyAccessor implements PropertyAccessor {

    private static final Logger LOGGER = Logging.getLogger(NotificationPropertyAccessor.class);

    @Override
    public boolean canHandle(Object object, String xpath, Class<?> target) {
        return object instanceof Notification;
    }

    @Override
    public <T> T get(Object object, String xpath, Class<T> target) throws IllegalArgumentException {
        T result = null;
        Notification notification = (Notification) object;
        try {
            result = (T) PropertyUtils.getProperty(notification, xpath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public <T> void set(Object object, String xpath, T value, Class<T> target)
            throws IllegalArgumentException {
        throw new UnsupportedOperationException();

    }

}
