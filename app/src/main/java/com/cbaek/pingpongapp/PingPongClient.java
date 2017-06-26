package com.cbaek.pingpongapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PingPongClient {

    private static final String PING_PONG_SERVER_HOST = "ec2-54-209-250-166.compute-1.amazonaws.com";
    private static final int PING_PONG_SERVER_PORT = 9999;
    private static final String PING_PONG_SERVER_MESSAGE = "ping";

    private final String hostName;
    private final int port;

    private final char[] buffer = new char[25];

    private Socket socket;
    private OutputStreamWriter output;
    private InputStreamReader input;

    public PingPongClient() {
        this(PING_PONG_SERVER_HOST, PING_PONG_SERVER_PORT);
    }

    public PingPongClient(final String hostName, final int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public void initialize() throws IOException {
        socket = new Socket(hostName, port);
        output = new OutputStreamWriter(socket.getOutputStream());
        input = new InputStreamReader(socket.getInputStream());
    }

    public void sendPingMessage() throws IOException {
        output.write(PING_PONG_SERVER_MESSAGE);
        output.flush();
    }

    public String readServerMessage() throws IOException {
        final int charactersRead = input.read(buffer, 0, buffer.length);
        final String message = new String(buffer, 0, charactersRead);
        return message;
    }

}
