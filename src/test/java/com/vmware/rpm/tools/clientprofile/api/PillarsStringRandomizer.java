package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.PillarDefinition;
import org.jeasy.random.api.Randomizer;

import java.util.EnumSet;
import java.util.Random;

public class PillarsStringRandomizer implements Randomizer<String> {

    @Override
    public String getRandomValue() {

        Random r = new Random();

        return EnumSet.allOf(PillarDefinition.class)
                .stream().toList().get(r.nextInt(PillarDefinition.values().length - 1)).getValue();
    }
}
