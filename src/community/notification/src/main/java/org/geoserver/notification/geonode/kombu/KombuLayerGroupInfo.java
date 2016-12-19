package org.geoserver.notification.geonode.kombu;

import java.util.List;

public class KombuLayerGroupInfo extends KombuPublishedInfo {
    String mode;

    String rootLayer;

    String rootLayerStyle;

    List<KombuLayerSimpleInfo> layers;
}
