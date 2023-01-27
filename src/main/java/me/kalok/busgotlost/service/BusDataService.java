package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;

public interface BusDataService {
    EtaResponse getStopEtaResponse(Coordinate coordinate);
}
