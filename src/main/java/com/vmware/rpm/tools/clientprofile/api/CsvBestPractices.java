package com.vmware.rpm.tools.clientprofile.api;

import com.opencsv.bean.CsvBindByName;
import com.vmware.rpm.tools.clientprofile.model.Model;
import com.vmware.rpm.tools.clientprofile.model.Pillar;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvBestPractices {

    @CsvBindByName(column = "model")
    private String model;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "pillar")
    private String pillar;

    @CsvBindByName(column = "strategic_objective")
    private String strategicObjective;
}