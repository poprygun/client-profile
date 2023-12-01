package com.vmware.rpm.tools.clientprofile.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.ModelDTO;
import com.vmware.rpm.tools.clientprofile.model.ModelRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ModelApiImplTest {

    private MockMvc mvc;
    private EasyRandom randomizer;
    private ObjectMapper objectMapper;
    private JacksonTester<ModelDTO> dtoModelTester;
    private JacksonTester<List<ModelDTO>> dtoModelsTester;

    @Mock
    private ModelRepository modelRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        randomizer = new EasyRandom();

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(new ModelApiImpl(modelRepository)).build();

    }

    @Test
    void getModelById() throws Exception {

        var modelId = 1313L;

        var existingModel = randomizer.nextObject(Model.class);
        existingModel.setId(modelId);

        given(modelRepository.findById(modelId)).willReturn(Optional.of(existingModel));

        final MvcResult mvcResult = mvc.perform(get("/models/{modelId}", modelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        verify(modelRepository).findById(modelId);

        var modelDTO = dtoModelTester.parseObject(mvcResult.getResponse().getContentAsString());
        assertThat(modelDTO.getId()).isEqualTo(modelId);

    }

    @Test
    void getModels() throws Exception {

        var existingModels = randomizer.objects(Model.class, 13).collect(Collectors.toList());

        given(modelRepository.findAll()).willReturn(existingModels);

        final MvcResult mvcResult = mvc.perform(get("/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        verify(modelRepository).findAll();

        var modelDtos = dtoModelsTester.parseObject(mvcResult.getResponse().getContentAsString());
        assertThat(modelDtos).hasSameSizeAs(existingModels);
    }
}