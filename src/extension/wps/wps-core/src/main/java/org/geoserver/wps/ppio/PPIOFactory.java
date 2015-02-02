package org.geoserver.wps.ppio;

import java.util.List;

public interface PPIOFactory {

    /**
     * Returns a list of process parameter IO. This method will be called every
     * time a PPIO is looked up , so implementors are required to implement
     * suitable caching if the creation of these objects is expensive
     */
    List<ProcessParameterIO> getProcessParameterIO();

}
