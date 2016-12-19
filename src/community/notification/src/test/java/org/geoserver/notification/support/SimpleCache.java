/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleCache {
    private List<CacheEntry> cache = new CopyOnWriteArrayList<CacheEntry>();

    private AtomicInteger counter = new AtomicInteger(0);

    public void update(String text) {
        cache.add(new CacheEntry(text, counter.getAndIncrement()));
    }

    public List<CacheEntry> getContent() {
        return Collections.unmodifiableList(cache);
    }
}
