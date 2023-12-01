package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.api.CsvBestPractices;
import com.vmware.rpm.tools.clientprofile.model.BestPractices;
import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.Pillar;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjective;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CsvBestPracticeMapper extends EntityMapper<CsvBestPractices, BestPractices>{
    default Model mapModel(String value){
        var model = new Model();
        model.setName(value);
        return model;
    }

    default String mapModel(Model value){
        return value.getName();
    }

    default String mapPillar(Pillar pillar){
        return pillar.description();
    }

    default String mapStrategicObjectife(StrategicObjective strategicObjective){
        return strategicObjective.description();
    }
}
