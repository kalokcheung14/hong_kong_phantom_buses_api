package me.kalok.busgotlost.model;

import java.util.List;

public record EtaBusRouteResponse(
        String route,
        List<EtaDetailsResponse> etaDetails
) {
}
