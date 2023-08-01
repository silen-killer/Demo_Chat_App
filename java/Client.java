import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String host;
    private int port;
    private Socket clientSocket;
    private PrintStream output;

    private static Client instance;

    private Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Client getInstance(String host, int port) throws IOException {
        if (instance == null) {
            instance = new Client(host, port);
            instance.connect();
        }
        return instance;
    }

    private void connect() throws IOException {
        clientSocket = new Socket(host, port);
        output = new PrintStream(clientSocket.getOutputStream());
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client.getInstance("localhost", 5000);
    }
}
