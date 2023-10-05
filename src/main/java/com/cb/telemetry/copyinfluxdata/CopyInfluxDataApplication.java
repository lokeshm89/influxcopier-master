package com.cb.telemetry.copyinfluxdata;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = {"com.cb.telemetry.copyinfluxdata"})

public class CopyInfluxDataApplication {


    private final CopyPlcDataService copyPlcDataService;
    private final ConfigurationMap configurationMap;
    private final CopyRpDataService copyRpDataService;

    public static void main(String[] args) {
        SpringApplication.run(CopyInfluxDataApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startCopying() {

        HashMap<String, String> assets = configurationMap.getAssets();

        for (String assetId : assets.keySet()) {
            copyRpDataService.copy(assetId,assets.get(assetId));
            //copyPlcDataService.copyDataOfAsset(assetId, assets.get(assetId));
        }
    }
}
