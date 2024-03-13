package com.cb.telemetry.copyinfluxdata;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Data
@Table(name = "copy_tracker_rp_2024")
public class CopyTrackerRP {


    @Id
    private String assetAbbreviation;

    private Long processedRecords;

    private Timestamp processedTill;

    public CopyTrackerRP(String assetAbbreviation, Long processedRecords, Timestamp processedTill) {
        this.assetAbbreviation = assetAbbreviation;
        this.processedRecords = processedRecords;
        this.processedTill = processedTill;
    }

    public CopyTrackerRP() {

    }
}
