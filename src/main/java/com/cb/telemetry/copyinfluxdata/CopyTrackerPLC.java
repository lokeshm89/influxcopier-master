package com.cb.telemetry.copyinfluxdata;

import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@Entity
@Data
@Table(name = "copy_tracker_plc_2024")
public class CopyTrackerPLC {


    @Id
    private String assetAbbreviation;

    private Long processedRecords;

    private Timestamp processedTill;


    public CopyTrackerPLC(String assetAbbreviation, Long processedRecords, Timestamp processedTill) {
        this.assetAbbreviation = assetAbbreviation;
        this.processedRecords = processedRecords;
        this.processedTill = processedTill;
    }

    public CopyTrackerPLC() {

    }
}
