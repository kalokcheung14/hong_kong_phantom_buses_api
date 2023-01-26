package me.kalok.busgotlost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class BusGotLostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusGotLostApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

		// Add the Jackson Message Converter to map JSON object
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		// Set the type of response to process
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		// Add the Jackson Message Converter as Message converter to Rest Template
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
}
