package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;
import me.kalok.busgotlost.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Get the full list of bus stops
     */
    private ListResult<Stop> getStopList() {
        return restTemplate.exchange(
            apiUrl + "v1/transport/kmb/stop",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ListResult<Stop>>() {}
        ).getBody();
    }

    /**
     * Get a list of ETA by passing the stop ID String
     */
    private ListResult<StopEta> getStopEta(String stopId) {
        LOG.info("Calling " + apiUrl + "v1/transport/kmb/stop-eta/" + stopId);
        return restTemplate.exchange(
            apiUrl + "v1/transport/kmb/stop-eta/" + stopId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ListResult<StopEta>>() {}
        ).getBody();
    }

    @Override
    public EtaResponse getStopEtaResponse(Coordinate coordinate) throws ParseException {
        // Get list of bus stops
        ListResult<Stop> stopList = getStopList();

        // Find the nearest stop to the provided coordinate
        // by calculating the distance between the coordinate and all the stops
        double shortestDistance = -1;
        Stop nearestStop = null;
        for (Stop stop: stopList.data()) {
            // Encapsulate coordinate of the bus stop
            Coordinate coordinate2 = new Coordinate(
                Double.parseDouble(stop.latitude()),
                Double.parseDouble(stop.longitude())
            );

            // Calculate the distance between the input location and the bus stop
            double distance = distanceUtil.calculateDistance(coordinate, coordinate2);

            // If shortest distance is still the initial value (-1) or greater than the calculated distance
            // assign the newly calculated value to the shortest distance
            // and assign the corresponding stop as nearestStop
            if (shortestDistance == -1 || distance < shortestDistance) {
                shortestDistance = distance;
                nearestStop = stop;
            }
        }

        assert nearestStop != null;

        // Get a list of ETA by stop ID
        ListResult<StopEta> stopEtaList = getStopEta(nearestStop.stopId());
        // Create a hashmap to group ETA of different routes
        HashMap<String, EtaBusRouteResponse> stopEtaMap = new HashMap<>();

        // Convert date string to LocalDateTime object
        // This is called "ISO format"
        LocalDateTime date = LocalDateTime.parse(
            stopEtaList.generatedTimestamp(),
            DateTimeFormatter.ISO_DATE_TIME
        );

        // Loop through all the stop ETA items and group them by routes
        for (StopEta stopEta: stopEtaList.data()) {
            EtaBusRouteResponse etaBusRouteResponse;
            String route = stopEta.route();
            // Store ETA items of the same route in the same EtaBusRouteResponse
            if (!stopEtaMap.containsKey(route)) {
                // If the key route is not in the map
                // create a new EtaBusRouteResponse instance
                etaBusRouteResponse = new EtaBusRouteResponse(
                        route,
                        stopEta.destEn(),
                        stopEta.destTc(),
                        new ArrayList<>()
                );
                // Put the newly created EtaBusRouteResponse into the map
                stopEtaMap.put(route, etaBusRouteResponse);
            } else {
                // If the EtaBusRouteResponse instance is already in the map for this route
                // get it from the map
                etaBusRouteResponse = stopEtaMap.get(route);
            }

            Long etaResult = null;

            if (stopEta.eta() != null) {
                // Calculate ETA in minutes if it is not null
                // by comparing the difference between the generated timestamp of the API call
                // and the ETA timestamp
                etaResult =
                    Duration.between(
                        date,
                        // Parse stop ETA time by ISO date time format
                        LocalDateTime.parse(
                            stopEta.eta(),
                            DateTimeFormatter.ISO_DATE_TIME
                        )
                    ).toMinutes();
            }

            // Add ETA details item to corresponding list
            etaBusRouteResponse.getEtaDetails().add(new EtaDetailsResponse(
                etaResult,
                stopEta.rmkEn(),
                stopEta.rmkTc()
            ));
        }

        return new EtaResponse(
            // This format is used to display the generated time to the user
            DateTimeFormatter.ofPattern("HH:mm:ss").format(date),
            nearestStop.nameEn(),
            nearestStop.nameTc(),
            // Return the values of the map as a list
            stopEtaMap.values().stream().toList()
        );
    }
}
