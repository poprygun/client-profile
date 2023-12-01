package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveDefinition;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

public class StrategicObjectiveDescriptionApiRandomizer implements Randomizer<String> {

    @Override
    public String getRandomValue() {

        Random r = new Random();
        return StrategicObjectiveDefinition.values()[r.nextInt(StrategicObjectiveDefinition.values().length - 1)].getValue();
    }
}
