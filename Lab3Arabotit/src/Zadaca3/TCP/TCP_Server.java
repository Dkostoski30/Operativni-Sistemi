package Zadaca3.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Server extends Thread{
    private int port;
    private ServerSocket serverSocket;
    private int num_messages;
    public TCP_Server(int port) throws IOException {
        this.port = port;
        num_messages = 0;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true){
            Socket clientSocket = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;
            int localCounter = 1;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("New client connected..");
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String line = reader.readLine();
                if(line.contains("login")) {
                    writer.write("logged in\n");
                    for (int i = 0; i < 2; i++) {
                        line = reader.readLine();

                        writer.write("echo - " + line + "\n");
                        if (line.contains("logout")){
                            writer.write("Logged out\n");
                        }
                        localCounter++;
                    }
                    writer.flush();
                }else{
                    System.out.println("Client - Unsuccesfull login!");
                }
                synchronized (this){
                    num_messages += localCounter;
                }

                System.out.println("Client disconnected");
                reader.close();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getNum_messages() {
        return num_messages;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TCP_Server server = new TCP_Server(Integer.parseInt(System.getenv("TCP_SERVER_PORT")));
        server.start();

        server.join();
        System.out.println(String.format("Server got %d messages.", server.num_messages));
    }
}

