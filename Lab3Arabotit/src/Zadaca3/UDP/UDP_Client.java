package Zadaca3.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDP_Client extends Thread{
    private String serverName;
    private int port;
    private String message;

    private byte[] buffer;
    private DatagramSocket clientSocket;

    public UDP_Client(String serverName, int port, String message) throws SocketException {
        this.serverName = serverName;
        this.port = port;
        this.message = message;
        buffer = message.getBytes();
        clientSocket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            //Scanner scanner = new Scanner(System.in);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverName), port);
            clientSocket.send(packet);
            buffer = new byte[256];
            DatagramPacket recieved_packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverName), port);
            //System.out.println("DO OVDE RABOTIT");
            clientSocket.receive(recieved_packet);
            String server_message = new String(recieved_packet.getData(), 0, recieved_packet.getLength());
            System.out.println("Server Said: " + server_message);
            if (server_message.contains("logged in")){
                System.out.println(server_message);  
                buffer = new byte[256];
                int i = 0;
                while (!server_message.contains("logged out")){
              //      System.out.println("Enter message to server: ");
                    String mess = String.format("Message %d\n", i+1);
                    //String mess = scanner.nextLine();
                    if (i>2){
                        mess += "logout User\n";
                    }
                    packet = new DatagramPacket(mess.getBytes(), mess.getBytes().length, InetAddress.getByName(serverName), port);
                    clientSocket.send(packet);
                    buffer = new byte[256];
                    clientSocket.receive(recieved_packet);
                    server_message = new String(recieved_packet.getData(), 0, recieved_packet.getLength());
                    System.out.printf("Server said: %s\n", server_message);
                    i++;
                }
                System.out.printf("Client logged out..");
            }else{
                System.out.println("Unsuccesfull login..");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SocketException {
        int port = Integer.parseInt(System.getenv("UDP_SERVER_PORT"));
        UDP_Client client = new UDP_Client(System.getenv("UDP_SERVER_NAME"), port, "login Daniel");
        client.start();
    }
}
