package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.mapper.ClientMapper;
import com.vmware.rpm.tools.clientprofile.mapper.ProfileMapper;
import com.vmware.rpm.tools.clientprofile.model.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientApiImpl implements ClientsApi {

    @NonNull
    private ClientRepository clientRepository;

    @NonNull
    private ProfilesRepository profilesRepository;

    private ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);
    private ProfileMapper profileMapper = Mappers.getMapper(ProfileMapper.class);

    @Override
    public ResponseEntity<ClientDTO> addClient(ClientDTO clientDTO) {
        var toSave = clientMapper.toEntity(clientDTO);
        var saved = clientRepository.save(toSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<ProfileDTO> addProfile(Long clientId, ProfileDTO profileDTO) {

        Profile profileToSave = profileMapper.toEntity(profileDTO);

        final Client referenceById = clientRepository.getReferenceById(clientId);
        profileToSave.setClient(referenceById);

        var savedProfile = profilesRepository.save(profileToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(profileMapper.toDto(savedProfile));
    }

    @Override
    public ResponseEntity<ClientDTO> getClientById(Long clientId) {
        var maybeClient = clientRepository.findById(clientId);
        System.out.println(">>>>>>>>>>Here we go....");

        return maybeClient.map(client -> {
            var clientDTO = clientMapper.toDto(client);
            return ResponseEntity.ok(clientDTO);
        }).orElse(ResponseEntity.notFound().build());


    }

    @Override
    public ResponseEntity<List<ClientDTO>> getClients() {
        final List<Client> allClients = clientRepository.findAll();
        final Set<ClientDTO> clientDTOS = clientMapper.toDto(new HashSet<>(allClients));
        return ResponseEntity.ok(clientDTOS.stream().toList());
    }

    @Override
    public ResponseEntity<List<ProfileDTO>> getProfiles(Long clientId) {
        var profiles = profilesRepository.findAllByClientId(clientId);
        return ResponseEntity.ok(profileMapper.toDto(new HashSet<>(profiles)).stream().toList());
    }
}
