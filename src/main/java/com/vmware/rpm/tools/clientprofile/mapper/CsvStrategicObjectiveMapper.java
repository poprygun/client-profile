package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.api.CsvStrategicObjective;
import com.vmware.rpm.tools.clientprofile.model.Pillar;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjective;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScore;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Locale;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public  interface CsvStrategicObjectiveMapper extends EntityMapper<CsvStrategicObjective, StrategicObjectiveScore> {

    @Mapping(source = "strategicObjective", target = "strategicObjective")
    default StrategicObjective mapStrategicObjective(String strategicObjective) {
        var cappedWithSnakeCase = StringUtils.replace(strategicObjective, " ", "_").toUpperCase(Locale.ROOT);
        return StrategicObjective.valueOf(cappedWithSnakeCase);
    }

    @Override
    @Mapping(source = "strategicObjective", target = "pillar", qualifiedByName = "pillarFromObjective")
    StrategicObjectiveScore toEntity(CsvStrategicObjective dto);

    @Named("pillarFromObjective")
    default Pillar toPillar(String objective){
        var cappedWithSnakeCase = StringUtils.replace(objective, " ", "_").toUpperCase(Locale.ROOT);
        final StrategicObjective strategicObjective = StrategicObjective.valueOf(cappedWithSnakeCase);
        return strategicObjective.pillar();
    }
}
