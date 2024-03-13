package com.cb.telemetry.copyinfluxdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntityAdapter {
    public final RosePointDataRepository rosePointDataRepository;

    private final ConfigurationMap configurationMap;

    @Nullable
    public TelemetryData getTelemetryData(Map<String, Object> values) {

        values.remove("result");
        values.remove("table");
        values.remove("_start");
        values.remove("_stop");
        values.remove("_field");
        values.put("time", values.get("_time"));
        values.put("assetId", configurationMap.getAssets().get(values.get("asset")));
        values.remove("_time");
        values.remove("asset");


        TelemetryData data = new TelemetryData();

        try {

            for (String key : values.keySet()) {
                boolean success = data.setProperty(key.replace('-', '_').trim(), values.get(key));
//                if (!success)
//                    log.error("Missing influx property=" + key.replace('-', '_').trim());
            }
            return data;
        } catch (Exception e) {
            log.error(e.getMessage() + "asset=" + values.get("assetId") + "time=" + values.get("time"));
        }
        return null;
    }

    public RosePointData getRosePointData(Map<String, Object> values) {
        values.remove("result");
        values.remove("table");
        values.remove("_start");
        values.remove("_stop");
        values.remove("_field");
        values.put("time", values.get("_time"));
        values.put("assetId", configurationMap.getAssets().get(values.get("asset")));
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

        try {

            for (String key : values.keySet()) {
                boolean success = data.setProperty(key.replace('-', '_').trim(), values.get(key));
//                if (!success)
//                    log.error("Missing influx property=" + key.replace('-', '_').trim());
            }
            return data;
        } catch (Exception e) {
            log.error(e.getMessage() + "asset=" + values.get("assetId") + "time=" + values.get("time"));
        }
        return null;
    }
}
