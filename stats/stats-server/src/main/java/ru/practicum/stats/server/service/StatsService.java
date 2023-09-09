package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.FieldValidationException;
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
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Transactional
    public EndpointHitDto saveEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(createEndpointHitDto);

        return endpointHitMapper.toEndpointHitDto(statsRepository.save(endpointHit));
    }

    public List<ViewStats> getStatistics(StatsDto statsDto) {
            if (statsDto.getStart().isAfter(statsDto.getEnd())) {
                throw new FieldValidationException("Start", "Start must be before RangeEnd");
            }


        return statsRepository.getStatisticsByUris(statsDto);
    }
}