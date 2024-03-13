package com.cb.telemetry.copyinfluxdata;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DateConfig {
    @Value("${bsm.fullStartTime}")
    private String fullStartTime;

    @Value("${bsm.fullStopTime}")
    private String fullStopTime;


}
