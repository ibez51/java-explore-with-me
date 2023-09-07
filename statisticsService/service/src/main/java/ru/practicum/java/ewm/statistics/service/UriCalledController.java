package ru.practicum.java.ewm.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.java.ewm.statistics.dto.UriCalledDto;
import ru.practicum.java.ewm.statistics.dto.UriCalledStatisticDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class UriCalledController {
    @Autowired
    private UriCalledService uriCalledService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hitUri(@Valid @RequestBody UriCalledDto uriCalledDto) {
        uriCalledService.saveUriCall(uriCalledDto);
    }

    @GetMapping("/stats")
    public List<UriCalledStatisticDto> getStats(@Valid @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(name = "start") LocalDateTime start,
                                                @Valid @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(name = "end") LocalDateTime end,
                                                @Valid @RequestParam(name = "uris", required = false) List<String> urisList,
                                                @RequestParam(name = "unique", defaultValue = "false") boolean isUnique) {
        return uriCalledService.getStats(start, end, urisList, isUnique);
    }
}
