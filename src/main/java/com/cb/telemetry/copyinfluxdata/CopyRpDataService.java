package com.cb.telemetry.copyinfluxdata;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.*;

@ComponentScan(basePackages = {"com.cb.telemetry.copyinfluxdata"})
@Service
@Slf4j
@RequiredArgsConstructor
public class CopyRpDataService {
    private final RosePointDataRepository rosePointDataRepository;
    private final ConfigurationMap configurationMap;
    private final DateConfig dateConfig;
    private final EntityAdapter adapter;


    @Qualifier("influxClientSource")
    private final InfluxDBClient influxClientSource;

    public void copy(String assetCode, String assetAbbreviation) {
        QueryApi readApi = influxClientSource.getQueryApi();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        log.info("Copying data of asset : " + assetAbbreviation);


        List<RosePointData> rosePointDataList = new ArrayList<>();

        DateTime fullStartTime = DateTime.parse(dateConfig.getFullStartTime());
        log.info(String.valueOf(fullStartTime));

        DateTime desireDate = DateTime.parse(dateConfig.getFullEndTime());
        log.info(String.valueOf(desireDate));
        DateTime fullEndTime = desireDate.minusDays(10);
        //  DateTime fullStartTime = new DateTime(2023, 9, 30, 17, 15, 0, 0, DateTimeZone.UTC);
        //DateTime fullEndTime = new DateTime(2023, 7, 10, 11, 0, 0, 0, DateTimeZone.UTC);


        String countQuery = "from(bucket: \"rosepoint-data\")\n" +
                "  |> range(start: " + fmt.print(fullEndTime) + ", stop: " + fmt.print(fullStartTime) + ")" +
                "  |> filter(fn: (r) => r[\"asset\"] == \"562115\")\n" +
                "  |> count()\n" +
                "  |> yield(name: \"count\")";

        Long totalRecords = Long.valueOf(0);
        Long processedRecords = Long.valueOf(0);
        Long skippedRecords = Long.valueOf(0);
        List<FluxTable> countResult = readApi.query(countQuery, "CBC Org");
        if (!countResult.isEmpty()) {
            totalRecords = (Long) countResult.get(0).getRecords().get(0).getValue();
            log.info(String.valueOf(totalRecords));
            log.info("Copying " + totalRecords + " data points from source to destination for the asset " + assetCode);
        }
        DateTime startTime = fullStartTime;
        DateTime endTime = startTime.minus(10);
        int previousMonth = endTime.getMonthOfYear();
        log.info("Time Period : " + fmt.print(endTime) + "\tto\t" + fmt.print(startTime) + " Total records processed : " + processedRecords);
        while (endTime.isAfter(fullEndTime)) {
            rosePointDataList.clear();
            String query = "from(bucket: \"rosepoint-data\")\n" +
                    "  |> range(start: " + fmt.print(endTime) + ", stop: " + fmt.print(startTime) + ")" +
                    "  |> filter(fn: (r) => r[\"asset\"] == \"562115\")\n" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n" +
                    "  |> pivot(rowKey: [\"_time\"], columnKey: [\"_measurement\"] , valueColumn: \"_value\")";
            List<FluxTable> resultsRose = readApi.query(query, "CBC Org");
            for (FluxTable result : resultsRose) {
                for (FluxRecord record : result.getRecords()) {
                    processedRecords++;
                    RosePointData data = adapter.getRosePointData(record.getValues());
                    rosePointDataList.add(data);
                }
            }

            if(processedRecords <= totalRecords) {
    double percentage = ((double) processedRecords / totalRecords) * 100;
    percentage = Math.round(percentage);
    log.info("Total records processed=" + processedRecords + "\tPercentage completed=" + percentage);
} else {
    break ;
}

            if (!rosePointDataList.isEmpty()) {
                rosePointDataRepository.saveAll(rosePointDataList);
                rosePointDataRepository.flush();
            }
            startTime = endTime.minusSeconds(1);
            endTime = endTime.minusHours(2);

            if (endTime.getMonthOfYear() != previousMonth) {
                log.info("Processed data from month of : " + endTime.toLocalDate().getYear() + "-"
                        + endTime.toLocalDate().getMonthOfYear() + " Records processed  : " + processedRecords
                        + " Records skipped  : " + skippedRecords);
                previousMonth = endTime.getMonthOfYear();
            }

        }
    }
}
