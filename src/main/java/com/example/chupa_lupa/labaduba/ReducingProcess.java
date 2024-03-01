package com.example.chupa_lupa.labaduba;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Random;

public class ReducingProcess {

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;
        Random random = new Random();

        try {
            while (true) {
                try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println("0.1");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000L * random.nextInt(1,5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
