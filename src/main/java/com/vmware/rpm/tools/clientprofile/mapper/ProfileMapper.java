package com.vmware.rpm.tools.clientprofile.mapper;


import com.vmware.rpm.tools.clientprofile.model.Profile;
import com.vmware.rpm.tools.clientprofile.model.ProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ClientMapper.class})
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {

    @Override
    @Mapping(source = "entity", target = "clientId", qualifiedByName = "client")
    ProfileDTO toDto(Profile entity);

    @Named("client")
    default Long toClientId(Profile profile){
        return profile.getClient().getId();
    }

}
