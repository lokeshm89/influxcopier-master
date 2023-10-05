package com.cb.telemetry.copyinfluxdata;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.Instant;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "iot_values_rp_2023")
@DynamicUpdate
public class RosePointData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Instant time;
    private String assetId;
    private Double air_temperature;
    private Double apparent_wind_angle;
    private Double apparent_wind_speed;
   private Double barometric_pressure;
    private Double course_over_ground;
    private Double hdop;
    private Double heading_true;
    private Double lateral_speed_bow;
   private Double lateral_speed_stern;
   private Double longitudinal_speed;
   private Double rate_of_turn;
    private Boolean real_time;
    private Double river_mile;
    private String river_code ;
    private String position;
    private Double speed_over_ground;
    private Double vessel_airdraft;
    private Double vessel_draft;
    private Double vessel_beam;
    private Double vessel_length;



public boolean setProperty(String propertyName, Object value) {
        try {
            Field field = getClass().getDeclaredField(propertyName.replace('-', '_').trim());
            field.setAccessible(true);
            if(value == null)
                field.set(this, null);
            else if(value.toString().equalsIgnoreCase("true"))
                field.set(this, Boolean.TRUE);
            else if (value.toString().equalsIgnoreCase("false"))
                field.set(this, Boolean.FALSE);
            else
            field.set(this, value);
            return  true;
        } catch (NoSuchFieldException e) {
            log.error("Field not found: " + propertyName);
        } catch (IllegalAccessException e) {
            log.error("Error setting field: " + propertyName, e);
        }
            return false;
        }

}
