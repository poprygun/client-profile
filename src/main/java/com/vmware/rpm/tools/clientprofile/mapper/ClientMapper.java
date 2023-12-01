package com.vmware.rpm.tools.clientprofile.mapper;

import com.vmware.rpm.tools.clientprofile.model.Client;
import com.vmware.rpm.tools.clientprofile.model.ClientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
}
