package com.cb.telemetry.copyinfluxdata;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Lokesh Venkatesan
 * Configuration class to create a connection with InFluxDB
 */

@Configuration
@PropertySource("classpath:influx/application-dev.properties")
public class InfluxConfigSource {
    @Value("${bsm.influx.host.source}")
    private String influxDBHost;

    @Value("${bsm.influx.port.source}")
    private String influxDBPort;

    @Value("${bsm.influx.token.source}")
    private String readToken;

    @Bean
    public InfluxDBClient influxClientSource (){

        String URL = String.format("http://%s:%s", influxDBHost, influxDBPort);
        return InfluxDBClientFactory.create(URL, readToken.toCharArray());
    }
}
