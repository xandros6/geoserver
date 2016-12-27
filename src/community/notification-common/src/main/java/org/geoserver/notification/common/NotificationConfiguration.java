/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

import java.io.Serializable;
import java.util.List;

public class NotificationConfiguration implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 2029473095919663064L;

    private Long queueSize;

    private List<Notificator> notificators;

    public Long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Long queueSize) {
        this.queueSize = queueSize;
    }

    public List<Notificator> getNotificators() {
        return notificators;
    }

    public void setNotificators(List<Notificator> notificators) {
        this.notificators = notificators;
    }

}
