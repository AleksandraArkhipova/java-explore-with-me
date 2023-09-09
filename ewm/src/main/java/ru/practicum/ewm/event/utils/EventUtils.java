package ru.practicum.ewm.event.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PUBLIC)
public class EventUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static StatsClient statsClient;
    static RequestRepository requestRepository;

    @Autowired
    public EventUtils(StatsClient client, RequestRepository requestRepo) {
        statsClient = client;
        requestRepository = requestRepo;
    }

    public static void addViewsAndConfirmedRequestsToEvents(
            List<EventDto> events
    ) {
        addViewsToEvents(events);
        addConfirmedRequests(events);
    }

    public static void addViewsToEvents(List<EventDto> events) {
        Map<String, EventDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(event -> "/events/" + event.getId(), event -> event));

        eventsMap.values().forEach(event -> event.setViews(0));

        Object rawStatistics = statsClient.getStatistics(
                LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                LocalDateTime.parse("5000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                new ArrayList<>(eventsMap.keySet()),
                false
        ).getBody();

        List<ViewStats> statistics = objectMapper.convertValue(rawStatistics, new TypeReference<>() {
        });

        statistics.forEach(statistic -> {
            if (eventsMap.containsKey(statistic.getUri())) {
                eventsMap.get(statistic.getUri()).setViews(0L);
                eventsMap.get(statistic.getUri()).setViews(statistic.getHits());
            }
        });
    }

    public static void addConfirmedRequests(List<EventDto> events) {
        Map<Long, Long> requestsCountMap = new HashMap<>();

        List<Request> requests = requestRepository.findAllConfirmedByEventIdIn(events
                .stream()
                .map(EventDto::getId)
                .collect(Collectors.toList())
        );

        requests.forEach(request -> {
            long eventId = request.getEvent().getId();

            if (!requestsCountMap.containsKey(eventId)) {
                requestsCountMap.put(eventId, 0L);
            }

            requestsCountMap.put(eventId, requestsCountMap.get(eventId) + 1);
        });

        events.forEach(event -> {
            if (requestsCountMap.containsKey(event.getId())) {
                event.setConfirmedRequests(requestsCountMap.get(event.getId()));
            }
        });
    }
}
