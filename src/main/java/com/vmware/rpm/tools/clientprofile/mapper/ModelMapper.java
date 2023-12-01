package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.ModelDTO;
import org.mapstruct.Mapper;

@Mapper(uses = {BestPracticesMapper.class})
public interface ModelMapper extends EntityMapper<ModelDTO, Model> {
}
