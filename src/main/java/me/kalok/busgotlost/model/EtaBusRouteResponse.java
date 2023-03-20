package me.kalok.busgotlost.model;

import java.util.ArrayList;

public record EtaBusRouteResponse (
    String updateTime,
    String route,
    String destEn,
    String destTc,
    String stopNameEn,
    String stopNameTc,
    ArrayList<EtaDetailsResponse> etaDetails
){
    EtaBusRouteResponse withEtaDetails(ArrayList<EtaDetailsResponse> etaDetails) {
        return new EtaBusRouteResponse(
            updateTime,
            route,
            destEn,
            destTc,
            stopNameEn,
            stopNameTc,
            etaDetails
        );
    }
}
