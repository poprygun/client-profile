package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.api.*;
import com.vmware.rpm.tools.clientprofile.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.WordRandomizer;
import org.jeasy.random.randomizers.number.LongRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Locale;

import static com.vmware.rpm.tools.clientprofile.model.Pillar.PERFORMANCE_EFFICIENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;


class BestPracticesMapperTest {

    private BestPracticesMapper mapper;
    private EasyRandom randomizer;
    private RecursiveComparisonConfiguration configuration;


    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BestPracticesMapper.class);

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("id"), new LongRandomizer())
                .randomize(named("pillar").and(inClass(BestPracticeDTO.class)), new PillarsRandomizer())
                .randomize(named("strategicObjective").and(inClass(BestPracticeDTO.class)), new StrategicObjectiveApiRandomizer())
                .randomize(named("pillar").and(inClass(BestPractices.class)), new PillarsModelRandomizer())
                .randomize(named("strategicObjective").and(inClass(BestPractices.class)), new StrategicObjectiveModelRandomizer())
                .randomize(named("name"), new WordRandomizer());

        randomizer = new EasyRandom(parameters);

        configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("model", "pillar", "strategicObjective")
                .build();
    }

    @Test
    void shouldConvertPillarCodesToWords() {
        Arrays.stream(Pillar.values()).forEach(pillar -> assertThat(PillarDefinition.values()).contains(PillarDefinition.fromValue(pillar.description())));
    }

    @Test
    void toDto_shouldMapPillarCodeBasedOnStrategicObjective() {
        var entity = randomizer.nextObject(BestPractices.class);
        var dto = mapper.toDto(entity);
        assertThat(dto.getPillar().name().toString()).isEqualTo(StrategicObjective.valueOf(entity.getStrategicObjective().name()).pillar().name());
    }

    @Test
    void toEntity_shouldMapPillarCodeBasedOnStrategicObjective() {
        var dto = randomizer.nextObject(BestPracticeDTO.class);
        var entity = mapper.toEntity(dto);

        assertThat(entity.getPillar()).isEqualTo(StrategicObjective.valueOf(dto.getStrategicObjective().name()).pillar());
    }

    @Test
    void toEntity_shouldMapRequiredFields() {

        var dto = randomizer.nextObject(BestPracticeDTO.class);
        var entity = mapper.toEntity(dto);

        assertThat(entity).usingRecursiveComparison(configuration).isEqualTo(dto);

    }

    @Test
    void toDto_shouldMapRequiredFields() {
        var entity = randomizer.nextObject(BestPractices.class);
        var dto = mapper.toDto(entity);

        assertThat(entity).usingRecursiveComparison(configuration).isEqualTo(dto);
    }

}