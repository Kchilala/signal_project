package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * Generates simulated blood saturation data for patients.
 * <p>
 * This class simulates blood saturation levels for a given number of patients. The saturation levels fluctuate
 * within a specified range, and the generated data is then sent to the specified output strategy.
 * </p>
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;
    /**
     * Constructs a BloodSaturationDataGenerator for a specified number of patients.
     * 
     * Initializes baseline blood saturation values for each patient within the range of 95-100%.
     * 
     * @param patientCount The number of patients to generate data for.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }
    /**
     * Generates blood saturation data for a specific patient and sends it to the provided output strategy.
     * <p>
     * The generated saturation value fluctuates by a small variation (between -1, 0, or 1) from the last recorded value,
     * ensuring the saturation remains in the healthy range of 90 to 100%.
     * </p>
     * 
     * @param patientId The unique identifier of the patient.
     * @param outputStrategy The strategy used to output the generated data (e.g., console, file, etc.).
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
