package com.vmware.rpm.tools.clientprofile.api;

import com.vmware.rpm.tools.clientprofile.model.BestPractices;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class WriteCsvResponseTest {

    @Test
    void shouldRespondWithCsvLine() throws IOException {
        EasyRandom random = new EasyRandom();
        var bestPractices = random.nextObject(CsvBestPractices.class);

        try(StringWriter output = new StringWriter()) {
            WriteCsvResponse.writeBestPractices(output, bestPractices);
            System.out.println(output);
            assertThat(output).isNotNull();
        }
    }

    @Test
    void shouldRespondWithCsvData() throws IOException {
        EasyRandom random = new EasyRandom();
        var bestPractices = random.objects(CsvBestPractices.class, 13).collect(Collectors.toList());

        try(StringWriter output = new StringWriter()) {
            WriteCsvResponse.writeBestPractices(output, bestPractices);
            assertThat(output).isNotNull();
        }
    }

}