package com.vmware.rpm.tools.clientprofile.mapper;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vmware.rpm.tools.clientprofile.api.CsvStrategicObjective;
import com.vmware.rpm.tools.clientprofile.api.PillarsModelRandomizer;
import com.vmware.rpm.tools.clientprofile.api.StrategicObjectiveDescriptionApiRandomizer;
import com.vmware.rpm.tools.clientprofile.api.StrategicObjectiveModelRandomizer;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjective;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScore;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;

class CsvStrategicObjectiveMapperTest {


    private CsvStrategicObjectiveMapper mapper;
    private EasyRandom randomizer;
    private RecursiveComparisonConfiguration configuration;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CsvStrategicObjectiveMapper.class);

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("strategicObjective").and(inClass(CsvStrategicObjective.class)), new StrategicObjectiveDescriptionApiRandomizer())
                .randomize(named("pillar").and(inClass(StrategicObjectiveScore.class)), new PillarsModelRandomizer())
                .randomize(named("strategicObjective").and(inClass(StrategicObjectiveScore.class)), new StrategicObjectiveModelRandomizer());

        randomizer = new EasyRandom(parameters);

        configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("comments")
                .build();
    }

    @Test
    void shouldMapPillar(){
        var csvObjective = randomizer.nextObject(CsvStrategicObjective.class);
        var entity = mapper.toEntity(csvObjective);

        assertThat(entity.getPillar()).isNotNull();
        assertThat(entity.getPillar().name()).isEqualTo(StrategicObjective.valueOf(toCapSnakeCase(csvObjective.getStrategicObjective())).pillar().name());
    }

    @Test
    void shouldMapStrategicObjective(){
        var csvObjective = randomizer.nextObject(CsvStrategicObjective.class);
        var entity = mapper.toEntity(csvObjective);
        assertThat(entity.getStrategicObjective().name()).isEqualTo(toCapSnakeCase(csvObjective.getStrategicObjective()));
    }


    @Test
    void shouldMapCsvLinesToStrategicObjectives() throws Exception {

        final InputStream inputStream = loadClassPathResource().getInputStream();

        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);


        List<CsvStrategicObjective> objectives = new CsvToBeanBuilder(inputStreamReader)
                .withType(CsvStrategicObjective.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

        var entityObjectives = mapper.toEntity(Set.copyOf(objectives));

        assertThat(entityObjectives).hasSameSizeAs(objectives);
    }

    public Resource loadClassPathResource() {
        return new ClassPathResource("samples/strategic-objectives.csv", this.getClass().getClassLoader());
    }

    private String toCapSnakeCase(String description){
        return StringUtils.replace(description, " ", "_").toUpperCase(Locale.ROOT);
    }

}

