/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.config.GeoServer;
import org.geoserver.platform.GeoServerExtensions;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

public class Notifier implements ApplicationListener<ApplicationEvent>{

    private NotifierConfig config;

    protected Catalog catalog;

    private GeoServer server;  

    private List<INotificationCatalogListener> catalogListeners = new ArrayList<INotificationCatalogListener>();

    public Notifier(NotifierConfig config) {
        this.config = config;
    }

    public NotifierConfig getConfig() {
        return config;
    }

    public void setServer(GeoServer server) {
        this.server = server;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextRefreshedEvent) {
            catalogListeners = GeoServerExtensions.extensions(INotificationCatalogListener.class);
            for(CatalogListener cl : catalogListeners){
                this.server.getCatalog().addListener(cl);
            }
        }
        if(event instanceof ContextClosedEvent) {
            this.server.getCatalog().removeListeners(INotificationCatalogListener.class);
        }
    }




}
