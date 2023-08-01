import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.*;

import java.util.ArrayList;
import java.util.Arrays;

    // ImageIcon bg = new ImageIcon("C:\\Users\\adith\\Downloads\\java-chat\\java-chat\\whatsapp_back.jpeg");
    // JLabel Label = new JLabel(bg);
    // JLayeredPane layeredPane = new JLayeredPane();
    // layeredPane.add(Label, JLayeredPane.DEFAULT_LAYER);
    // layeredPane.add(jtextFilDiscu, JLayeredPane.DEFAULT_LAYER);
    // jtextFilDiscu.add(layeredPane, BorderLayout.CENTER);


public class ClientGui extends Thread{

  final JTextPane jtextFilDiscu = new JTextPane();
  final JTextPane jtextListUsers = new JTextPane();
  final JTextField jtextInputChat = new JTextField();
  private String oldMsg = "";
  private Thread read;
  private String serverName;
  private int PORT;
  private String name;
  BufferedReader input;
  PrintWriter output;
  Socket server;
  final JToggleButton jtb = new JToggleButton("Encryption");

  public ClientGui() {
    this.serverName = "localhost";
    this.PORT = 12345;
    this.name = "nickname";

    String fontfamily = "Comic Sans MS";
    Font font = new Font(fontfamily, Font.BOLD, 15);

    final JFrame jfr = new JFrame("Chat");
    jfr.getContentPane().setLayout(null);
    jfr.setSize(800, 500);
    jfr.setResizable(false);
    jfr.getContentPane().setBackground(new java.awt.Color(7, 94, 84));
    jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Module du fil de discussion
    jfr.setBackground(Color.white);
    jfr.setLocationRelativeTo(null);
    jtextFilDiscu.setBounds(25, 25, 490, 320);
    jtextFilDiscu.setFont(font);
    jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
    jtextFilDiscu.setEditable(false);
    JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
    jtextFilDiscuSP.setBounds(25, 25, 550, 320);

    jtextFilDiscu.setContentType("text/html");
    jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

    // Module de la liste des utilisateurs
    jtextListUsers.setBounds(520, 25, 156, 320);
    jtextListUsers.setEditable(true);
    jtextListUsers.setFont(font);
    jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
    jtextListUsers.setEditable(false);
    JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
    jsplistuser.setBounds(600, 25, 156, 320);

    jtextListUsers.setContentType("text/html");
    jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

    // Field message user input
    jtextInputChat.setBounds(0, 350, 400, 50);
    jtextInputChat.setFont(font);
    jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
    final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
    jtextInputChatSP.setBounds(25, 350, 650, 50);

    // button send
    final JButton jsbtn = new JButton("Send");
    jsbtn.setFont(font);
    jsbtn.setBounds(575, 410, 100, 35);

    // button Disconnect
    final JButton jsbtndeco = new JButton("Disconnect");
    jsbtndeco.setFont(font);
    jsbtndeco.setBounds(25, 410, 130, 35);

    // Toggle encryption button
    jtb.setFont(font);
    jtb.setBounds(200, 410, 130, 35);

    jtextInputChat.addKeyListener(new KeyAdapter() {
      // send message on Enter
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          sendMessage();
        }

        // Get last message typed
        if (e.getKeyCode() == KeyEvent.VK_UP) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }
      }
    });

    // Click on send button
    jsbtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        sendMessage();
      }
    });

    // Connection view
    final JTextField jtfName = new JTextField(this.name);
    final JTextField jtfport = new JTextField(Integer.toString(this.PORT));
    final JTextField jtfAddr = new JTextField(this.serverName);
    final JButton jcbtn = new JButton("Connect");

    // check if those field are not empty
    jtfName.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));
    jtfport.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));
    jtfAddr.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));

    // edited
    class RoundBtn implements Border 
  {
      private int r;
      RoundBtn(int r) {
          this.r = r;
      }
      public Insets getBorderInsets(Component c) {
          return new Insets(this.r+1, this.r+1, this.r+2, this.r);
      }
      public boolean isBorderOpaque() {
          return true;
      }
      public void paintBorder(Component c, Graphics g, int x, int y, 
      int width, int height) {
          g.drawRoundRect(x, y, width-1, height-1, r, r);
      }
  }
    JFrame frame = new JFrame("");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(225, 250);
    frame.setLocationRelativeTo(null);
    JPanel connectionPanel = new JPanel();
    connectionPanel.setLayout(new GridLayout(3, 2));
    JLabel Jname = new JLabel("    NAME");
    JTextField JtfName = new JTextField(this.name);
    JLabel Jport = new JLabel("    PORT");
    JTextField Jtfport = new JTextField(Integer.toString(this.PORT));
    JLabel Jaddr = new JLabel("   SERVER IP");
    JTextField JtfAddr = new JTextField(this.serverName);
    JButton Jcbtn = new JButton("Connect");
    Jcbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                  String name = JtfName.getText();
                  String port = Jtfport.getText();
                  String serverName = JtfAddr.getText();
                  PORT = Integer.parseInt(port);
                  jfr.setVisible(true);
                  appendToPane(jtextFilDiscu, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
                  server = new Socket(serverName, PORT);

                  appendToPane(jtextFilDiscu, "<span>Connected to " +
                      server.getRemoteSocketAddress()+"</span>");

                  input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                  output = new PrintWriter(server.getOutputStream(), true);

                  // send nickname to server
                  output.println(name);

                  // create new Read Thread
                  read = new Read();
                  read.start();
                  jfr.remove(jtfName);
                  jfr.remove(jtfport);
                  jfr.remove(jtfAddr);
                  jfr.remove(jcbtn);
                  jfr.add(jsbtn);
                  jfr.add(jtextInputChatSP);
                  jfr.add(jsbtndeco);
                  jfr.add(jtb);
                  jfr.revalidate();
                  jfr.repaint();
                  jtextFilDiscu.setBackground(Color.WHITE);
                  jtextListUsers.setBackground(Color.WHITE);
                } catch (Exception ex) {
                  appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
                  JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
              }

            });
    Jcbtn.setFont(font);
    Jname.setBounds(10, 20, 100, 25);
    JtfName.setBounds(100,20, 100, 25);
    Jaddr.setBounds(10,60, 100, 25);
    JtfAddr.setBounds(100,60, 100, 25);
    Jport.setBounds(10,100, 100, 25);
    Jtfport.setBounds(100,100, 100, 25);
    Jcbtn.setBounds(50, 140, 100, 40);
    frame.add(Jname);
    frame.add(JtfName);
    frame.add(Jport);
    frame.add(Jtfport);
    frame.add(Jaddr);
    frame.add(JtfAddr);
    frame.add(Jcbtn);
    frame.getContentPane().add(connectionPanel);
    frame.setResizable(false);
    // jtextFilDiscuSP.setBounds(25, 25, 550, 320);
    // jtextFilDiscuSP.add(panel);

    // position des Modules
    jcbtn.setFont(font);
    jtfAddr.setBounds(25, 380, 135, 40);
    jtfName.setBounds(375, 380, 135, 40);
    jtfport.setBounds(200, 380, 135, 40);

    // couleur par defaut des Modules fil de discussion et liste des utilisateurs

    // ajout des éléments
    // jfr.getContentPane().setBackground(Color.BLACK);
    jcbtn.setBorder(new RoundBtn(40));
    jfr.add(jcbtn);
    jfr.add(jtextFilDiscuSP);
    jfr.add(jsplistuser);
    frame.setVisible(true);

    // info sur le Chat
    appendToPane(jtextFilDiscu, "<h2><b>Welcome to the bi0s chat app:</b></h2>"
        +"<ul>"
        +"<li>Enter your nickname, server address and port</li><br>"
        +"<li>This application is for demonstrating encryption"
        +"</ul><br>");

    // on deco
    jsbtndeco.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent ae) {
        jfr.add(jtfName);
        jfr.add(jtfport);
        jfr.add(jtfAddr);
        jfr.add(jcbtn);
        jfr.remove(jsbtn);
        jfr.remove(jtextInputChatSP);
        jfr.remove(jsbtndeco);
        jfr.remove(jtb);
        jfr.revalidate();
        jfr.repaint();
        read.interrupt();
        jtextListUsers.setText(null);
        jtextFilDiscu.setBackground(Color.BLACK);
        jtextListUsers.setBackground(Color.BLACK);
        appendToPane(jtextFilDiscu, "<span>Connection closed.</span>");
        output.close();
      }
    });

  }

  // check if if all field are not empty
  public class TextListener implements DocumentListener{
    JTextField jtf1;
    JTextField jtf2;
    JTextField jtf3;
    JButton jcbtn;

    public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3, JButton jcbtn){
      this.jtf1 = jtf1;
      this.jtf2 = jtf2;
      this.jtf3 = jtf3;
      this.jcbtn = jcbtn;
    }

    public void changedUpdate(DocumentEvent e) {}

    public void removeUpdate(DocumentEvent e) {
      if(jtf1.getText().trim().equals("") ||
          jtf2.getText().trim().equals("") ||
          jtf3.getText().trim().equals("")
          ){
        jcbtn.setEnabled(false);
      }else{
        jcbtn.setEnabled(true);
      }
    }
    public void insertUpdate(DocumentEvent e) {
      if(jtf1.getText().trim().equals("") ||
          jtf2.getText().trim().equals("") ||
          jtf3.getText().trim().equals("")
          ){
        jcbtn.setEnabled(false);
      }else{
        jcbtn.setEnabled(true);
      }
    }

  }

  public static String encrypt(String s, String key) {
    byte[] textBytes = s.getBytes();
    byte[] keyBytes = key.getBytes();
    byte[] cipherBytes = new byte[textBytes.length];
    
    for (int i = 0; i < textBytes.length; i++) {
        cipherBytes[i] = (byte) (textBytes[i] ^ keyBytes[i % keyBytes.length]);
    }
    
    return bytesToHex(cipherBytes);
}

  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
        int v = bytes[i] & 0xFF;
        hexChars[i * 2] = HEX_ARRAY[v >>> 4];
        hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}

  public static String decrypt(String s, String key) {
    byte[] cipherBytes = hexToBytes(s);
    byte[] keyBytes = key.getBytes();
    byte[] textBytes = new byte[cipherBytes.length];
    
    for (int i = 0; i < cipherBytes.length; i++) {
        textBytes[i] = (byte) (cipherBytes[i] ^ keyBytes[i % keyBytes.length]);
    }
    
    return new String(textBytes);
}

  public static byte[] hexToBytes(String hexString) {
    int len = hexString.length();
    byte[] bytes = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                             + Character.digit(hexString.charAt(i+1), 16));
    }
    return bytes;
}

  // envoi des messages
  public void sendMessage() {
    try {
      String message = jtextInputChat.getText().trim();
      if (message.equals("")) {
        return;
      }
      //Encrypt if button is toggled
      if (jtb.isSelected()) {
        message = encrypt(message, "bi0s");
      this.oldMsg = message;
      output.println("Enc: " + message);
      }
      else
      {
        this.oldMsg = message;
        output.println(message);
      }
      jtextInputChat.requestFocus();
      jtextInputChat.setText(null);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage());
      System.exit(0);
    }
  }

  public static void main(String[] args) throws Exception {
    ClientGui client = new ClientGui();
  }

  // read new incoming messages
  class Read extends Thread {
    public void run() {
      String message;
      while(!Thread.currentThread().isInterrupted()){
        try {
          message = input.readLine();
          if(message != null){
            if (message.charAt(0) == '[') {
              message = message.substring(1, message.length()-1);
              ArrayList<String> ListUser = new ArrayList<String>(
                  Arrays.asList(message.split(", "))
                  );
              jtextListUsers.setText(null);
              for (String user : ListUser) {
                appendToPane(jtextListUsers, "@" + user);
              }
            }else{
              // Decrypt enc part if necessary
              try
              {
              if (message.indexOf("Enc: ") != -1)
              {
                String enchex = message.substring(message.indexOf("Enc: ") + 5, message.length()-7);
                message = message.replace("Enc: " + enchex, decrypt(enchex, "bi0s"));
              }
              appendToPane(jtextFilDiscu, message);
            }
            catch (Exception e)
            {
              appendToPane(jtextFilDiscu, oldMsg);
            }
          }
        }
        }
        catch (IOException ex) {
          System.err.println("Failed to parse incoming message");
        }
      }
    }
  }

  // send html to pane
  private void appendToPane(JTextPane tp, String msg){
    HTMLDocument doc = (HTMLDocument)tp.getDocument();
    HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
    try {
      editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
      tp.setCaretPosition(doc.getLength());
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
