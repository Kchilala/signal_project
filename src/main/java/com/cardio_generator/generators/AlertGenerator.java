package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * Generates alert data for patients in a health monitoring system.
 * This class simulates alert triggering and resolution based on a probabilistic model.
 * Alerts represent critical health events and are handled in a time-sensitive manner.
 *
 * <p>Alerts have a chance of being triggered randomly, and an active alert has a 90% chance
 * of resolving in the next cycle. This class maintains alert state per patient and outputs
 * relevant alert messages using the specified {@link OutputStrategy}.
 *
 * 
 */
public class AlertGenerator implements PatientDataGenerator {
    /**
     * Random generator used for simulating alert triggers and resolutions.
     */
    // changed variable name to camelCase
    public static final Random RANDOM_GENERATOR = new Random();

     /**
     * Tracks alert state for each patient. True means alert is active, false means resolved.
     */
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs a new AlertGenerator with alert states for the specified number of patients.
     *
     * @param patientCount The number of patients being simulated.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alert data for a single patient. If the patient is already in an alert state,
     * there is a 90% chance the alert will be resolved. Otherwise, a new alert may be triggered
     * based on a probability model.
     *
     * @param patientId      The unique ID of the patient.
     * @param outputStrategy The output strategy to which the alert data is sent.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // changed variable name
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
