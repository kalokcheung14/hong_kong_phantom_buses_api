package me.kalok.busgotlost.model;

import java.util.ArrayList;
import java.util.Objects;

public final class EtaBusRouteResponse {
    private final String updateTime;
    private final String route;
    private final String destEn;
    private final String destTc;
    private final String stopNameEn;
    private final String stopNameTc;
    private ArrayList<EtaDetailsResponse> etaDetails;

    public EtaBusRouteResponse(
            String route,
            String destEn,
            String destTc,
            ArrayList<EtaDetailsResponse> etaDetails,
            String updateTime,
            String stopNameEn,
            String stopNameTc
    ) {
        this.route = route;
        this.destEn = destEn;
        this.destTc = destTc;
        this.etaDetails = etaDetails;
        this.updateTime = updateTime;
        this.stopNameEn = stopNameEn;
        this.stopNameTc = stopNameTc;
    }

    public String getRoute() {
        return route;
    }

    public String getDestEn() {
        return destEn;
    }

    public String getDestTc() {
        return destTc;
    }

    public ArrayList<EtaDetailsResponse> getEtaDetails() {
        return etaDetails;
    }

    public void setEtaDetails(ArrayList<EtaDetailsResponse> etaDetails) {
        this.etaDetails = etaDetails;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getStopNameEn() {
        return stopNameEn;
    }

    public String getStopNameTc() {
        return stopNameTc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EtaBusRouteResponse) obj;
        return Objects.equals(this.route, that.route) &&
                Objects.equals(this.destEn, that.destEn) &&
                Objects.equals(this.destTc, that.destTc) &&
                Objects.equals(this.etaDetails, that.etaDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, destEn, destTc, etaDetails);
    }

    @Override
    public String toString() {
        return "EtaBusRouteResponse[" +
                "route=" + route + ", " +
                "destEn=" + destEn + ", " +
                "destTc=" + destTc + ", " +
                "etaDetails=" + etaDetails + ']';
    }

}
