package TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

public class Client extends Thread{
    private int server_port;
    private String server_name;
    private static String[] users = {"Daniel", "Test", "Dupli Kolbas", "Nikola"};
    private static String[] methods = {"GET", "POST", "DELETE"};
    private static String[] messages = {
            "Hello, world!",
            "Java programming is fun.",
            "How are you today?",
            "Sending random messages.",
            "НИКОЛА!",
            "The quick brown fox jumps over the lazy dog.",
            "Random message generator.",
            "Let's learn Java!",
            "Hello I am Dupli!",
            "Coding is awesome!"
    };
    public Client(int server_port, String server_name) {
        this.server_port = server_port;
        this.server_name = server_name;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            socket = new Socket(InetAddress.getByName(this.server_name), this.server_port);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Random random = new Random();

            String message = String.format("HTTP %s 1.%d /index/users/%s\n",
                    methods[random.nextInt(3)], random.nextInt(5), users[random.nextInt(4)]);

            writer.write(message);
            for (int i = 0; i < 3; i++) {
                String line = messages[random.nextInt(10)];
                writer.write(line + "\n");
            }
            writer.write("\n");
            writer.flush();

            String line;

            while ((line = reader.readLine()) != null){
                System.out.print(String.format("%s\n", line));
            }

            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("SERVER_PORT"));
        String server_name = System.getenv("SERVER_NAME");
        for (int i = 0; i < 50; i++) {
            Client client = new Client(port, server_name);
            client.start();
        }
    }
}
