package com.cardio_generator.outputs;
/**
 * Interface for defining output strategies for patient data.
 * 
 * This interface provides the contract for outputting generated patient data
 * in different formats or systems, such as console, files, or network.
 */
public interface OutputStrategy {
    /**
     * Outputs the generated patient data.
     *
     * @param patientId The unique identifier for the patient.
     * @param timestamp The timestamp when the data was generated.
     * @param label A label or type describing the data (e.g., heart rate, blood pressure).
     * @param data The actual generated data for the patient.
     */
    void output(int patientId, long timestamp, String label, String data);
}
