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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    private final int NUM_OF_NEAREST_STOPS = 3;

    @Override
    public List<EtaResponse> getStopEtaResponse(Coordinate coordinate) throws ParseException {
        // Get list of bus stops
        ListResult<Stop> stopList = getStopList();

        // Find the nearest stop to the provided coordinate
        // by calculating the distance between the coordinate and all the stops
        List<Stop> nearestStops = getNearestStops(coordinate, stopList, NUM_OF_NEAREST_STOPS);

        // Get ETA response list of stops
        return nearestStops.stream().map(this::getEtaResponse).toList();
    }

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

    /**
     * Encapsulate a ETA Response of a bus stop
     */
    private EtaResponse getEtaResponse(Stop nearestStop) {
        // Get a list of ETA by stop ID
        ListResult<StopEta> stopEtaList = getStopEta(nearestStop.getStopId());

        // Convert date string to LocalDateTime object
        // This is called "ISO format"
        LocalDateTime date = LocalDateTime.parse(
            stopEtaList.generatedTimestamp(),
            DateTimeFormatter.ISO_DATE_TIME
        );

        // Get a hashmap of EtaBusRouteResponse with routes as key
        HashMap<String, EtaBusRouteResponse> stopEtaMap = getStringEtaBusRouteResponseHashMap(stopEtaList, date);

        return new EtaResponse(
                // This format is used to display the generated time to the user
                DateTimeFormatter.ofPattern("HH:mm:ss").format(date),
                nearestStop.getNameEn(),
                nearestStop.getNameTc(),
                // Return the values of the map as a list
                stopEtaMap.values().stream().toList()
        );
    }

    /**
     * Get a hashmap of EtaBusRouteResponse with routes as key
     */
    private static HashMap<String, EtaBusRouteResponse> getStringEtaBusRouteResponseHashMap(ListResult<StopEta> stopEtaList, LocalDateTime date) {
        // Create a hashmap to group ETA of different routes
        HashMap<String, EtaBusRouteResponse> stopEtaMap = new HashMap<>();

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

            // Add ETA details item to corresponding list
            etaBusRouteResponse.getEtaDetails().add(new EtaDetailsResponse(
                getEtaInMinutes(date, stopEta),
                stopEta.rmkEn(),
                stopEta.rmkTc()
            ));
        }
        return stopEtaMap;
    }

    /**
     * Get ETA in minutes by calculating the difference between the provided date and the stop ETA date time
     */
    private static Long getEtaInMinutes(LocalDateTime date, StopEta stopEta) {
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
        return etaResult;
    }

    /**
     * Get the nth nearest stops from the provided location
     */
    private List<Stop> getNearestStops(Coordinate coordinate, ListResult<Stop> stopList, int numOfStops) {
        for (Stop stop: stopList.data()) {
            // Encapsulate coordinate of the bus stop
            Coordinate coordinate2 = new Coordinate(
                    Double.parseDouble(stop.getLatitude()),
                    Double.parseDouble(stop.getLongitude())
            );

            // Store the distance in the stop instance
            stop.setDistance(distanceUtil.calculateDistance(coordinate, coordinate2));
        }

        stopList.data().sort(Comparator.comparingDouble(Stop::getDistance));

        return stopList.data().subList(0, numOfStops);
    }
}
