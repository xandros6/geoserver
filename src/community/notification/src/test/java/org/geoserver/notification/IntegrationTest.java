/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTest {

    private static final String FIRST = "first";
    private static final String SECOND = "second";
    private static final String THIRD = "third";

    private static BrokerManager brokerStarter;

    @BeforeClass
    public static void startup() throws Exception {
        brokerStarter = new BrokerManager();
        brokerStarter.startBroker();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        brokerStarter.stopBroker();
    }

    private SimpleCache cache = new SimpleCache();

    @Test
    public void cacheShouldContainThreeEntries_afterThreeReceivedMessages() throws Exception {
        Sender sender = new Sender();
        sender.sendMessage(FIRST);
        sender.sendMessage(SECOND);
        sender.sendMessage(THIRD);
        new Receiver(cache).receive();

        Thread.sleep(500); // This, of course can and should be replaced with something smarter
        List<CacheEntry> cacheContent = cache.getContent();

        assertEquals(3, cacheContent.size());
        assertEquals(new CacheEntry(FIRST, 0), cacheContent.get(0));
        assertEquals(new CacheEntry(SECOND, 1), cacheContent.get(1));
        assertEquals(new CacheEntry(THIRD, 2), cacheContent.get(2));
    }

}
