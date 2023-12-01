package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.PillarDefinition;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

public class PillarsRandomizer implements Randomizer<PillarDefinition> {

    @Override
    public PillarDefinition getRandomValue() {

        Random r = new Random();

        return PillarDefinition.values()[r.nextInt(PillarDefinition.values().length - 1)];
    }
}
