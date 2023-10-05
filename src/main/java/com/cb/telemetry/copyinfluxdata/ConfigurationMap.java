package com.cb.telemetry.copyinfluxdata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author lokeshvenkatesan
 */

@Component
@Slf4j

public class ConfigurationMap {
    @Getter
    private HashMap<String, String> v2ToV3Map = new HashMap<>();

    @Getter
    private HashMap<String, String> assets = new HashMap<>();
    @Getter
    private List<String> rpMeasurementNamesList = new ArrayList<>();

    @Getter
    private List<String> plcMeasurementNamesList = new ArrayList<>();
    public ConfigurationMap() {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/V2vsV3.csv")));
        String[] values;
        String line ;
        try {

            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                values = line.split(",");
                String key = (values[0] + values[1])
                        .toLowerCase(Locale.ROOT)
                        .replace("_", "")
                        .replace("-", "");
                v2ToV3Map.put(key, values[2]);
            }

        } catch (Exception e) {

            log.debug("Unable to load V2vsV3 configuration" + e.getClass().getSimpleName());
            System.exit(1);
        }

        bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/PlcMeasurementName")));
        line = "";
        try {

            while ((line = bufferedReader.readLine()) != null) {
                plcMeasurementNamesList.add(line);
            }

        } catch (Exception e) {
            log.debug("Unable to load plc MeasurementNames configuration" + e.getClass().getSimpleName());
            System.exit(1);
        }


        bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/RosePointMeasurementNames")));
        line = "";
        try {

            while ((line = bufferedReader.readLine()) != null) {
                rpMeasurementNamesList.add(line);
            }

        } catch (Exception e) {
            log.debug("Unable to load rose point MeasurementNames configuration" + e.getClass().getSimpleName());
            System.exit(1);
        }


        bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Assets.csv")));
        line = "";
        try {

            while ((line = bufferedReader.readLine()) != null) {
                values = line.split(",");
                assets.put(values[0], values[1]);

            }

        } catch (Exception e) {
            log.debug("Unable to load MeasurementNames configuration" + e.getClass().getSimpleName());
            System.exit(1);
        }

    }
}
