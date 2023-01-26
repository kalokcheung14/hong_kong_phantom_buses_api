package me.kalok.busgotlost;

import me.kalok.busgotlost.model.ListResult;
import me.kalok.busgotlost.model.Stop;
import me.kalok.busgotlost.model.StopEta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
public class ApiConsumer {
    private final Logger LOG = Logger.getLogger(String.valueOf(ApiConsumer.class));
    @Autowired
    RestTemplate restTemplate;

    @Value("${api.url}")
    String apiUrl;

    @GetMapping("eta")
    public ListResult<Stop> getStopList() {
        return restTemplate.exchange(
                apiUrl + "v1/transport/kmb/stop",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ListResult<Stop>>() {
                }
        ).getBody();
    }

    @GetMapping("stopEta/{stopId}")
    public ListResult<StopEta> getStopEta(@PathVariable("stopId") String stopId) {
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
