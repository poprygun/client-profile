package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface BestPracticesMapper extends EntityMapper<BestPracticeDTO, BestPractices> {


    @Mapping(source = "strategicObjective", target = "pillar", qualifiedByName = "pillarFromObjectiveModel")
    BestPractices toEntity(BestPracticeDTO dto);

    @Mapping(source = "strategicObjective", target = "pillar", qualifiedByName = "pillarFromObjectiveDto")
    BestPracticeDTO toDto(BestPractices model);

    @Named("pillarFromObjectiveModel")
    default Pillar toPillar(StrategicObjectiveDefinition objective){
        final StrategicObjective strategicObjective = StrategicObjective.valueOf(objective.name());
        return strategicObjective.pillar();
    }

    @Named("pillarFromObjectiveDto")
    default PillarDefinition toPillar(StrategicObjective strategicObjective){
        return PillarDefinition.fromValue(strategicObjective.pillar().description());
    }
}