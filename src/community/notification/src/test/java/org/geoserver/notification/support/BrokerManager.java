/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

import com.google.common.io.Files;

public class BrokerManager {
    private static final String INITIAL_CONFIG_PATH = "src/test/resources/qpid-config.json";

    private static final String PWD_PATH = "src/test/resources/passwd.properties";

    private static final String PORT = "4432";

    private final Broker broker = new Broker();

    public void startBroker() throws Exception {
        final BrokerOptions brokerOptions = new BrokerOptions();
        // final String configFileName = "qpid-config.json";
        // final String passwordFileName = "passwd.properties";
        // prepare options
        // final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", PORT);
        brokerOptions.setConfigProperty("qpid.pass_file", PWD_PATH);
        brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath());
        brokerOptions.setInitialConfigurationLocation(INITIAL_CONFIG_PATH);
        /*
         * brokerOptions.setConfigProperty("qpid.amqp_port", PORT); brokerOptions.setConfigProperty("broker.name", "GEOSERVER");
         * brokerOptions.setConfigurationStoreLocation(INITIAL_CONFIG_PATH); brokerOptions.setOverwriteConfigurationStore(false);
         * brokerOptions.setInitialConfigurationLocation(INITIAL_CONFIG_PATH);
         */

        broker.startup(brokerOptions);
    }

    public void stopBroker() {
        broker.shutdown();
    }
}
