package TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
class Worker extends Thread{
    Socket client_socket;

    public Worker(Socket client_socket) {
        this.client_socket = client_socket;
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            String logger_name = System.getenv("LOGGER_NAME");
            int logger_port = Integer.parseInt(System.getenv("LOGGER_PORT"));
            Socket socket = new Socket(InetAddress.getByName(logger_name), logger_port);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            String line = reader.readLine();
            Request client_request = Request.create(line);
            shareLog(client_request, socket);
            //System.out.println("DO OVDE RABOTIT");
            writer.flush();
            reader.close();
            writer.close();
            client_socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static synchronized void shareLog(Request request, Socket client_socket){
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client_socket.getOutputStream()));
            writer.write(String.format("[%s] %s %s: HTTP %s %s\n", LocalTime.now(), client_socket.getInetAddress(), request.user, request.method, request.version));
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static class Request{
        private String method;
        private String version;
        private String user;

        public Request(String method, String version, String user) {
            this.method = method;
            this.version = version;
            this.user = user;
        }

        public String getMethod() {
            return method;
        }

        public String getVersion() {
            return version;
        }

        public String getUser() {
            return user;
        }
        public static Request create(String line){
            String[] lines = line.split("\\s+");
            String method = lines[1];
            String version = lines[2];
            String user = lines[3].split("/")[3];

            return new Request(method, version, user);
        }
    }
}


public class Server extends Thread{
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket server_socket = new ServerSocket(port);
            System.out.println("Server started..");
            while (true){
                Socket client_socket = server_socket.accept();
                System.out.println("New connection established.");
                new Worker(client_socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("SERVER_PORT"));
        Server server = new Server(port);
        server.start();
    }
}
