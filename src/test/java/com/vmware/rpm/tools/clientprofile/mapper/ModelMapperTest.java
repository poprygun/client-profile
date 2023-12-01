package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.ModelDTO;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.WordRandomizer;
import org.jeasy.random.randomizers.number.LongRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;

class ModelMapperTest {

    private ModelMapper mapper;
    private EasyRandom randomizer;

    @BeforeEach
    void setUp(){
        mapper = Mappers.getMapper( ModelMapper.class );

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("id"), new LongRandomizer())
                .randomize(named("name").and(inClass(ModelDTO.class)), new WordRandomizer());

        randomizer = new EasyRandom(parameters);

    }

    @Test
    void toEntity_shouldPopulateEntity(){
        var modelDTO = randomizer.nextObject(ModelDTO.class);
        final Model entity = mapper.toEntity(modelDTO);

        assertThat(entity).usingRecursiveComparison()
                .ignoringFields("bestPractices")
                .isEqualTo(modelDTO);
    }

    @Test
    void toDto_shouldMapRequiredFields(){
        var modelDTO = randomizer.nextObject(ModelDTO.class);
        var model = mapper.toEntity(modelDTO);

        assertThat(model).usingRecursiveComparison()
                .ignoringFields("bestPractices")
                .isEqualTo(modelDTO);
    }
}