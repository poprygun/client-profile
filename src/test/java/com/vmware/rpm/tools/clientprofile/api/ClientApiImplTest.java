package com.vmware.rpm.tools.clientprofile.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vmware.rpm.tools.clientprofile.mapper.ClientMapper;
import com.vmware.rpm.tools.clientprofile.mapper.ProfileMapper;
import com.vmware.rpm.tools.clientprofile.model.*;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClientApiImplTest {

    private MockMvc mvc;

    private JacksonTester<ClientDTO> dtoClientTester;
    private JacksonTester<List<ClientDTO>> dtoClientsTester;
    private JacksonTester<ProfileDTO> dtoProfileTester;
    private JacksonTester<List<ProfileDTO>> dtoProfilesTester;

    private EasyRandom randomizer;

    private ObjectMapper objectMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProfilesRepository profilesRepository;

    private ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);
    private ProfileMapper profileMapper = Mappers.getMapper(ProfileMapper.class);

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

        mvc = MockMvcBuilders.standaloneSetup(new ClientApiImpl(clientRepository, profilesRepository)).build();

    }

    @Test
    void addClient() throws Exception {

        var clientDTO = randomizer.nextObject(ClientDTO.class);

        final Client client = clientMapper.toEntity(clientDTO);
        client.setId(1313L);
        given(clientRepository.save(any())).willReturn(client);

        final MvcResult mvcResult = mvc.perform(post("/clients")
                        .content(objectMapper.writeValueAsString(clientDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        verify(clientRepository).save(any(Client.class));

        var savedClientDto = dtoClientTester.parseObject(mvcResult.getResponse().getContentAsString());
        assertThat(savedClientDto.getId()).isPositive();
        assertThat(savedClientDto.getName()).isEqualTo(clientDTO.getName());
    }

    @Test
    void addProfile() throws Exception {

        var profileDto = randomizer.nextObject(ProfileDTO.class);
        profileDto.setClientId(1313L);

        var savedProfile = profileMapper.toEntity(profileDto);
        var client = randomizer.nextObject(Client.class);
        client.setId(profileDto.getClientId());

        given(clientRepository.getReferenceById(profileDto.getClientId()))
                .willReturn(client);

        savedProfile.setClient(client);

        given(profilesRepository.save(any(Profile.class))).willReturn(savedProfile);

        final MvcResult mvcResult = mvc.perform(post("/clients/{clientId}/profiles", profileDto.getClientId())
                        .content(objectMapper.writeValueAsString(profileDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        verify(profilesRepository).save(any(Profile.class));
        verify(clientRepository).getReferenceById(client.getId());

        var savedProfileDto = dtoProfileTester.parseObject(mvcResult.getResponse().getContentAsString());

        assertThat(savedProfileDto.getId()).isNotNull();
        assertThat(savedProfileDto.getClientId()).isEqualTo(profileDto.getClientId());
        assertThat(savedProfileDto.getName()).isEqualTo(profileDto.getName());

    }

    @Test
    void getClientById() throws Exception {
        var clientId = 1313L;

        var existingClient = randomizer.nextObject(Client.class);
        existingClient.setId(clientId);

        given(clientRepository.findById(clientId)).willReturn(Optional.of(existingClient));

        final MvcResult mvcResult = mvc.perform(get("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        verify(clientRepository).findById(clientId);

        final ClientDTO clientDTO = dtoClientTester.parseObject(mvcResult.getResponse().getContentAsString());
        assertThat(clientDTO.getId()).isPositive();

    }

    @Test
    void getClients() throws Exception {

        var clients = randomizer.objects(Client.class, 13).collect(Collectors.toList());
        given(clientRepository.findAll()).willReturn(clients);

        final MvcResult mvcResult = mvc.perform(get("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        verify(clientRepository).findAll();

        var clientDTOs = dtoClientsTester.parseObject(mvcResult.getResponse().getContentAsString());

        assertThat(clientDTOs).hasSameSizeAs(clients);

    }

    @Test
    void getProfiles() throws Exception {
        var profiles = randomizer.objects(Profile.class, 13).collect(Collectors.toList());
        final long clientId = 1313L;
        profiles.forEach(p -> {
                    p.getClient().setId(clientId);
                }
        );

        given(profilesRepository.findAllByClientId(clientId)).willReturn(profiles);

        final MvcResult mvcResult = mvc.perform(get("/clients/{clientId}/profiles", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        verify(profilesRepository).findAllByClientId(clientId);

        var profileDTOs = dtoProfilesTester.parseObject(mvcResult.getResponse().getContentAsString());

        assertThat(profileDTOs).hasSameSizeAs(profiles);

    }
}