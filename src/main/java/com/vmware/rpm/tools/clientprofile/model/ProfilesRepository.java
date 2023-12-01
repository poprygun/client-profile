package com.vmware.rpm.tools.clientprofile.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilesRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByClientId(Long clientId);
}
