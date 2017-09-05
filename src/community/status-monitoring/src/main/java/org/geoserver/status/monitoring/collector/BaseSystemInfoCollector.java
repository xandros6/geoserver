/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.status.monitoring.collector;

import java.util.Collections;
import java.util.List;

/**
 * 
 * Create an empty system information metrics for all element defined in {@link MetricInfo} 
 * <p>
 * As default all elements are initialized as not available
 * 
 * @author sandr
 *
 */
public class BaseSystemInfoCollector implements SystemInfoCollector {

    public static String DEFAULT_VALUE = "NOT AVAILABLE";

    public final Metrics retriveAllSystemInfo() {
        Metrics metrics = new Metrics();
        for (MetricInfo sip : MetricInfo.values()) {
            metrics.getMetrics().addAll(retriveSystemInfo(sip));
        }
        return metrics;
    }

    /**
     * Retrieve one or more metric for each element defined in {@link MetricInfo} 
     * @param info the element to retrieve
     * @return a list of {@link MetricValue} for each {@link MetricInfo}
     */
    List<MetricValue> retriveSystemInfo(MetricInfo info) {
        MetricValue mv = new MetricValue(info);
        mv.setAvailable(false);
        mv.setValue(DEFAULT_VALUE);
        return Collections.singletonList(mv);
    }

}
