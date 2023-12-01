package com.vmware.rpm.tools.clientprofile.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vmware.rpm.tools.clientprofile.model.*;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfilesApiImplTest {

    private MockMvc mvc;

    private JacksonTester<List<StrategicObjectiveScoreDTO>> strategicObjectiveTester;
    private JacksonTester<List<ProfileSummaryDTO>> profileSummaryTester;

    private EasyRandom randomizer;

    private ObjectMapper objectMapper;

    @Mock
    private ProfilesRepository profileRepository;

    @Mock
    private StrategicObjectiveRepository strategicObjectiveRepository;




    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(named("pillar").and(inClass(StrategicObjectiveScoreDTO.class)), new PillarsRandomizer())
                .randomize(named("strategicObjective").and(inClass(StrategicObjectiveScoreDTO.class)), new StrategicObjectiveApiRandomizer())
                .randomize(named("pillar").and(inClass(StrategicObjectiveScore.class)), new PillarsModelRandomizer())
                .randomize(named("strategicObjective").and(inClass(StrategicObjectiveScore.class)), new StrategicObjectiveModelRandomizer());

        randomizer = new EasyRandom(parameters);

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(new ProfilesApiImpl(profileRepository, strategicObjectiveRepository)).build();
    }

    @Test
    void shouldCalculateProfileSummaryAndRespondWith200() throws Exception {
        var existingProfileId = 1313L;
        var strategicObjectives = randomizer.objects(StrategicObjectiveScore.class, 13).collect(Collectors.toSet());

        given(strategicObjectiveRepository.findAllByProfileId(existingProfileId)).willReturn(strategicObjectives);

        var response = mvc.perform(get("/profiles/{profileId}/summary", existingProfileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();

        final var summary = profileSummaryTester.parseObject(response.getContentAsString());

        verify(strategicObjectiveRepository).findAllByProfileId(existingProfileId);
        assertThat(summary).isNotEmpty();

    }

    @Test
    void addStrategicObjective_shouldSetPillarFromObjectiveAndRespond201() throws Exception {

        var existingProfile = randomizer.nextObject(Profile.class);
        var strategicObjective = randomizer.nextObject(StrategicObjectiveScoreDTO.class);
        strategicObjective.setProfileId(existingProfile.getId());
        strategicObjective.setPillar(null);

        given(profileRepository.getReferenceById(existingProfile.getId())).willReturn(existingProfile);

        mvc.perform(post("/profiles/{profileId}/strategic-objective", strategicObjective.getProfileId())
                        .content(objectMapper.writeValueAsString(strategicObjective))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andDo(MockMvcResultHandlers.print());

        final ArgumentCaptor<StrategicObjectiveScore> objectiveToSave
                = ArgumentCaptor.forClass(StrategicObjectiveScore.class);

        verify(profileRepository).getReferenceById(existingProfile.getId());
        verify(strategicObjectiveRepository).save(objectiveToSave.capture());

        assertThat(objectiveToSave.getValue().getProfile().getId()).isEqualTo(strategicObjective.getProfileId());
        assertThat(objectiveToSave.getValue().getPillar()).isEqualTo(StrategicObjective.valueOf(objectiveToSave.getValue().getStrategicObjective().toString()).pillar());
        assertThat(objectiveToSave.getValue().getComments()).isEqualTo(strategicObjective.getComments());
    }

    @Test
    void getProfileStrategicObjectives_shouldReturnSavedObjectives() throws Exception {
        var existingProfileId = 1313L;
        var strategicObjectives = randomizer.objects(StrategicObjectiveScore.class, 13).collect(Collectors.toSet());

        given(strategicObjectiveRepository.findAllByProfileId(existingProfileId)).willReturn(strategicObjectives);

        var response = mvc.perform(get("/profiles/{profileId}/strategic-objective", existingProfileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();

        verify(strategicObjectiveRepository).findAllByProfileId(existingProfileId);

        final List<StrategicObjectiveScoreDTO> actual = strategicObjectiveTester.parseObject(response.getContentAsString());

        var configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("pillar", "scoreDate", "profileId", "strategicObjective").build();

        assertThat(actual).usingRecursiveComparison(configuration).isEqualTo(strategicObjectives);
    }

    public Resource loadClassPathResource() {
        return new ClassPathResource("samples/strategic-objectives.csv", this.getClass().getClassLoader());
    }
}