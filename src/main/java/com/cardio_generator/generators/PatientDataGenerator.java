package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This interface is called to get basic structure for patient ID and its biometric properties and to get them.
 * These properties are time dependent, they pass over all the metrics contained in the Outputstrategy class, by demand.
 * 
 * */
public interface PatientDataGenerator {
        /**
     * Generates data for a specific patient.
     *
     * @param patientId The unique identifier for the patient.
     * @param outputStrategy The strategy to handle the output of the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
