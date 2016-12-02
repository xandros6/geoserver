/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.event.CatalogAddEvent;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.CatalogPostModifyEvent;
import org.geoserver.catalog.event.CatalogRemoveEvent;

public class NotificationCatalogListener implements INotificationCatalogListener{

    @Override
    public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reloaded() {
        // TODO Auto-generated method stub
        
    }

}
