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

/**
 * @author lokeshvenkatesan
 */
@ComponentScan(basePackages = {"com.cb.telemetry.copyinfluxdata"})
@Service
@Slf4j
@RequiredArgsConstructor
public class CopyPlcDataService {


    private final ConfigurationMap configurationMap;

        private  final  TelemetryDataRepository telemetryDataRepository;
    private final EntityAdapter adapter;

private final DateConfig dateConfig;

    @Qualifier("influxClientSource")
    private final InfluxDBClient influxClientSource;

    public void copy(String assetCode, String assetAbbreviation) {
        QueryApi readApi = influxClientSource.getQueryApi();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        log.info("Copying data of asset : " + assetAbbreviation);
        List<String> measurementNames = configurationMap.getPlcMeasurementNamesList();

        Collections.sort(measurementNames, Collections.reverseOrder());

        List<TelemetryData> telemetryDataList = new ArrayList<>();


        DateTime fullStartTime = DateTime.parse(dateConfig.getFullStartTime());
        log.info(String.valueOf(fullStartTime));
        DateTime desireDate = DateTime.parse(dateConfig.getFullEndTime());
        log.info(String.valueOf(desireDate));
        DateTime fullEndTime = desireDate.minusDays(3);
//        DateTime fullStartTime =  DateTime.now();
//        DateTime desireDate = new DateTime(2023, 1, 1, 11, 59, 0, 0, DateTimeZone.UTC);
//        DateTime fullEndTime = fullStartTime.minusDays(3);


            String countQuery = "from(bucket: \"telemetry-data\")\n" +
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
                log.info("Copying " + totalRecords + " data points from source to destination for the asset " + assetCode);
            }

            DateTime startTime =fullStartTime;
            DateTime endTime = startTime.minus(10);
            int previousMonth = endTime.getMonthOfYear();
            log.info("Time Period : " + fmt.print(endTime) + "\tto\t" + fmt.print(startTime) + " Total records processed : " + processedRecords);
            while (endTime.isAfter(fullEndTime)) {
                telemetryDataList.clear();
                String query = "from(bucket: \"telemetry-data\")\n" +
                        "  |> range(start: " + fmt.print(endTime) + ", stop: " + fmt.print(startTime) + ")" +
                        "  |> filter(fn: (r) => r[\"asset\"] == \"562115\")\n" +
                        "  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n" +
                        "  |> pivot(rowKey: [\"_time\"], columnKey: [\"_measurement\"] , valueColumn: \"_value\")";
                List<FluxTable> results = readApi.query(query, "CBC Org");


                for (FluxTable result : results) {

                    for (FluxRecord record : result.getRecords()) {
                        processedRecords++;
                        TelemetryData data = adapter.getTelemetryData(record.getValues());
                        telemetryDataList.add(data);
                    }
                }
                if (!telemetryDataList.isEmpty()) {
                    telemetryDataRepository.saveAll(telemetryDataList);
                    telemetryDataRepository.flush();
                }
                if (endTime.getMonthOfYear() != previousMonth) {
                    log.info("Processed data from month of : " + endTime.toLocalDate().getYear() + "-"
                            + endTime.toLocalDate().getMonthOfYear() + " Records processed  : " + processedRecords
                            + " Records skipped  : " + skippedRecords);
                    previousMonth = endTime.getMonthOfYear();
                }
                log.info("Total records processed=" + processedRecords + "\tPercentage completed=" + ((double)processedRecords / totalRecords) *100 );

                if (!telemetryDataList.isEmpty())
                    startTime = endTime.minusSeconds(1);
                endTime = endTime.minusHours(2);
            }

    }


}

