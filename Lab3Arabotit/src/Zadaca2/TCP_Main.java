

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

class Server extends Thread{
    private int port;
    private ServerSocket serverSocket;
    private int num_messages;
    public Server(int port) throws IOException {
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
}
class Client extends Thread{
    private String serverName;
    private int port;

    public static Map<String, String[]> poraki = new TreeMap<>();
    public Client(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket(InetAddress.getByName(serverName), port);
            Random random = new Random(1); // za sekogas isti rez namesti seed
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            writer.write(String.format("%s\n", poraki.get("First Message")[random.nextInt(3)]));
            writer.write(String.format("%s\n", poraki.get("Second Message")[random.nextInt(3)]));
            writer.write(String.format("%s\n", poraki.get("Third Message")[random.nextInt(3)]));
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            for (String line: reader.lines().toList()){
                System.out.println("Server said: " + line);
            }
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
public class TCP_Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client.poraki.put("First Message", new String[]{"login Daniel", "a;sfl;lsf", "login User2"});
        Client.poraki.put("Second Message", new String[]{"Hi Server", "logout", "log out from server"});
        Client.poraki.put("Third Message", new String[]{"logout", "logout", "logout Daniel"});
        //int port = Integer.parseInt(System.getenv("SERVER_PORT"));
        int port = 7000;
        Server server = new Server(port);
        server.start();
        for (int i = 0; i < 40; i++) {
            Client client = new Client("localhost", port);
            client.start();
        }
        Thread.sleep(7000);
        System.out.println("Server got " + server.getNum_messages() + " messages");
    }
}
