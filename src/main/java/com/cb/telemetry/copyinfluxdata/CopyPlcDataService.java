package com.cb.telemetry.copyinfluxdata;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author lokeshvenkatesan
 */
@ComponentScan(basePackages = {"com.cb.telemetry.copyinfluxdata"})
@Service
@Slf4j

public class CopyPlcDataService {

    private final ConfigurationMap configurationMap;

    private final TelemetryDataRepository telemetryDataRepository;
    private final EntityAdapter adapter;

    private final DateConfig dateConfig;

    private final QueryApi readApi;

    private final PlcCopyTrackerRepository trackerRepository;

    final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    @Qualifier("influxClientSource")
    private final InfluxDBClient influxClientSource;

    public CopyPlcDataService(ConfigurationMap configurationMap, TelemetryDataRepository telemetryDataRepository, EntityAdapter adapter, DateConfig dateConfig, PlcCopyTrackerRepository trackerRepository, InfluxDBClient influxClientSource) {
        this.configurationMap = configurationMap;
        this.telemetryDataRepository = telemetryDataRepository;
        this.adapter = adapter;
        this.dateConfig = dateConfig;
        this.trackerRepository = trackerRepository;
        this.influxClientSource = influxClientSource;
        readApi = influxClientSource.getQueryApi();
    }

    public void copy(String assetCode, String assetAbbreviation) throws InterruptedException {


        List<String> measurementNames = configurationMap.getPlcMeasurementNamesList();
        Collections.sort(measurementNames, Collections.reverseOrder());
        List<TelemetryData> telemetryDataList = new ArrayList<>();


        DateTime fullStartTime = DateTime.parse(dateConfig.getFullStartTime());
        DateTime fullStopTime = DateTime.parse(dateConfig.getFullStopTime());


        //Start is small Date. *************************
        //Stop is larger Date. *************************

        Optional<CopyTrackerPLC> trackerRecord = trackerRepository.findById(assetAbbreviation);


        Long processedRecords = Long.valueOf(0);
        if (trackerRecord.isPresent()) {
            processedRecords = trackerRecord.get().getProcessedRecords();
            fullStopTime = new DateTime(trackerRecord.get().getProcessedTill());
            log.info("Previous copied records" + processedRecords + ". Resuming copying from " + fullStopTime);
        } else
            log.info("Previous copied records not found" + ". Starting copying from " + fullStopTime);
        log.info("Asset= " + assetCode + " Copying range from " + fullStartTime + " to " + fullStopTime);
        DateTime selectStartTime = fullStopTime.minusHours(2);
        DateTime selectStopTime = fullStopTime;
        int previousMonth = selectStopTime.getMonthOfYear();


        while (selectStopTime.isAfter(fullStartTime)) {

            telemetryDataList.clear();
            String query = "from(bucket: \"telemetry-data\")\n" +
                    "  |> range(start: " + fmt.print(selectStartTime) + ", stop: " + fmt.print(selectStopTime) + ")" +
                    "  |> filter(fn: (r) => r[\"asset\"] == \"" + assetCode + "\")" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n" +
                    "  |> pivot(rowKey: [\"_time\"], columnKey: [\"_measurement\"] , valueColumn: \"_value\")";

            List<FluxTable> results = readApi.query(query, "CBC Org");

            for (FluxTable result : results) {
                for (FluxRecord record : result.getRecords()) {
                    processedRecords++;
                    TelemetryData data = adapter.getTelemetryData(record.getValues());
                    if (data != null)
                        telemetryDataList.add(data);
                }
            }

            if (!telemetryDataList.isEmpty()) {
                telemetryDataRepository.saveAll(telemetryDataList);
                telemetryDataRepository.flush();
            }

            trackerRepository.saveAndFlush(new CopyTrackerPLC(assetAbbreviation, processedRecords, new Timestamp( selectStartTime.toInstant().getMillis())));

            if (selectStopTime.getMonthOfYear() != previousMonth) {
                log.info("Finished processing data until :" + selectStartTime + " Records processed=" + processedRecords);
                previousMonth = selectStopTime.getMonthOfYear();
            }
            selectStopTime = selectStartTime.plusSeconds(1);
            selectStartTime = selectStartTime.minusHours(2);

        }
    }

    private Long logAndReturnTotalRecordsToProcess(String assetCode, DateTime processingTimeRangeStart, DateTime
            processingTimeRangeStop) {

        String countQuery = "from(bucket: \"telemetry-data\")" +
                "  |> range(start: " + fmt.print(processingTimeRangeStart) + ", stop: " + fmt.print(processingTimeRangeStop) + ")" +
                "  |> filter(fn: (r) => r[\"asset\"] == \"" + assetCode + "\")" +
                "  |> count()" +
                "  |> yield(name: \"count\")";

        Long totalRecords = Long.valueOf(0);


        try {
            List<FluxTable> countResult = readApi.query(countQuery, "CBC Org");
            if (!countResult.isEmpty()) {
                totalRecords = (Long) countResult.get(0).getRecords().get(0).getValue();
                log.info("Asset= " + assetCode + " processing " + totalRecords + " rows  between " + processingTimeRangeStart + " and " + processingTimeRangeStop);
                return totalRecords;
            }
        } catch (InfluxException e) {
            log.error("No Data in period for asset=" + assetCode + "\t" + processingTimeRangeStart + "\t" + processingTimeRangeStop);
            log.error(e.getMessage());
        }
        return totalRecords;
    }

}

