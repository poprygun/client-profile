package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.BestPractices;
import com.vmware.rpm.tools.clientprofile.model.PillarDefinition;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveDefinition;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CsvBestPracticeMapperTest {

    private CsvBestPracticeMapper mapper;
    private EasyRandom randomizer;

    @BeforeEach
    void setUp(){
        mapper = Mappers.getMapper(CsvBestPracticeMapper.class);
        randomizer = new EasyRandom();
    }

    @Test
    void shouldConvertEntityToCsvModel(){

        var bestPracticeEntity = randomizer.nextObject(BestPractices.class);

        var csvBestPractices = mapper.toDto(bestPracticeEntity);

        assertThat(csvBestPractices).usingRecursiveComparison()
                .ignoringFields("strategicObjective", "pillar", "model")
                .isEqualTo(bestPracticeEntity);

        assertThat(csvBestPractices.getModel())
                .isEqualTo(bestPracticeEntity.getModel().getName());

        assertThat(csvBestPractices.getPillar())
                .isIn(Arrays.stream(PillarDefinition.values()).map(v -> v.toString()).collect(Collectors.toList()));

        assertThat(csvBestPractices.getStrategicObjective())
                .isIn(Arrays.stream(StrategicObjectiveDefinition.values()).map(v -> v.toString()).collect(Collectors.toList()));

    }

}