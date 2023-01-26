package me.kalok.busgotlost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class ApiConsumer {
    @Autowired
    RestTemplate restTemplate;

    @Value("${api.url}")
    String apiUrl;

    @GetMapping("eta")
    public Object getEta() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
            apiUrl + "v1/transport/kmb/stop",
                HttpMethod.GET,
                entity,
                Object.class
        ).getBody();
    }
}
