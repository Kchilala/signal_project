package com.alerts;

// Concrete factory for creating BloodPressureAlert instances
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlertFactory(patientId, condition, timestamp);
    }
}
