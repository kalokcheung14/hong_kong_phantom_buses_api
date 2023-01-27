package me.kalok.busgotlost.model;

import java.util.List;

public record EtaResponse(
        String stopNameEn,
        String stopNameTc,
        List<EtaBusRouteResponse> busRoutes
) {
}
