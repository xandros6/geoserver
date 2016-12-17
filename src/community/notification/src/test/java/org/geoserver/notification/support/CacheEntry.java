/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import java.io.Serializable;
import java.util.Arrays;

public class CacheEntry implements Serializable {

    private static final long serialVersionUID = 7286026533592311855L;

    private String text;

    private int sequenceNr;

    public CacheEntry(String text, int sequenceNr) {
        this.text = text;
        this.sequenceNr = sequenceNr;
    }

    public String getText() {
        return text;
    }

    public int getSequenceNr() {
        return sequenceNr;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.text, this.sequenceNr });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CacheEntry) {
            CacheEntry thisObj = (CacheEntry) obj;
            return (thisObj.sequenceNr == this.sequenceNr && thisObj.text.equals(this.text));
        } else {
            return false;
        }
    }
}
