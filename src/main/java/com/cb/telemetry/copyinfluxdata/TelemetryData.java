package com.cb.telemetry.copyinfluxdata;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "iot_values_plc_2024")
@DynamicUpdate
public class TelemetryData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Instant time;

    private String assetId;
    private Double alarmSystemBattery;
    private Double dailyFuelBurn;
    private Double engineRoomSpareBattery;
    private Double flankingBatteries;
    private Double fuelTakenOn;
    private Double gen_AlarmBatteries;
    private Double generatorAmp_a;
    private Double generatorAmp_b;
    private Double generatorAmp_c;

    private Double generatorBussVolt_AB;
    private Double generatorBussVolt_AC;
    private Double generatorBussVolt_BC;
    private Double generatorFrequency_hz;
    private Double generatorPower_kw;
    private Double mainAirPressure;
    private Double pgeBatteries;
    private Double pmeBatteries;
    private Double portFuelTank;
    private Double portGeneratorFuelPressure;
    private Double portGeneratorHours;
    private Double portGeneratorOilPressure;
    private Double portGeneratorWaterTemperature;
    private Double portLoadOfPercentage;
    private Double portMainAftercoolWaterPressure;
    private Double portMainAftercoolWaterTemperature;
    private Double portMainCrankcasePressure;
    private Double portMainCylinderTemp_1;
    private Double portMainCylinderTemp_10;
    private Double portMainCylinderTemp_11;
    private Double portMainCylinderTemp_12;
    private Double portMainCylinderTemp_2;
    private Double portMainCylinderTemp_3;
    private Double portMainCylinderTemp_4;
    private Double portMainCylinderTemp_5;
    private Double portMainCylinderTemp_6;
    private Double portMainCylinderTemp_7;
    private Double portMainCylinderTemp_8;
    private Double portMainCylinderTemp_9;
    private Double portMainFuelOilPressure;
    private Double portMainHourMeter;
    private Double portMainLeftBankWaterPressure;
    private Double portMainOilPressure;
    private Double portMainOilTemperature;
    private Double portMainPistonCoolPsi;
    private Double portMainRightBankWaterPressure;
    private Double portMainRpm;
    private Double portMainTurboOilPressure;
    private Double portMainWaterPressure;
    private Double portMainWaterTemperature;
    private Double portReductionGearClutchAirPressure;
    private Double portReductionGearOilPressure;
    private Double portReductionGearOilTemperature;
    private Double portReductionGearWaterPressure;
    private Double propulsion1battery;
    private Double propulsion2battery;
    private Double sgeBatteries;
    private Double smeBatteries;
    private Double stbdFuelTank;
    private Double stbdGeneratorFuelPressure;
    private Double stbdGeneratorHours;
    private Double stbdGeneratorOilPressure;
    private Double stbdGeneratorWaterTemperature;
    private Double stbdLoadOfPercentage;
    private Double stbdMainAftercoolWaterPressure;
    private Double stbdMainAftercoolWaterTemperature;
    private Double stbdMainCrankcasePressure;
    private Double stbdMainCylinderTemp_1;
    private Double stbdMainCylinderTemp_10;
    private Double stbdMainCylinderTemp_11;
    private Double stbdMainCylinderTemp_12;
    private Double stbdMainCylinderTemp_2;
    private Double stbdMainCylinderTemp_3;
    private Double stbdMainCylinderTemp_4;
    private Double stbdMainCylinderTemp_5;
    private Double stbdMainCylinderTemp_6;
    private Double stbdMainCylinderTemp_7;
    private Double stbdMainCylinderTemp_8;
    private Double stbdMainCylinderTemp_9;
    private Double stbdMainFuelOilPressure;
    private Double stbdMainHourMeter;
    private Double stbdMainLeftBankWaterPressure;
    private Double stbdMainOilPressure;
    private Double stbdMainOilTemperature;
    private Double stbdMainPistonCoolPsi;
    private Double stbdMainRightBankWaterPressure;
    private Double stbdMainRpm;
    private Double stbdMainTurboOilPressure;
    private Double stbdMainWaterPressure;
    private Double stbdMainWaterTemperature;
    private Double stbdReductionGearClutchAirPressure;
    private Double stbdReductionGearOilPressure;
    private Double stbdReductionGearOilTemperature;
    private Double stbdReductionGearWaterPressure;
    private Double steeringBattery;
    private Double steeringRai;
    private Double switchgearBatteries;
    private Double vhfBattery;
    private Double wheelhouseBattery1;
    private Double wheelhouseBattery2;
    private Double wheelhouseBattery3;

    public boolean setProperty(String propertyName, Object value) {
        try {
            Field field = getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(this, value);
            return  true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

}
