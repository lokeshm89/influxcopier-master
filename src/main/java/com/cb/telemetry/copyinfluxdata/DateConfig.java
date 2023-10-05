package com.cb.telemetry.copyinfluxdata;

import lombok.Getter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DateConfig {
    @Value("${fullStartTime}")
    private String fullStartTime;

    @Value("${fullEndTime}")
    private String fullEndTime;
public  DateConfig(){
    this.fullStartTime= String.valueOf(DateTime.now());
}
}
