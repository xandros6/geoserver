/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.logging.Level;

import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.event.CatalogAddEvent;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.CatalogPostModifyEvent;
import org.geoserver.catalog.event.CatalogRemoveEvent;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.notification.Notification.Action;
import org.geoserver.notification.Notification.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class NotificationCatalogListener extends NotificationListener implements
        INotificationCatalogListener {

    @Override
    public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
        LOGGER.log(Level.FINE, "Remove event");
    }

    @Override
    public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
        LOGGER.log(Level.FINE, "Modify event");
    }

    @Override
    public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
        LOGGER.log(Level.FINE, "Modify post event");
    }

    @Override
    public void reloaded() {
        LOGGER.log(Level.FINE, "Reload event");
    }

    @Override
    public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
        LOGGER.log(Level.FINE, "Add event");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : null;
        CatalogInfo info = ModificationProxy.unwrap(event.getSource());
        Notification notification = new NotificationImpl(Type.Catalog, event.getSource().getId(),
                Action.Add, info, null, user);
        notify(notification);
    }

    @Override
    public void setNotificationConfiguration(NotificationConfiguration ncfg) {
        this.notifierConfig = ncfg;
    }

    @Override
    public NotificationConfiguration getNotificationConfiguration() {
        return notifierConfig;
    }

}
