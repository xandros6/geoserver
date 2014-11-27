package org.geoserver.wps.web;

import org.apache.wicket.Response;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;

public class RolesRenderer extends AbstractAutoCompleteRenderer<String> {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 3407675669346346083L;
    private StringBuilder selectedRoles;

    public RolesRenderer(StringBuilder selectedRoles) {
        this.selectedRoles = selectedRoles;
    }
    
    @Override
    protected void renderChoice(String object, Response response,
            String criteria) {
        response.write(object);

    }
    @Override
    protected String getTextValue(String object) {
        return selectedRoles.toString()+object;
    }     

};
