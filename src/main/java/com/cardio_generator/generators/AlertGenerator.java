package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert data for patients.
 * <p>
 * This class simulates the occurrence of alerts for patients. The alerts can either be triggered or resolved,
 * and the state of the alerts is updated and sent to the specified output strategy.
 * </p>
 */
public class AlertGenerator implements PatientDataGenerator {

  public static final Random randomGenerator = new Random();
  
  private boolean[] alertStates; // false = resolved, true = pressed
  // change method lower camel case
  /**
   * Constructs an AlertGenerator for a specified number of patients.
   * <p>
   * Initializes the alert states for each patient to false (resolved).
   * </p>
   * 
   * @param patientCount The number of patients to generate alert data for.
   */
  public AlertGenerator(int patientCount) { 
    alertStates = new boolean[patientCount + 1];
  }

  /**
   * Generates alert data for a specific patient and sends it to the provided output strategy.
   * <p>
   * The alert can either be resolved with a 90% probability if it was previously triggered,
   * or it can be triggered based on a Poisson distribution with a specified rate (Lambda).
   * </p>
   * 
   * @param patientId The unique identifier of the patient.
   * @param outputStrategy The strategy used to output the generated data (e.g., console, file, etc.).
   */
  @Override
  public void generate(int patientId, OutputStrategy outputStrategy) {
    try {
      if (alertStates[patientId]) {
        if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
          alertStates[patientId] = false;
          // Output the alert
          outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
        }
      } else {
          double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
          double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
          boolean alertTriggered = randomGenerator.nextDouble() < p;
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
