package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.mapper.CsvBestPracticeMapper;
import com.vmware.rpm.tools.clientprofile.mapper.ModelMapper;
import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.ModelDTO;
import com.vmware.rpm.tools.clientprofile.model.ModelRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Collections;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ModelApiImpl implements ModelsApi {

    @NonNull
    private ModelRepository modelRepository;

    private ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);
    private CsvBestPracticeMapper csvBestPracticeMapper = Mappers.getMapper(CsvBestPracticeMapper.class);

    @Override
    public ResponseEntity<String> getModelData(Long modelId) {

        var maybeModel = modelRepository.findById(modelId);
        return maybeModel.map(model -> {
                    var bestPractices = model.getBestPractices();
                    var csvPractices = csvBestPracticeMapper.toDto(Set.copyOf(bestPractices));

                    try (StringWriter writer = new StringWriter()) {
                        WriteCsvResponse.writeBestPractices(writer, List.copyOf(csvPractices));

                        return ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=model_" + modelId + ".csv")
                                .contentLength(writer.toString().length())
                                .contentType(MediaType.parseMediaType("text/csv"))
                                .body(writer.toString());
                    } catch (IOException e) {
                        log.error("Error downloading model:🔥", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                    }
                }
        ).orElse(
                ResponseEntity.notFound().build()
        );

    }

    @Override
    public ResponseEntity<ModelDTO> getModelById(Long modelId) {
        var maybeClient = modelRepository.findById(modelId);

        return maybeClient.map(client -> {
            var clientDTO = modelMapper.toDto(client);
            return ResponseEntity.ok(clientDTO);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ModelDTO>> getModels() {
        final List<Model> allModels = modelRepository.findAll();
        final Set<ModelDTO> clientDTOS = modelMapper.toDto(new HashSet<>(allModels));
        return ResponseEntity.ok(clientDTOS.stream().toList());
    }
}
