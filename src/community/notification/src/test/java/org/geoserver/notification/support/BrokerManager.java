/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

public class BrokerManager {
    private static final String INITIAL_CONFIG_PATH = "src/test/resources/brokerconfig.json";
    private static final String PORT = "4432";
    private final Broker broker = new Broker();

    public void startBroker() throws Exception {
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", PORT);
        brokerOptions.setConfigProperty("broker.name", "GEOSERVER");
        brokerOptions.setConfigurationStoreLocation(INITIAL_CONFIG_PATH);
        brokerOptions.setOverwriteConfigurationStore(false);
        //brokerOptions.setInitialConfigurationLocation(INITIAL_CONFIG_PATH);

        broker.startup(brokerOptions);
    }

    public void stopBroker() {
        broker.shutdown();
    }
}
