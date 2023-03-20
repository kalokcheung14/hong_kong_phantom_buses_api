package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;

import java.text.ParseException;
import java.util.List;

public interface BusDataService {
    EtaResponse getStopEtaResponse(Coordinate coordinate) throws ParseException;
}
