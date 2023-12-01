package com.vmware.rpm.tools.clientprofile.api;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvStrategicObjective {
    @CsvBindByName(column = "strategic_objective")
    String strategicObjective;

    @CsvBindByName(column = "score")
    double score;

    @CsvBindByName(column = "comments")
    String comments;
}
