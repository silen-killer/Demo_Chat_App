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
    private PrintWriter writer;
    private String username;
    private boolean isEncrypted;
    private Scanner reader;

    public WhatsAppGUI(String host, int port) {
        username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous";
        }

        setTitle("WhatsApp");
        setLayout(new BorderLayout());

        // Apply WhatsApp color scheme
        Color backgroundColor = new Color(0xF5F5F5); // Light gray
        Color chatAreaColor = new Color(0xFFFFFF);   // White
        Color buttonColor = new Color(0x25D366);     // WhatsApp Green

        getContentPane().setBackground(backgroundColor);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(chatAreaColor);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        messageField.setBackground(chatAreaColor);
        messageField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size and style

        sendButton = new JButton("Send");
        sendButton.setBackground(buttonColor);
        sendButton.addActionListener(new SendMessageListener());

        encryptButton = new JButton("Encrypt");
        encryptButton.setBackground(buttonColor);
        encryptButton.addActionListener(new EncryptMessageListener());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(encryptButton, BorderLayout.WEST);
        inputPanel.setBackground(backgroundColor);
        add(inputPanel, BorderLayout.SOUTH);

        // Set a background image
        ImageIcon backgroundImage = new ImageIcon("whatsapp_back.jpeg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        add(backgroundLabel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);  // Center the frame
        setVisible(true);

        try {
            clientSocket = new Socket(host, port);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new Scanner(clientSocket.getInputStream());
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
