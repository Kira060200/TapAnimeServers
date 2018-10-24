import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
class ChatClient{
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock,sock2;
    static String str=new String();
    public static void main (String[] args)
    {
        str = JOptionPane.showInputDialog("Enter your name to use chatroom:");
        System.out.println(str);
        ChatClient client = new ChatClient();
        client.go();
    }
    public void go() {
        JFrame frame = new JFrame("Chat");
        JPanel mainPanel= new JPanel();
        incoming = new JTextArea(15,50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getRootPane().setDefaultButton(sendButton);
        setUpNetworking();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(600, 500);
        frame.setVisible(true);
    }
    private void setUpNetworking() {
        try {
            sock=new Socket("192.168.0.50", 5000);
            sock2=new Socket("192.168.0.50", 5001);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            DataOutputStream isWriter = new DataOutputStream(sock2.getOutputStream());
            isWriter.writeInt(0);
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Net established");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public class SendButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(str + ": " + outgoing.getText());
                writer.flush();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
    public class IncomingReader implements Runnable{
        public void run() {
            String message;
            try {
                while ( (message=reader.readLine())!=null) {
                    //System.out.println("read " + message);
                    incoming.append( message + '\n');
                }
                System.out.println("!while");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}