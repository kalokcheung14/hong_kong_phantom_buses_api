package me.kalok.busgotlost.service;

import me.kalok.busgotlost.model.ListResult;
import me.kalok.busgotlost.model.Stop;
import me.kalok.busgotlost.model.StopEta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class ApiConsumerServiceImpl implements ApiConsumerService {
    private final Logger LOG = Logger.getLogger(String.valueOf(ApiConsumerServiceImpl.class));
    @Autowired
    RestTemplate restTemplate;

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
}
