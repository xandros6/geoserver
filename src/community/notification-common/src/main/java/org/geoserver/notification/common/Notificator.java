/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.common;

import java.io.Serializable;

public class Notificator implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 6185508068154638658L;

    private Long queueSize;

    private Long processorThreads;

    private String messageFilter;

    private NotificationProcessor genericProcessor;

    public Long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Long queueSize) {
        this.queueSize = queueSize;
    }

    public Long getProcessorThreads() {
        return processorThreads;
    }

    public void setProcessorThreads(Long processorThreads) {
        this.processorThreads = processorThreads;
    }

    public String getMessageFilter() {
        return messageFilter;
    }

    public void setMessageFilter(String messageFilter) {
        this.messageFilter = messageFilter;
    }

    public NotificationProcessor getGenericProcessor() {
        return genericProcessor;
    }

    public void setGenericProcessor(NotificationProcessor genericProcessor) {
        this.genericProcessor = genericProcessor;
    }

}
