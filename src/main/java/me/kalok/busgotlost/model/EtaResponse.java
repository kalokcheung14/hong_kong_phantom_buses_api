package me.kalok.busgotlost.model;

import java.util.List;

public record EtaResponse(
        List<EtaBusRouteResponse> busRoutes
) {
}
