import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.awt.image.BufferedImage;

public class WhatsAppGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private RoundedButton sendButton;
    private RoundedButton encryptButton;
    private Socket clientSocket;
    private PrintWriter writer;
    private String username;
    // private boolean isEncrypted = true;
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
        chatArea.setFont(new Font("ArialBlack", Font.BOLD, 20));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        messageField.setBackground(chatAreaColor);
        messageField.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font size and style

        sendButton = new RoundedButton("Send");
        sendButton.setBackground(buttonColor);
        sendButton.addActionListener(new SendMessageListener());

        encryptButton = new RoundedButton("Mic");
        encryptButton.setBackground(buttonColor);
        // encryptButton.addActionListener(new EncryptMessageListener());

        // Set a background image
        ImageIcon backgroundImage = new ImageIcon("output-onlinepngtools.png");
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
        int width = 800; // Adjust the width as needed
        int height = 1150; // Adjust the height as needed
        int x = 1150; // Adjust the x-coordinate as needed
        int y = 0; // Adjust the y-coordinate as needed
        setSize(width, height);
        setLocation(x, y);
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
                {
                    message = encrypt(username + ":" + message);
                    writer.println(message);
                    messageField.setText("");
                }
            }
        }
    }

    private class ReceivedMessagesHandler implements Runnable {
        @Override
        public void run() {
            while (reader.hasNextLine()) {
                String message = reader.nextLine();
                message = decrypt(message);
                chatArea.append(message + "\n");
            }
        }
    }

    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            String tmp = Integer.toHexString((int) ch);
            if(tmp.length()==1){
                tmp="0"+tmp;
            }
            hex.append(tmp);
        }
        return hex.toString();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        
        return output.toString();
    }

    private String encrypt(String message) {
    StringBuilder encryptedMessage = new StringBuilder();
    String key = "Bi0s";
    for (int i = 0; i < message.length(); i++) {
        char encryptedChar = (char) (message.charAt(i) ^ key.charAt(i % key.length()));
        encryptedMessage.append(encryptedChar);
    }
    
    String mess = asciiToHex(encryptedMessage.toString());
    return mess.toString();

    }

    private String decrypt(String message){
        String decryptedMessageAscii = new String();
        String key = "Bi0s";
        decryptedMessageAscii = hexToAscii(message);
        StringBuilder decryptedMessage = new StringBuilder();
        for (int i = 0; i < decryptedMessageAscii.length(); i++) {
            char decryptedChar = (char) (decryptedMessageAscii.charAt(i) ^ key.charAt(i % key.length()));
            decryptedMessage.append(decryptedChar);
        }
        return decryptedMessage.toString();

    }


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
        SwingUtilities.invokeLater(() -> new WhatsAppGUI("localhost", 12345));
    }
}