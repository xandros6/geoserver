/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.status.monitoring.rest;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.rest.ObjectToMapWrapper;
import org.geoserver.rest.RestBaseController;
import org.geoserver.rest.converters.XStreamMessageConverter;
import org.geoserver.rest.wrapper.RestWrapper;
import org.geoserver.status.monitoring.collector.MetricValue;
import org.geoserver.status.monitoring.collector.Metrics;
import org.geoserver.status.monitoring.collector.SystemInfoCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thoughtworks.xstream.XStream;

import freemarker.template.ObjectWrapper;

/**
 * 
 * REST endpoint that return the available system information.
 * <p>
 * Every time this endpoint is hitted the informations are retrieved from the system, no cached information is used.
 * <p>
 * HTML, XML and JSON are supported.
 * 
 * @author sandr
 *
 */

@RestController
@RequestMapping(path = RestBaseController.ROOT_PATH + "/about/monitoring")
public class MonitorRest extends RestBaseController {

    private static Log log = LogFactory.getLog(MonitorRest.class);

    @Autowired
    SystemInfoCollector systemInfoCollector;

    @GetMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_HTML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public RestWrapper<Metrics> getData(HttpServletRequest request, HttpServletResponse response) {
        Metrics si = systemInfoCollector.retriveAllSystemInfo();
        return wrapObject(si, Metrics.class);
    }

    @Override
    public void configurePersister(XStreamPersister persister, XStreamMessageConverter converter) {
        XStream xs = persister.getXStream();
        xs.alias("metric", MetricValue.class);
        xs.alias("metrics", Metrics.class);
        xs.addImplicitCollection(Metrics.class, "metrics");
    }

    @Override
    protected <T> ObjectWrapper createObjectWrapper(Class<T> clazz) {
        return new ObjectToMapWrapper<>(clazz, Arrays.asList(MetricValue.class));
    }

}
