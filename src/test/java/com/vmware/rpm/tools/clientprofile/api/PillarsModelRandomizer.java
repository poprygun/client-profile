package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.Pillar;
import com.vmware.rpm.tools.clientprofile.model.StrategicObjectiveScoreDTO;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

public class PillarsModelRandomizer implements Randomizer<Pillar> {

    @Override
    public Pillar getRandomValue() {

        Random r = new Random();

        return Pillar.values()[r.nextInt(Pillar.values().length - 1)];
    }
}
