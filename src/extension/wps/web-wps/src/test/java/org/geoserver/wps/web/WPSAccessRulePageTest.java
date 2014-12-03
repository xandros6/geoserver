package org.geoserver.wps.web;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.value.ValueMap;
import org.geoserver.config.GeoServer;
import org.geoserver.security.CatalogMode;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.ProcessGroupInfoImpl;
import org.geoserver.wps.ProcessInfo;
import org.geoserver.wps.ProcessInfoImpl;
import org.geoserver.wps.WPSInfo;
import org.geotools.feature.NameImpl;
import org.geotools.process.ProcessFactory;
import org.geotools.process.Processors;
import org.junit.Test;

public class WPSAccessRulePageTest extends GeoServerWicketTestSupport {

    @Test
    public void testGroupToWpsLink() throws Exception {
        login();
        tester.startPage(new WPSAccessRulePage());
        tester.assertRenderedPage(WPSAccessRulePage.class);
        tester.clickLink("form:processFilterTable:listContainer:items:1:itemProperties:5:component:link", false);
        tester.assertRenderedPage(ProcessSelectionPage.class);
    }

    @Test
    public void testDisableWps() throws Exception {
        login();
        tester.startPage(new WPSAccessRulePage());
        tester.assertRenderedPage(WPSAccessRulePage.class);
        tester.clickLink("form:processFilterTable:listContainer:items:1:itemProperties:5:component:link", false);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("selectionTable:listContainer:items:1:itemProperties:0:component:enabled", "false");
        ft.submit("apply");
        GeoServerTablePanel<ProcessGroupInfo> processFilterTable = (GeoServerTablePanel<ProcessGroupInfo>) tester.getComponentFromLastRenderedPage("form:processFilterTable");
        ProcessFactoryInfoProvider dp = (ProcessFactoryInfoProvider)processFilterTable.getDataProvider();
        assertEquals(dp.getItems().get(0).getFilteredProcesses().size(),1);
    }

    @Test
    public void testCheckGroup() throws Exception {
        login();
        GeoServer gs = getGeoServer();
        WPSInfo wps = gs.getService(WPSInfo.class);

        // start the page
        tester.startPage(new WPSAccessRulePage());
        tester.assertRenderedPage(WPSAccessRulePage.class);

        tester.assertComponent("form:processFilterTable", GeoServerTablePanel.class);
        GeoServerTablePanel<ProcessGroupInfo> processFilterTable = (GeoServerTablePanel<ProcessGroupInfo>) tester.getComponentFromLastRenderedPage("form:processFilterTable");
        ProcessFactoryInfoProvider dp = (ProcessFactoryInfoProvider)processFilterTable.getDataProvider();
        for(ProcessGroupInfo pgi : dp.getItems()){
            assertEquals(pgi.isEnabled(),true);
        }

        FormTester ft = tester.newFormTester("form");
        ft.setValue("processFilterTable:listContainer:items:1:itemProperties:0:component:enabled", "false");
        ft.setValue("processFilterTable:listContainer:items:4:itemProperties:0:component:enabled", "false");
        ft.submit();
        assertEquals(dp.getItems().get(0).isEnabled(),false);
        assertEquals(dp.getItems().get(3).isEnabled(),false);


        /*
        ft.setValue("processFilterTable:listContainer:items:1:itemProperties:4:component:roles", "A");

        Component autocompleteRoles = tester.getComponentFromLastRenderedPage("form:processFilterTable:listContainer:items:1:itemProperties:4:component:roles");

        RolesAutoCompleteBehavior autocompleteBehavior = (RolesAutoCompleteBehavior) autocompleteRoles.getBehaviors().get(0);

        CharSequence url = autocompleteBehavior.getCallbackUrl(false);
        WebRequestCycle cycle = tester.setupRequestAndResponse(true);
        tester.getServletRequest().setRequestToRedirectString(url.toString());
        tester.processRequestCycle(cycle);


        //tester.executeBehavior(autocompleteBehavior);


        assertEquals(0, table.getSelection().size());


        // select just one
        FormTester ft = tester.newFormTester("form");
        ft.setValue("panel:listContainer:items:1:selectItemContainer:selectItem", "true");
        ft.setValue("panel:listContainer:items:7:selectItemContainer:selectItem", "true");
        ft.submit();
        assertEquals(2, table.getSelection().size());
        assertEquals(new Integer(0), table.getSelection().get(0));
        assertEquals(new Integer(6), table.getSelection().get(1));*/

    }


}
