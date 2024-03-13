package com.cb.telemetry.copyinfluxdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlcCopyTrackerRepository extends JpaRepository<CopyTrackerPLC , String> {
}
