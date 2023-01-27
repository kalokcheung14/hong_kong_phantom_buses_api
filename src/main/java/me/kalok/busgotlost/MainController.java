package me.kalok.busgotlost;

import me.kalok.busgotlost.model.Coordinate;
import me.kalok.busgotlost.model.EtaResponse;
import me.kalok.busgotlost.service.BusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class MainController {
    private final Logger LOG = Logger.getLogger(String.valueOf(MainController.class));
    @Autowired
    private BusDataService busDataService;

    @GetMapping("/eta")
    public EtaResponse getEta(@RequestParam("latitude") double lat, @RequestParam("longitude") double lon) {
        LOG.info(String.format("Calculating nearest stop from coordinates %f, %f", lat, lon));
        return busDataService.getStopEtaResponse(new Coordinate(lat, lon));
    }
}
