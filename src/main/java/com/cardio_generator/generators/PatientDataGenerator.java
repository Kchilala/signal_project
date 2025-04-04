package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * Interface for generating patient health data.
 * Implementing classes simulate specific types of medical data (e.g., ECG, blood pressure)
 * and output the generated data using a provided {@link OutputStrategy}.
 *
 * <p>This abstraction allows different types of data to be generated consistently for each patient,
 * making it easier to plug in new data generators into the simulation framework.
 * 
 * @see com.cardio_generator.outputs.OutputStrategy
 */
public interface PatientDataGenerator {
    /**
     * Generates data for a specific patient and outputs it using the given strategy.
     *
     * @param patientId      The unique identifier of the patient.
     * @param outputStrategy The strategy used to output the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
