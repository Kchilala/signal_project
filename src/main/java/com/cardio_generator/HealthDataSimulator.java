package com.cardio_generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
/**
*
* @author Lars 
* @author Keci
*/

public class HealthDataSimulator {
    /**
     * These are the variable parameters at the beginning of the method and set the conditions for information stream and handeling.
     * 
     * @param patientCount this gives minimum number of patients this is set at 50 or more.
     * @param scheduler this schedules patients (where the method overloads: methods can handle 1 to 5 parameters in method).
     * @param outputStrategy this variable contains the continous stream of data for patients in realtime (patientId, timestamp, label, data).
     * @param random creates random numbers between for example random(5) 1 and 4 that are equally likely.
     * */
    private static int patientCount = 50; // Default number of patients
    private static ScheduledExecutorService scheduler;
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy(); // Default output strategy
    private static final Random random = new Random();

    /**
     * the method helps retrieving info but also helps randomizing order of patients for fairness and schedules task.
     * <p>
     * the main method takes an String array variable named args and it contains array of strings that are typed in by operator.
     * these strings are compared to inputs like "-h","--patient-count","--output" or if nothing is recognized 
     * we define a default option and return this : "Unknown option '" + args[i] + "'".
     * it then prints out the respective cases and there information.
     * 
     * @param args these are command line argument for inputs like "-h","--patient-count","--output" or 
     *            if nothing is recognized we define a default option and return this : "Unknown option '" + args[i] + "'".
     * @throws IOException throws error when reading from args input went wrong and is not a String for example.
     * */
    public static void main(String[] args) throws IOException {

        parseArguments(args);

        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds);
    }
    /**
     *
     * 
     * @param args these are command line argument for inputs like "-h","--patient-count","--output" or 
     *            if nothing is recognized we define a default option and return this : "Unknown option '" + args[i] + "'".
     * @throws IOException IOException If an error occurs while creating output directories or initializing output strategies.
     * */

    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err  
                                .println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                // Initialize your WebSocket output strategy here
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                // Initialize your TCP socket output strategy here
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }
    /**
    * Prints the usage instructions for the HealthDataSimulator application.
    * <p>
    * This method outlines the command-line options available to the user, including:
    * <ul>
    *   <li><code>-h</code>: Displays the help message and exits the application.</li>
    *   <li><code>--patient-count &lt;count&gt;</code>: Specifies the number of patients to simulate data for. Defaults to 50 if not provided.</li>
    *   <li><code>--output &lt;type&gt;</code>: Determines the output method. Supported types include:
    *     <ul>
    *       <li><code>console</code>: Outputs data to the console.</li>
    *       <li><code>file:&lt;directory&gt;</code>: Outputs data to files within the specified directory.</li>
    *       <li><code>websocket:&lt;port&gt;</code>: Outputs data to WebSocket clients connected to the given port.</li>
    *       <li><code>tcp:&lt;port&gt;</code>: Outputs data to TCP clients connected to the specified port.</li>
    *     </ul>
    *   </li>
    * </ul>
    * <p>
    * An example usage is also displayed:
    * <pre>
    * java HealthDataSimulator --patient-count 100 --output websocket:8080
    * </pre>
    * This example simulates data for 100 patients and streams it to WebSocket clients on port 8080.
    */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println(
                "  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }
    /**
     * Initializes a list of patient IDs from 1 to the specified patient count(default set at 50).
     * <p>
     * This method creates a list of integers representing patient IDs, starting from 1 up to the specified
     * number of patients. It is useful for assigning unique identifiers to a set of patients in the simulation.
     *
     * @param patientCount The total number of patients to generate IDs for.
     * @return A list of integers representing patient IDs from 1 to {@code patientCount}.
     */
    private static List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    private static void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }
    /**
    * Schedules a task to run at a fixed rate with a random initial delay.
    * <p>
    * This method schedules the specified task to be executed periodically at a fixed rate, with an initial delay
    * that is randomly selected from the range [0, 4] seconds. The task will continue to execute at the specified
    * interval until canceled.
    *
    * @param task The task to be scheduled for periodic execution. This should be a {@link Runnable} that contains
    *             the code to be executed.
    * @param period The period between successive executions of the task.
    * @param timeUnit The time unit of the {@code period} argument, such as {@link TimeUnit#SECONDS}, {@link TimeUnit#MILLISECONDS}, etc.
    */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}
