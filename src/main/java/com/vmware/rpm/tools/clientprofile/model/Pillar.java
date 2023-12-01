package com.vmware.rpm.tools.clientprofile.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.Locale;

public enum Pillar {
    RELIABILITY, SECURITY, OPERATIONAL_EXCELLENCE, PERFORMANCE_EFFICIENCY, COST_OPTIMIZATION;

    public String description(){

        var capsWithUnderscores = this.name().toLowerCase(Locale.ROOT);
        return WordUtils.capitalize(StringUtils.replace(capsWithUnderscores, "_", " "));
    }
}
