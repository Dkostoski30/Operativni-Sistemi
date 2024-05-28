package Zadaca1;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.Set;

class Client extends Thread{
    private String serverName;
    private int port;
    private String message;

    private byte[] buffer;
    private DatagramSocket clientSocket;

    public Client(String serverName, int port, String message) throws SocketException {
        this.serverName = serverName;
        this.port = port;
        this.message = message;
        buffer = message.getBytes();
        clientSocket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverName), port);
            clientSocket.send(packet);
            buffer = new byte[256];
            DatagramPacket recieved_packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverName), port);
            clientSocket.receive(recieved_packet);
            String server_message = new String(recieved_packet.getData(), 0, recieved_packet.getLength());
            if (server_message.contains("logged in")){
                System.out.println(server_message);
                buffer = new byte[256];
                while (!server_message.contains("logged out")){
                    System.out.println("Enter message to server: ");
                    String mess = scanner.next();
                    packet = new DatagramPacket(mess.getBytes(), mess.getBytes().length, InetAddress.getByName(serverName), port);
                    clientSocket.send(packet);
                    buffer = new byte[256];
                    clientSocket.receive(recieved_packet);
                    server_message = new String(recieved_packet.getData(), 0, recieved_packet.getLength());
                    System.out.printf("Server said: %s\n", server_message);
                }
                System.out.printf("Client logged out..");
            }else{
                System.out.println("Unsuccesfull login..");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class Server extends Thread{
    private DatagramSocket serverSocket;
    private byte[] buffer;

    public Server(int port) throws SocketException {
        this.serverSocket = new DatagramSocket(port);
        buffer = new byte[256];
    }

    @Override
    public void run() {
        DatagramPacket recieved_packet = new DatagramPacket(buffer, buffer.length);
        while (true){
            try {
                serverSocket.receive(recieved_packet);
                buffer = new byte[256];
                String mess = new String(recieved_packet.getData(), 0, recieved_packet.getLength());
                String response;
                if (mess.contains("login")){
                    response = "logged in";
                }else if(mess.contains("logout")){
                    response = "logged out";
                }else{
                    response = String.format("echo - %s", mess);
                }
                DatagramPacket send_packet = new DatagramPacket(response.getBytes(), response.getBytes().length, recieved_packet.getAddress(), recieved_packet.getPort());
                serverSocket.send(send_packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
public class UDP_Main {
    public static void main(String[] args) throws SocketException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter message to server: ");
        String mess = scanner.next();
        Server server = new Server(6000);
        Client client = new Client("localhost", 6000, mess);
        server.start();
        client.start();
    }
}
