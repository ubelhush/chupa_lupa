package com.example.chupa_lupa.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFactory {
    int port;
    public ServerFactory(int port){
        this.port=port;
    }
    public void acceptResultFromClient(Receiveable onReceive){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                onReceive.onReceive(processClientRequest(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient(String clientPath) throws IOException {
        File jarFile = new File(clientPath);

        if (jarFile.exists()) {
            Runtime runtime = Runtime.getRuntime();
            String command = "java -jar " + clientPath;
            Process process = runtime.exec(command);
        } else {
            System.out.println("Файл JAR не существует.");
        }
    }

    private String processClientRequest(Socket clientSocket) throws IOException {
        String result;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            result = reader.readLine();
        } finally {
            clientSocket.close();
        }

        return result;
    }

    public interface Receiveable{
        void onReceive(String result);
    }
}
