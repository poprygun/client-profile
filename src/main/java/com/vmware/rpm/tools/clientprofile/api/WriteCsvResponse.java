package com.vmware.rpm.tools.clientprofile.api;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.vmware.rpm.tools.clientprofile.model.BestPractices;
import lombok.extern.slf4j.Slf4j;

import java.io.Writer;
import java.util.List;

@Slf4j
public class WriteCsvResponse {
    public static void writeBestPractices(Writer writer, List<CsvBestPractices> practices) {

        try {

            var builder = getStatefulBean(writer);
            builder.write(practices);

        } catch (CsvException ex) {

            log.error("Error mapping Bean to CSV", ex);
        }
    }

    public static void writeBestPractices(Writer writer, CsvBestPractices practice) {

        try {

            var builder = getStatefulBean(writer);
            builder.write(practice);

        } catch (CsvException ex) {

            log.error("Error mapping Bean to CSV", ex);
        }
    }

    private static StatefulBeanToCsv<CsvBestPractices> getStatefulBean(Writer writer) {

        ColumnPositionMappingStrategy<CsvBestPractices> mapStrategy
                = new ColumnPositionMappingStrategy<>();

        mapStrategy.setType(CsvBestPractices.class);

        StatefulBeanToCsv<CsvBestPractices> builder = new StatefulBeanToCsvBuilder<CsvBestPractices>(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(',')
                .build();

        return builder;
    }
}
