package com.vmware.rpm.tools.clientprofile.model;

import lombok.Builder;

import java.util.Arrays;
import java.util.Set;

@Builder
public class StrateticObjectiveEvaluator {
    private Pillar pillar;
    private Set<StrategicObjective> definedObjectives;

    public double evaluate(StrategicObjective... achievedObjectives) {
        final double objectiveScore
                = Arrays.stream(achievedObjectives).toList().stream().mapToDouble(StrategicObjective::score).sum();
        return objectiveScore/ (double) this.definedObjectives.size();
    }
}
