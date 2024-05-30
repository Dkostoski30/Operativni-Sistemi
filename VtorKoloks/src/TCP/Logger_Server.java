package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

class LoggerWorker extends Thread {
    private static String filepath;
    Socket client_socket;

    public LoggerWorker(String filepath, Socket client_socket) {
        LoggerWorker.filepath = filepath;
        this.client_socket = client_socket;
    }

    @Override
    public void run() {
        shareLog(client_socket);
    }

    private static synchronized void shareLog(Socket client_socket) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true));
            BufferedReader reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

            writer.write(String.format("[%s] %s %s", LocalTime.now(), client_socket.getInetAddress(), reader.readLine()));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Logger_Server extends Thread {
    int port;
    String filepath;

    public Logger_Server(int port, String filepath) {
        this.port = port;
        this.filepath = filepath;
    }

    @Override
    public void run() {
        try {
            ServerSocket server_socket = new ServerSocket(port);
            System.out.println("Logger Server started on port " + port);
            while (true) {
                Socket client_socket = server_socket.accept();
                System.out.println("New connection from " + client_socket.getInetAddress());
                new LoggerWorker(filepath, client_socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String portEnv = System.getenv("LOGGER_PORT");
        String filepath = System.getenv("FILE_PATH");

        System.out.println("LOGGER_PORT: " + portEnv);
        System.out.println("FILE_PATH: " + filepath);

        if (portEnv == null || filepath == null) {
            System.err.println("Environment variables LOGGER_PORT or FILE_PATH not set.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portEnv);
        } catch (NumberFormatException e) {
            System.err.println("Invalid LOGGER_PORT value: " + portEnv);
            return;
        }

        Logger_Server loggerServer = new Logger_Server(port, filepath);
        loggerServer.start();
    }
}
