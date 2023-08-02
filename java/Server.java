import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private List<PrintWriter> clientWriters;

    public static void main(String[] args) {
        new Server().run();
    }

    public Server() {
        clientWriters = new ArrayList<>();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is running and waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                new Thread(new ClientHandler(clientSocket, writer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket, PrintWriter writer) {
            this.clientSocket = clientSocket;
            this.writer = writer;
        }

        @Override
        public void run() {
            try {
                Scanner reader = new Scanner(clientSocket.getInputStream());
                while (reader.hasNextLine()) {
                    String message = reader.nextLine();
                    System.out.println("Received: " + message);

                    // Broadcast the message to all connected clients
                    for (PrintWriter clientWriter : clientWriters) {
                        clientWriter.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    clientWriters.remove(writer);
                }
            }
        }
    }
}
