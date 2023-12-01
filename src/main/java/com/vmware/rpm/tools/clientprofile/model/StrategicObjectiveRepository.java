package com.vmware.rpm.tools.clientprofile.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StrategicObjectiveRepository extends JpaRepository<StrategicObjectiveScore, Long> {
    Set<StrategicObjectiveScore> findAllByProfileId(Long profileId);
}
