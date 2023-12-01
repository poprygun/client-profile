package com.vmware.rpm.tools.clientprofile.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.WordUtils;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public enum StrategicObjective {
    AUTOMATICALLY_RECOVER_FROM_FAILURE(Pillar.RELIABILITY)
    , TEST_RECOVERY_PROCEDURES(Pillar.RELIABILITY)
    , SCALE_HORIZONTALLY(Pillar.RELIABILITY)
    , STOP_GUESSING_CAPACITY(Pillar.RELIABILITY)
    , MANAGE_CHANGE_IN_AUTOMATION(Pillar.RELIABILITY)
    , IMPLEMENT_A_STRONG_IDENTITY_FOUNDATION(Pillar.SECURITY)
    , ENABLE_TRACEABILITY(Pillar.SECURITY)
    , APPLY_SECURITY_AT_ALL_LAYERS(Pillar.SECURITY)
    , AUTOMATE_SECURITY_BEST_PRACTICES(Pillar.SECURITY)
    , PROTECT_DATA_IN_TRANSIT_AND_AT_REST(Pillar.SECURITY)
    , KEEP_PEOPLE_AWAY_FROM_DATA(Pillar.SECURITY)
    , PREPARE_FOR_SECURITY_EVENTS(Pillar.SECURITY)
    , PERFORM_OPERATIONS_AS_CODE(Pillar.OPERATIONAL_EXCELLENCE)
    , MAKE_FREQUENT_SMALL_REVERSIBLE_CHANGES(Pillar.OPERATIONAL_EXCELLENCE)
    , REFINE_OPERATIONS_PROCEDURES_FREQUENTLY(Pillar.OPERATIONAL_EXCELLENCE)
    , ANTICIPATE_FAILURE(Pillar.OPERATIONAL_EXCELLENCE)
    , LEARN_FROM_ALL_OPERATIONAL_FAILURES(Pillar.OPERATIONAL_EXCELLENCE)
    , DEMOCRATIZE_ADVANCED_TECHNOLOGIES(Pillar.PERFORMANCE_EFFICIENCY)
    , GO_GLOBAL_IN_MINUTES(Pillar.PERFORMANCE_EFFICIENCY)
    , USE_SERVERLESS_ARCHITECTURES(Pillar.PERFORMANCE_EFFICIENCY)
    , EXPERIMENT_MORE_OFTEN(Pillar.PERFORMANCE_EFFICIENCY)
    , CONSIDER_MECHANICAL_SYMPATHY(Pillar.PERFORMANCE_EFFICIENCY)
    , IMPLEMENT_CLOUD_FINANCIAL_MANAGEMENT(Pillar.COST_OPTIMIZATION)
    , ADOPT_A_CONSUMPTION_MODEL(Pillar.COST_OPTIMIZATION)
    , MEASURE_OVERALL_EFFICIENCY(Pillar.COST_OPTIMIZATION)
    , STOP_SPENDING_MONEY_ON_UNDIFFERENTIATED_HEAVY_LIFTING(Pillar.COST_OPTIMIZATION)
    , ANALYZE_AND_ATTRIBUTE_EXPENDITURE(Pillar.COST_OPTIMIZATION);

    private final Pillar pillar;

    private Set<Pair<String, Double>> metrics = Collections.emptySet();

    StrategicObjective with(Set<Pair<String, Double>> metrics) {
        this.metrics = metrics;
        return this;
    }

    StrategicObjective(Pillar pillar) {
        this.pillar = pillar;
    }

    public Pillar pillar(){
        return pillar;
    }

    public double score() {
        return metrics.stream().mapToDouble(Pair::getValue).average().getAsDouble();
    }

    public String description(){

        var capsWithUnderscores = this.name().toLowerCase(Locale.ROOT);
        return WordUtils.capitalize(StringUtils.replace(capsWithUnderscores, "_", " "));
    }

}
