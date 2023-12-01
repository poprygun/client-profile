package com.vmware.rpm.tools.clientprofile.api;


import com.opencsv.bean.CsvToBeanBuilder;
import com.vmware.rpm.tools.clientprofile.mapper.CsvStrategicObjectiveMapper;
import com.vmware.rpm.tools.clientprofile.model.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfilesApiImpl implements ProfilesApi {

    @NonNull
    private ProfilesRepository profileRepository;

    @NonNull
    private StrategicObjectiveRepository strategicObjectiveRepository;

    private StrategicObjectiveScoreMapper objectiveScoreMapper = Mappers.getMapper(StrategicObjectiveScoreMapper.class);

    private CsvStrategicObjectiveMapper csvStrategicObjectiveMapper = Mappers.getMapper(CsvStrategicObjectiveMapper.class);

    @Override
    public ResponseEntity<List<ProfileSummaryDTO>> getSummary(Long profileId) {

        var scores = strategicObjectiveRepository.findAllByProfileId(profileId);

        final Map<Pillar, Double> scoresByPillar
                = scores.stream().collect(Collectors.groupingBy(StrategicObjectiveScore::getPillar
                , Collectors.averagingDouble(StrategicObjectiveScore::getScore)));

        var profileSummaryDTOs = scoresByPillar.entrySet().stream().map(e -> {
            var profileSummary = new ProfileSummaryDTO();
            profileSummary.setPillar(PillarDefinition.valueOf(e.getKey().name()));
            profileSummary.score(e.getValue());
            return profileSummary;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(profileSummaryDTOs);
    }

    @Override
    public ResponseEntity<Void> addStrategicObjectives(Long profileId, Resource body) {

        try {
            try (var bodyStream = body.getInputStream()) {
                try (var inputStreamReader = new InputStreamReader(bodyStream)) {
                    List<CsvStrategicObjective> objectives = new CsvToBeanBuilder(inputStreamReader)
                            .withType(CsvStrategicObjective.class)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build()
                            .parse();

                    var entities = csvStrategicObjectiveMapper.toEntity(Set.copyOf(objectives));

                    final Profile profile = profileRepository.getReferenceById(profileId);

                    entities.forEach(soToSave -> {
                        soToSave.setProfile(profile);
                        soToSave.setScoreDate(Instant.now());
                    });
                    strategicObjectiveRepository.saveAll(entities);
                }
            }
        } catch (IOException e) {
            log.error("Error uploading objective score data:🔥", e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build();
        }


        return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
    }

    @Override
    public ResponseEntity<Void> addStrategicObjective(Long profileId, StrategicObjectiveScoreDTO strategicObjectiveScoreDTO) {

        var strategicObjectiveScore = objectiveScoreMapper.toEntity(strategicObjectiveScoreDTO);

        final Profile profile = profileRepository.getReferenceById(profileId);
        strategicObjectiveScore.setProfile(profile);
        strategicObjectiveRepository.save(strategicObjectiveScore);

        return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
    }

    @Override
    public ResponseEntity<List<StrategicObjectiveScoreDTO>> getProfileStrategicObjectives(Long profileId) {

        var strategicObjectives = strategicObjectiveRepository.findAllByProfileId(profileId);
        return ResponseEntity.ok(objectiveScoreMapper.toDto(strategicObjectives).stream().toList());

    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ProfilesApi.super.getRequest();
    }
}