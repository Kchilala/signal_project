package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * A concrete implementation of the {@link PatientDataGenerator} interface that simulates blood saturation data
 * for patients in a health monitoring system.
 *
 * <p>This class generates simulated blood saturation levels for each patient within a realistic range (90% to 100%),
 * with slight fluctuations introduced over time to mimic natural variations in blood oxygen saturation.
 * The generated data is then sent to the specified output strategy.
 * 
 * @see PatientDataGenerator
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Constructs a {@link BloodSaturationDataGenerator} with initial saturation values for a specified number of patients.
     * Each patient is initialized with a blood saturation value between 95% and 100%.
     *
     * @param patientCount The number of patients being simulated.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates simulated blood saturation data for a single patient. The blood saturation value fluctuates slightly
     * within a realistic range (90% to 100%), and the updated value is sent to the specified output strategy.
     *
     * @param patientId      The unique identifier of the patient whose blood saturation data is being generated.
     * @param outputStrategy The output strategy to which the simulated data is sent.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
