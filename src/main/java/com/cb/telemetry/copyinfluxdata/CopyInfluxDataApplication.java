package com.cb.telemetry.copyinfluxdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = {"com.cb.telemetry.copyinfluxdata"})
@Slf4j
public class CopyInfluxDataApplication {


    private final CopyPlcDataService copyPlcDataService;
    private final ConfigurationMap configurationMap;
    private final CopyRpDataService copyRpDataService;

    @Value("${bucket}")
    private final String bucketToProcess;

    public static void main(String[] args) {
        SpringApplication.run(CopyInfluxDataApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startCopying() {

        HashMap<String, String> assets = configurationMap.getAssets();

        if (bucketToProcess.equals("telemetry-data")){

            for (String assetId : assets.keySet()) {
                log.info("Beginning to process telemetry-data of asset="+ assetId);
                //copyPlcDataService.copy(assetId, assets.get(assetId));
                log.info("Completed processing telemetry-data of asset="+ assetId);
            }
        }
        else if (bucketToProcess.equals("rosepoint-data")) {
            for (String assetId : assets.keySet()) {
                log.info("Beginning to process telemetry-data of asset="+ assetId);
                copyRpDataService.copy(assetId, assets.get(assetId));
                log.info("Completed processing telemetry-data of asset="+ assetId);
            }
        }
    }
}
