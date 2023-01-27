package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;
import me.kalok.busgotlost.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class BusDataServiceImpl implements BusDataService {
    private final Logger LOG = Logger.getLogger(String.valueOf(BusDataServiceImpl.class));
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DistanceUtil distanceUtil;

    @Value("${api.url}")
    String apiUrl;

    public ListResult<Stop> getStopList() {
        return restTemplate.exchange(
                apiUrl + "v1/transport/kmb/stop",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ListResult<Stop>>() {
                }
        ).getBody();
    }

    public ListResult<StopEta> getStopEta(String stopId) {
        LOG.info("Calling " + apiUrl + "v1/transport/kmb/stop-eta/" + stopId);
        return restTemplate.exchange(
                apiUrl + "v1/transport/kmb/stop-eta/" + stopId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ListResult<StopEta>>() {
                }
        ).getBody();
    }

    @Override
    public EtaResponse getStopEtaResponse(Coordinate coordinate) {
        ListResult<Stop> stopList = getStopList();

        double shortestDistance = -1;
        Stop nearestStop = null;
        for (Stop stop: stopList.data()) {
            Coordinate coordinate2 = new Coordinate(
                    Double.parseDouble(stop.latitude()),
                    Double.parseDouble(stop.longitude())
            );
            double distance = distanceUtil.calculateDistance(coordinate, coordinate2);

            if (shortestDistance == -1 || distance < shortestDistance) {
                shortestDistance = distance;
                nearestStop = stop;
            }
        }

        assert nearestStop != null;
        return new EtaResponse(nearestStop.nameEn(), nearestStop.nameTc(), null);
    }
}
