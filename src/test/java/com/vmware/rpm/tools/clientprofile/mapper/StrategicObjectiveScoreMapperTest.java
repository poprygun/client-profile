package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.api.*;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjective;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScore;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScoreDTO;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;


class StrategicObjectiveScoreMapperTest {

    private StrategicObjectiveScoreMapper mapper;
    private EasyRandom randomizer;
    private RecursiveComparisonConfiguration configuration;


    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StrategicObjectiveScoreMapper.class);

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("pillar").and(inClass(StrategicObjectiveScoreDTO.class)), new PillarsRandomizer())
                .randomize(named("strategicObjective").and(inClass(StrategicObjectiveScoreDTO.class)), new StrategicObjectiveApiRandomizer())
                .randomize(named("pillar").and(inClass(StrategicObjectiveScore.class)), new PillarsModelRandomizer())
                .randomize(named("strategicObjective").and(inClass(StrategicObjectiveScore.class)), new StrategicObjectiveModelRandomizer());

        randomizer = new EasyRandom(parameters);

        configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("pillar", "scoreDate", "profileId", "strategicObjective")
                .build();
    }

    @Test
    void toEntities_shouldMapPillarFromStrategicObjective() {
        final Set<StrategicObjectiveScoreDTO> scoreDTOS = randomizer.objects(StrategicObjectiveScoreDTO.class, 13).collect(Collectors.toSet());

        scoreDTOS.forEach(so -> so.setPillar(null));

        var strategicObjectiveScores = mapper.toEntity(scoreDTOS);

        strategicObjectiveScores.forEach(strategicObjectiveScore -> {
            assertThat(strategicObjectiveScore.getPillar()).isNotNull();
            assertThat(strategicObjectiveScore.getPillar()).isEqualTo(strategicObjectiveScore.getStrategicObjective().pillar());
        });
    }

    @Test
    void toEntity_shouldMapPillarFromStrategicObjective() {
        var strategicObjectiveScoreDto
                = randomizer.nextObject(StrategicObjectiveScoreDTO.class);

        strategicObjectiveScoreDto.setPillar(null);
        final StrategicObjectiveScore strategicObjectiveScore = mapper.toEntity(strategicObjectiveScoreDto);
        assertThat(strategicObjectiveScore.getPillar()).isNotNull();
        assertThat(strategicObjectiveScore.getPillar()).isEqualTo(strategicObjectiveScore.getStrategicObjective().pillar());
        assertThat(strategicObjectiveScore.getComments()).isEqualTo(strategicObjectiveScore.getComments());

    }

    @Test
    void toDto_shouldMapRequiredFields() {
        var strategicObjectiveScore = randomizer.nextObject(StrategicObjectiveScore.class);
        var strategicObjectiveScoreDto = mapper.toDto(strategicObjectiveScore);

        assertThat(strategicObjectiveScoreDto).usingRecursiveComparison(configuration).isEqualTo(strategicObjectiveScore);

        assertThat(strategicObjectiveScoreDto.getProfileId()).isEqualTo(strategicObjectiveScore.getProfile().getId());
        assertThat(strategicObjectiveScoreDto.getScoreDate().toInstant()).isEqualTo(strategicObjectiveScore.getScoreDate());
        assertThat(strategicObjectiveScoreDto.getPillar().name()).isEqualTo(strategicObjectiveScore.getPillar().name());
        assertThat(strategicObjectiveScoreDto.getStrategicObjective().name()).isEqualTo(strategicObjectiveScore.getStrategicObjective().name());
        assertThat(strategicObjectiveScoreDto.getComments()).isEqualTo(strategicObjectiveScore.getComments());
    }

    @Test
    void toEntity_shouldMapRequiredFields() {
        var strategicObjectiveScoreDTO = randomizer.nextObject(StrategicObjectiveScoreDTO.class);
        strategicObjectiveScoreDTO.setPillar(null);

        var strategicObjectiveScore = mapper.toEntity(strategicObjectiveScoreDTO);

        assertThat(strategicObjectiveScore).usingRecursiveComparison(configuration).isEqualTo(strategicObjectiveScore);

        assertThat(strategicObjectiveScore.getScoreDate()).isEqualTo(strategicObjectiveScoreDTO.getScoreDate().toInstant());

        var expectedPillarFromStrategicObjective = StrategicObjective.valueOf(strategicObjectiveScoreDTO.getStrategicObjective().name()).pillar();

        assertThat(strategicObjectiveScore.getPillar()).isEqualTo(expectedPillarFromStrategicObjective);
        assertThat(strategicObjectiveScore.getStrategicObjective().name()).isEqualTo(strategicObjectiveScoreDTO.getStrategicObjective().name());
    }
}