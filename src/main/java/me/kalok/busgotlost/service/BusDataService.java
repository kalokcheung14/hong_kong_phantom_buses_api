package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;

public interface BusDataService {
    ListResult<Stop> getStopList();
    ListResult<StopEta> getStopEta(String stopId);
    EtaResponse getStopEtaResponse(Coordinate coordinate);
}
