package com.cbaek.pingpongapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PingPongClient {

    private static final String DEFAULT_HOSTNAME = "ec2-54-209-250-166.compute-1.amazonaws.com";
    private static final int DEFAULT_PORT = 9999;
    private static final String PING_MESSAGE = "ping";

    private final String hostname;
    private final int port;

    private final char[] buffer = new char[25];

    private Socket socket;
    private OutputStreamWriter output;
    private InputStreamReader input;

    public PingPongClient() {
        this.hostname = DEFAULT_HOSTNAME;
        this.port = DEFAULT_PORT;
    }

    private void initialize() throws IOException {
        if (socket == null) {
            socket = new Socket(hostname, port);
            output = new OutputStreamWriter(socket.getOutputStream());
            input = new InputStreamReader(socket.getInputStream());
        }
    }

    public synchronized void sendPingMessage() throws IOException {
        initialize();

        output.write(PING_MESSAGE);
        output.flush();
    }

    public String readServerMessage() throws IOException {
        initialize();

        final int charactersRead = input.read(buffer, 0, buffer.length);
        final String message = new String(buffer, 0, charactersRead);
        return message;
    }

}
