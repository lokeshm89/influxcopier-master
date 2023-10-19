package com.cb.telemetry.copyinfluxdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntityAdapter {
public final RosePointDataRepository rosePointDataRepository;

    public TelemetryData getTelemetryData(Map<String, Object> values) {

        values.remove("result");
        values.remove("table");
        values.remove("_start");
        values.remove("_stop");
        values.remove("_field");
        values.put("time", values.get("_time"));
        values.put("assetId", values.get("asset"));
        values.remove("_time");
        values.remove("asset");


        TelemetryData data = new TelemetryData();

        for (String key : values.keySet()) {
            boolean success = data.setProperty(key.replace('-','_').trim(), values.get(key));
            if (!success)
                log.error("Unable to find matching postgres column for influx property=" + key.replace('-','_').trim());
        }
        return data;
    }
    public RosePointData getRosePointData(Map<String, Object> values) {
        values.remove("result");
        values.remove("table");
        values.remove("_start");
        values.remove("_stop");
        values.remove("_field");
        values.put("time", values.get("_time"));
        values.put("assetId", values.get("asset"));
//        values.put("airTemperature",values.get("air-temperature"));
//        values.put("apparentWindSpeed",values.get("apparent-wind-speed"));
//        values.put("headingTrue",values.get("heading-true"));
//        values.put("apparentWindAngle",values.get("apparent-wind-angle"));
//        values.put("barometricPressure",values.get("barometric-pressure"));
//        values.put("courseOverGround",values.get("course-over-ground"));
//        values.put("lateralSpeedBow",values.get("lateral-speed-bow"));
//        values.put("lateralSpeedStern",values.get("lateral-speed-stern"));
//        values.put("longitudinalSpeed",values.get("longitudinal-speed"));
//        values.put("rateOfTurn",values.get("rate-of-turn"));
//        //values.put("realTime",values.get("real-time"));
//        values.put("riverCode",values.get("river-code"));
//        values.put("riverMile",values.get("river-mile"));
//        values.put("speedOverGround",values.get("speed-over-ground"));
//        values.put("vesselAirdraft",values.get("vessel-airdraft"));
//        values.put("vesselBeam",values.get("vessel-beam"));
//        values.put("vesselDraft",values.get("vessel-draft"));
//        values.put("vesselLength",values.get("vessel-length"));
        values.remove("_time");
        values.remove("asset");

        RosePointData data = new RosePointData();

        for (String key : values.keySet()) {
            String propertyName = key.replace('-', '_').trim();
//            log.info("Mapping InfluxDB property: " + key.replace('-', '_').trim() + " to entity property: " + propertyName);
//            log.info("Value from InfluxDB: " + values.get(key));

            boolean success = data.setProperty(propertyName, values.get(key));
            if (!success)
                log.error("Unable to find matching postgres column for influx property=" + key.replace('-','_').trim());
        }
        rosePointDataRepository.save(data);

        return data;
    }
}
