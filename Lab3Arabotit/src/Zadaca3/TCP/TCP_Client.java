package Zadaca3.TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class TCP_Client extends Thread{
    private String serverName;
    private int port;

    public Map<String, String[]> poraki = new TreeMap<>();
    public TCP_Client(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        poraki.put("First Message", new String[]{"login Daniel", "a;sfl;lsf", "login User2"});
        poraki.put("Second Message", new String[]{"Hi Server", "logout", "log out from server"});
        poraki.put("Third Message", new String[]{"logout", "logout", "logout Daniel"});
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket(InetAddress.getByName(serverName), port);
            Random random = new Random(); // za sekogas isti rez namesti seed
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

    public static void main(String[] args) {
        String name = System.getenv("TCP_SERVER_NAME");
        int port = Integer.parseInt(System.getenv("TCP_SERVER_PORT"));
        TCP_Client client = new TCP_Client(name, port);
        client.start();
    }
}
