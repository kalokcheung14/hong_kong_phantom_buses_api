package me.kalok.busgotlost;

import me.kalok.busgotlost.exception.InvalidCoordinateException;
import me.kalok.busgotlost.model.Coordinate;
import me.kalok.busgotlost.model.EtaResponse;
import me.kalok.busgotlost.service.BusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class MainController {
    private final Logger LOG = Logger.getLogger(String.valueOf(MainController.class));
    @Autowired
    private BusDataService busDataService;

    @GetMapping("/eta")
    public EtaResponse getEta(@RequestParam("coordinate") String coordinate) throws ParseException {
        // Split input coordinates by comer
        String[] coords = coordinate.split(",");
        // Convert to double
        double lat;
        double lon;
        // Throw InvalidCoordinateException if exception occurred when parsing the coordinate to double
        try {
            lat = Double.parseDouble(coords[0]);
            lon = Double.parseDouble(coords[1]);
        } catch (Exception e) {
            throw new InvalidCoordinateException();
        }
        LOG.info(String.format("Calculating nearest stop from coordinates %f, %f", lat, lon));
        return busDataService.getStopEtaResponse(new Coordinate(lat, lon));
    }
}
