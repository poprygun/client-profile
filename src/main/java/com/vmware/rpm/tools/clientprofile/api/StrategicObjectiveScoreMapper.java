package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.mapper.EntityMapper;
import com.vmware.rpm.tools.clientprofile.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StrategicObjectiveScoreMapper extends EntityMapper<StrategicObjectiveScoreDTO, StrategicObjectiveScore> {

    default OffsetDateTime map(Instant value) {
        return OffsetDateTime.ofInstant(value, ZoneId.systemDefault());
    }

    default Instant map(OffsetDateTime value){
        return value.toInstant();
    }

    @Override
    @Mapping(source = "profile", target = "profileId", qualifiedByName = "profileId")
    StrategicObjectiveScoreDTO toDto(StrategicObjectiveScore entity);

    @Named("profileId")
    default Long toProfileId(Profile profile){
        return profile.getId();
    }

    @Override
    @Mapping(source = "strategicObjective", target = "pillar", qualifiedByName = "pillarFromObjective")
    StrategicObjectiveScore toEntity(StrategicObjectiveScoreDTO dto);

    @Named("pillarFromObjective")
    default Pillar toPillar(StrategicObjectiveDefinition objective){
        final StrategicObjective strategicObjective = StrategicObjective.valueOf(objective.name());
        return strategicObjective.pillar();
    }

    default PillarDefinition toPillar(Pillar pillar){
        return PillarDefinition.valueOf(pillar.name());
    }
}
