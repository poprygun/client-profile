package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.StrategicObjective;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

public class StrategicObjectiveModelRandomizer implements Randomizer<StrategicObjective> {

    @Override
    public StrategicObjective getRandomValue() {

        Random r = new Random();

        return StrategicObjective.values()[r.nextInt(StrategicObjective.values().length - 1)];
    }
}
