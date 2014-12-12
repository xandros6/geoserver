/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.NameImpl;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Manages unmarshalling of {@link ProcessGroupInfoImpl} taking into account
 * previous wps.xml format in witch {@link ProcessGroupInfoImpl #getFilteredProcesses()} is a
 * collection of {@link NameImpl}
 * 
 */
public class WPSXStreamBackConverter extends ReflectionConverter{

    public WPSXStreamBackConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    @Override
    public boolean canConvert(Class clazz) {
        return ProcessGroupInfoImpl.class == clazz;
    }

    @Override
    public Object doUnmarshal(Object result, HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        ProcessGroupInfo converted = (ProcessGroupInfo)super.doUnmarshal(result, reader, context);

        if(converted.getFilteredProcesses() != null){
            List<ProcessInfo> newFilteredProcesses = new ArrayList<ProcessInfo>();
            for(Object fp:converted.getFilteredProcesses()){
                if(fp instanceof NameImpl){
                    NameImpl ni = (NameImpl)fp;
                    ProcessInfo pi = new ProcessInfoImpl();
                    pi.setName(ni);
                    pi.setEnabled(false);
                    newFilteredProcesses.add(pi);
                }else{
                    break;
                }
            }
            if(!newFilteredProcesses.isEmpty()){
                converted.getFilteredProcesses().clear();
                converted.getFilteredProcesses().addAll(newFilteredProcesses);
            }
        }
        
        return converted;
    }

}
