package com.cb.telemetry.copyinfluxdata;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
    private final DateConfig dateConfig;
    private final EntityAdapter adapter;


    @Qualifier("influxClientSource")
    private final InfluxDBClient influxClientSource;

    public void copy(String assetCode, String assetAbbreviation) {
        QueryApi readApi = influxClientSource.getQueryApi();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        log.info("Copying data of asset : " + assetAbbreviation);


        List<RosePointData> rosePointDataList = new ArrayList<>();

//        DateTime fullStartTime = DateTime.parse(dateConfig.getFullStartTime());
//        DateTime desireDate = DateTime.parse(dateConfig.getFullEndTime());
//        DateTime fullEndTime = desireDate.minusDays(10);
        DateTime fullStartTime = new DateTime(2023, 10, 17, 17, 0, 0, 0, DateTimeZone.UTC);
        DateTime fullEndTime = new DateTime(2023, 10, 15, 11, 0, 0, 0, DateTimeZone.UTC);
String assetId = dateConfig.getAssetId();
        String countQuery = "from(bucket: \"rosepoint-data\")\n" +
                "  |> range(start: " + fmt.print(fullEndTime) + ", stop: " + fmt.print(fullStartTime) + ")" +
                "  |> filter(fn: (r) => r[\"asset\"] == \"" + assetId + "\")\n" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"position\")" +
                "  |> count()\n" +
                "  |> yield(name: \"count\")";
log.info(countQuery);
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
        DateTime endTime = startTime.minusHours(6);
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

            double percentage = ((double) processedRecords / totalRecords) * 100;
            percentage = Math.min(percentage, 100);
            log.info("Total records processed=" + processedRecords + "\tPercentage completed=" + percentage);


//            if(processedRecords <= totalRecords) {
//                double percentage = ((double) processedRecords / totalRecords) * 100;
//                percentage = Math.min(percentage, 100);
//                log.info("Total records processed=" + processedRecords + "\tPercentage completed=" + percentage);
//            } else {
//                break;
//            }

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
log.info(String.valueOf(processedRecords));
    }
}

