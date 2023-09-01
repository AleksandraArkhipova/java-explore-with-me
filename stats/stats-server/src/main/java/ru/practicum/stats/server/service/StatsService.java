package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.repository.StatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    public EndpointHitDto saveEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(createEndpointHitDto);

        return endpointHitMapper.toEndpointHitDto(statsRepository.save(endpointHit));
    }

    public List<ViewStats> getStatistics(StatsDto statsDto) {
        return statsRepository.getStatisticsByUris(statsDto);
    }
}