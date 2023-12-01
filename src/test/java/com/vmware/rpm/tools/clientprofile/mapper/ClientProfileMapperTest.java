package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.api.PillarsRandomizer;
import com.vmware.rpm.tools.clientprofile.api.PillarsStringRandomizer;
import com.vmware.rpm.tools.clientprofile.model.Profile;
import com.vmware.rpm.tools.clientprofile.model.ProfileDTO;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScore;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScoreDTO;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
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

class ClientProfileMapperTest {

    private ProfileMapper mapper;
    private EasyRandom randomizer;


    @BeforeEach
    void setUp(){
        mapper = Mappers.getMapper( ProfileMapper.class );

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("id"), new LongRandomizer())
                .randomize(named("pillar").and(inClass(StrategicObjectiveScoreDTO.class)), new PillarsRandomizer())
                .randomize(named("pillar").and(inClass(StrategicObjectiveScore.class)), new PillarsStringRandomizer())
                .randomize(named("name"), new WordRandomizer());

        randomizer = new EasyRandom(parameters);


    }

    @Test
    void toDto_shouldMapRequiredFields(){
        var profile = randomizer.nextObject(Profile.class);
        var profileDto = mapper.toDto(profile);

        assertThat(profileDto.getId()).isEqualTo(profile.getId());
        assertThat(profileDto.getName()).isEqualTo(profile.getName());
        assertThat(profileDto.getClientId()).isEqualTo(profile.getClient().getId());
    }

    @Test
    void toEntity_shouldMapRequiredFields(){
        var profileDTO = randomizer.nextObject(ProfileDTO.class);
        var profile = mapper.toEntity(profileDTO);

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("strategicObjectiveScores", "client", "clientId")
                .build();

        assertThat(profile).usingRecursiveComparison(configuration).isEqualTo(profileDTO);
    }
}