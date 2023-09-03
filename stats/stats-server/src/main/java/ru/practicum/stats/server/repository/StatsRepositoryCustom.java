package ru.practicum.stats.server.repository;

import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

public interface StatsRepositoryCustom {
    List<ViewStats> getStatisticsByUris(StatsDto statsDto);
}