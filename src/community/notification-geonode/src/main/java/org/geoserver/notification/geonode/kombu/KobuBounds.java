package org.geoserver.notification.geonode.kombu;

import org.geotools.geometry.jts.ReferencedEnvelope;

public class KobuBounds {

    private Double minx;

    private Double miny;

    private Double maxx;

    private Double maxy;

    private String crs;

    public KobuBounds() {}
    
    public KobuBounds(ReferencedEnvelope nativeBoundingBox) {
        this.minx = nativeBoundingBox.getMinX();
        this.maxx = nativeBoundingBox.getMaxX();
        this.miny = nativeBoundingBox.getMinY();
        this.maxy = nativeBoundingBox.getMaxY();
        this.crs = nativeBoundingBox.getCoordinateReferenceSystem().getName().toString();
    }

    public Double getMinx() {
        return minx;
    }

    public void setMinx(Double minx) {
        this.minx = minx;
    }

    public Double getMiny() {
        return miny;
    }

    public void setMiny(Double miny) {
        this.miny = miny;
    }

    public Double getMaxx() {
        return maxx;
    }

    public void setMaxx(Double maxx) {
        this.maxx = maxx;
    }

    public Double getMaxy() {
        return maxy;
    }

    public void setMaxy(Double maxy) {
        this.maxy = maxy;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

}
