package ru.practicum.java.ewm.statistics.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.java.ewm.statistics.dto.UriCalledDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ServiceHTTPClient {
    private final RestTemplate restTemplate;
    private static final String STATISTICS_SERVICE_URL = "http://localhost:9090";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public ServiceHTTPClient(@Value(STATISTICS_SERVICE_URL) String serverUrl,
                             RestTemplateBuilder builder) {
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> postHitUri(UriCalledDto body) {
        HttpEntity<UriCalledDto> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> serviceResponse;

        try {
            serviceResponse = restTemplate.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareGatewayResponse(serviceResponse);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uriList,
                                                boolean isUnique) {
        String uriTemplate = (Objects.isNull(uriList)) ?
                "/stats?start={start}&end={end}&unique={unique}" :
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        parameters.put("end", end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        parameters.put("unique", isUnique);

        if (Objects.nonNull(uriList)) {
            parameters.put("uris", uriList);
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Object> serviceResponse;

        try {
            serviceResponse = restTemplate.exchange(uriTemplate, HttpMethod.GET, requestEntity, Object.class, parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareGatewayResponse(serviceResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
