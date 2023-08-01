import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private final int port;
    private Set<PrintWriter> clientWriters = new HashSet<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(writer);
            new ClientHandler(clientSocket, writer).start();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket, PrintWriter writer) throws IOException {
            this.socket = socket;
            this.writer = writer;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    sendToAllClients(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientWriters.remove(writer);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendToAllClients(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
