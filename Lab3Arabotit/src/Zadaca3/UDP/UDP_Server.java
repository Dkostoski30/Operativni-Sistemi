package Zadaca3.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Server extends Thread{
    private DatagramSocket serverSocket;
    private byte[] buffer;

    public UDP_Server(int port) throws SocketException {
        this.serverSocket = new DatagramSocket(port);
        buffer = new byte[256];
    }

    @Override
    public void run() {
        System.out.println("SERVER STARTED");
        DatagramPacket recieved_packet = new DatagramPacket(buffer, buffer.length);
        while (true){
            try {
                //System.out.println("DO OVDE RABOTIT");
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

    public static void main(String[] args) throws SocketException {
        int port = Integer.parseInt(System.getenv("UDP_SERVER_PORT"));
        UDP_Server server = new UDP_Server(port);
        server.start();
    }
}