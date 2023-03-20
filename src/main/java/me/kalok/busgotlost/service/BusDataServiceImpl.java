package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.*;
import me.kalok.busgotlost.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Value("${api.numOfStopsToDisplay}")
    Integer numberOfStopsToDisplay;

    @Override
    public EtaResponse getStopEtaResponse(Coordinate coordinate) {
        // Get list of bus stops
        ListResult<Stop> stopList = getStopList();

        // Find the nearest stop to the provided coordinate
        // by calculating the distance between the coordinate and all the stops
        List<Stop> nearestStops = getNearestStops(coordinate, stopList, numberOfStopsToDisplay);

        // Get ETA response list of stops
        return new EtaResponse(
                nearestStops.stream()
                        .map(this::getEtaResponse)
                        .flatMap(List::stream)
                        .toList()
        );
    }

    /**
     * Get the full list of bus stops
     */
    private ListResult<Stop> getStopList() {
        String requestUrl = apiUrl + "v1/transport/kmb/stop";
        LOG.info("Calling " + requestUrl);
        return restTemplate.exchange(
            requestUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ListResult<Stop>>() {}
        ).getBody();
    }

    /**
     * Get a list of ETA by passing the stop ID String
     */
    private ListResult<StopEta> getStopEta(String stopId) {
        String requestUrl = apiUrl + "v1/transport/kmb/stop-eta/" + stopId;
        LOG.info("Calling " + requestUrl);
        return restTemplate.exchange(
            requestUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ListResult<StopEta>>() {}
        ).getBody();
    }

    /**
     * Encapsulate an ETA Response of a bus stop
     */
    private List<EtaBusRouteResponse> getEtaResponse(Stop nearestStop) {
        // Get a list of ETA by stop ID
        ListResult<StopEta> stopEtaList = getStopEta(nearestStop.getStopId());

        // Convert date string to LocalDateTime object
        // This is called "ISO format"
        LocalDateTime date = LocalDateTime.parse(
            stopEtaList.generatedTimestamp(),
            DateTimeFormatter.ISO_DATE_TIME
        );

        // Get a list of EtaBusRouteResponse
        return getStringEtaBusRouteResponseList(stopEtaList, date, nearestStop);
    }

    /**
     * Get a list of EtaBusRouteResponse with routes as key
     */
    private static List<EtaBusRouteResponse> getStringEtaBusRouteResponseList(
            ListResult<StopEta> stopEtaList,
            LocalDateTime date,
            Stop stop
    ) {
        // This format is used to display the generated time to the user
        String updateTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(date);

        // Create a hashmap to group ETA of different routes
        HashMap<String, EtaBusRouteResponse> stopEtaMap = new HashMap<>();

        // Loop through all the stop ETA items in which their eta are not null and group them by routes
        List<StopEta> stopEtas = stopEtaList.data()
                .stream()
                .filter(stopEta -> stopEta.eta() != null)
                .toList();

        for (StopEta stopEta: stopEtas) {
            EtaBusRouteResponse etaBusRouteResponse;
            String route = stopEta.route();
            // Store ETA items of the same route in the same EtaBusRouteResponse
            if (!stopEtaMap.containsKey(route)) {
                // If the key route is not in the map
                // create a new EtaBusRouteResponse instance
                etaBusRouteResponse = new EtaBusRouteResponse(
                        updateTime,
                        route,
                        stopEta.destEn(),
                        stopEta.destTc(),
                        stop.getNameEn(),
                        stop.getNameTc(),
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
            if (etaBusRouteResponse.etaDetails().size() < 3) {
                etaBusRouteResponse.etaDetails().add(new EtaDetailsResponse(
                        getEtaInMinutes(date, stopEta),
                        stopEta.rmkEn(),
                        stopEta.rmkTc()
                ));
            }
        }
        return stopEtaMap.values().stream().toList();
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
