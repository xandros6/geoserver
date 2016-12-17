/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import net.opengis.wfs.TransactionResponseType;
import net.opengis.wfs.TransactionType;

import org.geoserver.wfs.TransactionEvent;
import org.geoserver.wfs.WFSException;

public class NotificationTransactionListener implements INotificationTransactionListener {

    private NotificationConfiguration notifierConfig;

    @Override
    public NotificationConfiguration getNotificationConfiguration() {
        return notifierConfig;
    }

    @Override
    public void setNotificationConfiguration(NotificationConfiguration ncfg) {
        this.notifierConfig = ncfg;
    }

    @Override
    public TransactionType beforeTransaction(TransactionType request) throws WFSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void beforeCommit(TransactionType request) throws WFSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTransaction(TransactionType request, TransactionResponseType result,
            boolean committed) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getPriority() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void dataStoreChange(TransactionEvent event) throws WFSException {
        // TODO Auto-generated method stub

    }

}
