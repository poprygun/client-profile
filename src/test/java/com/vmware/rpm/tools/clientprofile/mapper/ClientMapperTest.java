package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.Client;
import com.vmware.rpm.tools.clientprofile.model.ClientDTO;
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

class ClientMapperTest {

    private ClientMapper mapper;
    private EasyRandom randomizer;

    @BeforeEach
    void setUp(){
        mapper = Mappers.getMapper( ClientMapper.class );

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("id"), new LongRandomizer())
                .randomize(named("name").and(inClass(ClientDTO.class)), new WordRandomizer());

        randomizer = new EasyRandom(parameters);

    }

    @Test
    void toEntity_shouldPopulateEntity(){
        var clientDTO = randomizer.nextObject(ClientDTO.class);
        final Client entity = mapper.toEntity(clientDTO);

        assertThat(entity).usingRecursiveComparison().isEqualTo(clientDTO);
    }

    @Test
    void toDto_shouldMapRequiredFields(){
        var clientDTO = randomizer.nextObject(ClientDTO.class);
        var client = mapper.toEntity(clientDTO);

        assertThat(client).usingRecursiveComparison().isEqualTo(clientDTO);
    }
}