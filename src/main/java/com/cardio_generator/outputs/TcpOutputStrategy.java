package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
/**
 * A concrete implementation of the {@link OutputStrategy} interface that outputs data over a TCP connection.
 * <p>
 * This class listens for incoming client connections on a specified port, and once a connection is established, 
 * it sends patient data to the client using a TCP connection.
 * </p>
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    /**
     * Constructs a TcpOutputStrategy that listens for client connections on the specified port.
     * 
     * @param port The port on which the TCP server will listen for incoming connections.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Outputs the generated patient data to the connected client over the TCP connection.
     * 
     * @param patientId The unique identifier for the patient.
     * @param timestamp The timestamp when the data was generated.
     * @param label A label or type describing the data (e.g., heart rate, blood pressure).
     * @param data The actual generated data for the patient.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
