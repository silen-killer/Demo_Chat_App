import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class WhatsAppGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton encryptButton;
    private Socket clientSocket;
    private Scanner reader;
    private PrintWriter writer;
    private String username;
    private boolean isEncrypted;

    // Constructor
    public WhatsAppGUI(String host, int port) {
        username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous";
        }

        // Set up the GUI components
        setTitle("WhatsApp");
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);
        Font messageFont = new Font("Arial", Font.PLAIN, 16);
        messageField = new JTextField();
        messageField.setFont(messageFont);
        sendButton = new JButton("Send");
        sendButton.addActionListener(new SendMessageListener());

        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(new EncryptMessageListener());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(encryptButton, BorderLayout.WEST);
        add(inputPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);

        try {
            // Establish connection with the server
            clientSocket = new Socket(host, port);
            reader = new Scanner(clientSocket.getInputStream());
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Start a new thread to listen for messages from the server
            new Thread(new ReceivedMessagesHandler()).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messageField.getText();
            if (!message.trim().isEmpty()) {
                if (isEncrypted) {
                    message = encrypt(message);
                }
                writer.println(username + ": " + message);
                messageField.setText("");
            }
        }
    }

    private class EncryptMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEncrypted) {
                isEncrypted = false;
                encryptButton.setText("Encrypt");
            } else {
                isEncrypted = true;
                encryptButton.setText("On");
            }
        }
    }

    private class ReceivedMessagesHandler implements Runnable {
        @Override
        public void run() {
            while (reader.hasNextLine()) {
                String message = reader.nextLine();
                chatArea.append(message + "\n");
            }
        }
    }

    private String encrypt(String message) {
        StringBuilder encryptedMessage = new StringBuilder();
        String key = "Bi0s";
        for (int i = 0; i < message.length(); i++) {
            char encryptedChar = (char) (message.charAt(i) ^ key.charAt(i % key.length()));
            encryptedMessage.append(encryptedChar);
        }
        return encryptedMessage.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhatsAppGUI("localhost", 5000));
    }
}
