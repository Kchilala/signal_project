package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
/**
 * A concrete implementation of the {@link OutputStrategy} interface that sends patient data over a TCP socket.
 * This strategy listens on a specified port for client connections and sends data to the connected client.
 * 
 * <p>The class sets up a TCP server and accepts a client connection. Once a client connects,
 * the data generated for a patient is sent as a comma-separated message through the TCP connection.
 * This allows the patient data to be transferred in real-time over the network.
 *
 * @see OutputStrategy
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a {@link TcpOutputStrategy} that listens for incoming connections on the specified port.
     *
     * @param port The port number on which the TCP server will listen for incoming connections.
     * @throws IOException If there is an error in creating the server socket or handling the client connection.
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
     * Sends the patient data over the TCP connection to the connected client.
     * 
     * @param patientId The unique identifier of the patient whose data is being sent.
     * @param timestamp The timestamp when the data was generated.
     * @param label     The label that identifies the type of data (e.g., "Alert", "ECG").
     * @param data      The actual patient data that needs to be sent.
     * @throws NullPointerException if the client socket is not yet established.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
