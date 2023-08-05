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
    private RoundedButton sendButton;
    private RoundedButton encryptButton;
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
        chatArea.setFont(new Font("Arial", Font.BOLD, 20));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        messageField.setBackground(chatAreaColor);
        messageField.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font size and style

        sendButton = new RoundedButton("Send");
        sendButton.setBackground(buttonColor);
        sendButton.addActionListener(new SendMessageListener());

        encryptButton = new RoundedButton("Encrypt");
        encryptButton.setBackground(buttonColor);
        encryptButton.addActionListener(new EncryptMessageListener());

        // Set a background image
        ImageIcon backgroundImage = new ImageIcon("whatsapp_back.jpeg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        // Add the chat area to the background label
        Color TRANSPARENT = new Color(0, 0, 0, 0);
        chatArea.setOpaque(false);
        chatArea.setBackground(TRANSPARENT);
        backgroundLabel.add(chatArea, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(encryptButton, BorderLayout.WEST);
        inputPanel.setBackground(backgroundColor);
        add(inputPanel, BorderLayout.SOUTH);

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
                    writer.println(username + ": ENCRYPTED : " + message);
                    messageField.setText("");
                }
                else {
                    writer.println(username + " : " + message);
                    messageField.setText("");
                }
            }
        }
    }

    private class EncryptMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isEncrypted = !isEncrypted;
            encryptButton.setText(isEncrypted ? "ON" : "OFF");
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
            encryptedMessage.append(String.format("%02X", (int) encryptedChar));  // Convert to hex
        }
        return encryptedMessage.toString();
    }

    // Custom button class with rounded edges
    private class RoundedButton extends JButton {
        private static final int ARC_WIDTH = 20;
        private static final int ARC_HEIGHT = 20;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isArmed()) {
                g.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g.setColor(getBackground().brighter());
            } else {
                g.setColor(getBackground());
            }
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhatsAppGUI("192.168.42.60", 5000));
    }
}