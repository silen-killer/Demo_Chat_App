import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatBubbleExample extends JFrame {

    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;

    public ChatBubbleExample() {
        setTitle("Chat Bubble Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane chatScrollPane = new JScrollPane(chatPanel);
        add(chatScrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.trim().isEmpty()) {
                    addMessageToChat(message, true);
                    messageField.setText("");
                }
            }
        });
    }

    private void addMessageToChat(String message, boolean sentByUser) {
        ChatBubble chatBubble = new ChatBubble(message, sentByUser);
        chatPanel.add(chatBubble);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    private class ChatBubble extends JPanel {
        private static final int BUBBLE_PADDING = 10;
        private boolean sentByUser;

        public ChatBubble(String message, boolean sentByUser) {
            this.sentByUser = sentByUser;
            setOpaque(false);
            setBorder(new EmptyBorder(BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING));
            setAlignmentX(sentByUser ? RIGHT_ALIGNMENT : LEFT_ALIGNMENT);
            setLayout(new BorderLayout());
            JLabel label = new JLabel("<html><body>" + message + "</body></html>");
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setForeground(sentByUser ? Color.BLACK : Color.WHITE);
            add(label, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int[] xPoints = {0, width - BUBBLE_PADDING, width, width, 0};
            int[] yPoints = {0, 0, BUBBLE_PADDING, height - BUBBLE_PADDING, height};
            if (sentByUser) {
                g.setColor(new Color(0xDCF8C6)); // Light green for user's messages
            } else {
                g.setColor(new Color(0x007BFF)); // Blue for other's messages
            }
            g.fillPolygon(xPoints, yPoints, 5);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatBubbleExample example = new ChatBubbleExample();
            example.setVisible(true);
        });
    }
}
