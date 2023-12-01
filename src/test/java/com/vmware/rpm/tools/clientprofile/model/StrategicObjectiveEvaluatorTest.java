package com.vmware.rpm.tools.clientprofile.model;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.data.Percentage;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.vmware.rpm.tools.clientprofile.model.StrategicObjective.*;
import static org.assertj.core.api.Assertions.assertThat;

class StrategicObjectiveEvaluatorTest {

    @Test
    void shouldProduceAverageFromMetrics() {
        Set<Pair<String, Double>> metrics = Set.of(Pair.of("One", 0.5D), Pair.of("Two", 0.7D), Pair.of("Three", 0.9D));
        final StrategicObjective objective = AUTOMATICALLY_RECOVER_FROM_FAILURE.with(metrics);
        final double score = objective.score();

        assertThat(score).isCloseTo(0.70D, Percentage.withPercentage(2));
    }

    @Test
    void shouldScoreWhenAllObjectivesAre50Percent(){
        Set<Pair<String, Double>> metrics = Set.of(Pair.of("One", 0.5D), Pair.of("Two", 0.5D));
        final double expectedScore = 0.5D;
        final StrategicObjective[] achievedObjectives = Arrays.array(STOP_GUESSING_CAPACITY.with(metrics), MANAGE_CHANGE_IN_AUTOMATION.with(metrics), AUTOMATICALLY_RECOVER_FROM_FAILURE.with(metrics));

        assertScore(expectedScore, achievedObjectives);
    }

    @Test
    void shouldScoreWhenOneObjectiveNotCompleted(){
        Set<Pair<String, Double>> metrics = Set.of(Pair.of("One", 1.0D), Pair.of("Two", 1.0D));
        final double expectedScore = 0.66D;
        final StrategicObjective[] achievedObjectives = Arrays.array(STOP_GUESSING_CAPACITY.with(metrics), MANAGE_CHANGE_IN_AUTOMATION.with(metrics));

        assertScore(expectedScore, achievedObjectives);
    }

    @Test
    void shouldScoreBasedOnSumOfHighestScoredMetrics() {
        Set<Pair<String, Double>> metrics = Set.of(Pair.of("One", 1.0D), Pair.of("Two", 1.0D));
        final double expectedScore = 1D;
        final StrategicObjective[] achievedObjectives = Arrays.array(STOP_GUESSING_CAPACITY.with(metrics), MANAGE_CHANGE_IN_AUTOMATION.with(metrics), AUTOMATICALLY_RECOVER_FROM_FAILURE.with(metrics));

        assertScore(expectedScore, achievedObjectives);
    }

    private void assertScore(double expectedScore, StrategicObjective[] achievedObjectives) {
        StrateticObjectiveEvaluator strateticObjectiveEvaluator = StrateticObjectiveEvaluator.builder()
                .pillar(Pillar.RELIABILITY)
                .definedObjectives(Set.of(AUTOMATICALLY_RECOVER_FROM_FAILURE, STOP_GUESSING_CAPACITY, MANAGE_CHANGE_IN_AUTOMATION))
                .build();


        double objectiveScore = strateticObjectiveEvaluator.evaluate(achievedObjectives);
        assertThat(objectiveScore).isCloseTo(expectedScore, Percentage.withPercentage(2));
    }
}
