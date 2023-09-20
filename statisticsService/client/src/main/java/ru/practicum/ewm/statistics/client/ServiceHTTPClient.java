package ru.practicum.ewm.statistics.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ServiceHTTPClient {
    private final RestTemplate restTemplate;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public ServiceHTTPClient(@Value("${service.url}") String serverUrl,
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

        return serviceResponse;
    }

    public ResponseEntity<List<UriCalledStatisticDto>> getStatistics(LocalDateTime start,
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
            parameters.put("uris", String.join(",", uriList));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<List<UriCalledStatisticDto>> serviceResponse;

        try {
            serviceResponse = restTemplate.exchange(uriTemplate,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    },
                    parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }

        return serviceResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
