import java.io.*;
import java.net.*;
import java.util.*;
public class ChatServer {
    ArrayList[] clientOutputStreams=new ArrayList[100];
    //String[] message;
    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;
        int channel;
        public ClientHandler(Socket clientSocket,int id) {
            try {
                sock=clientSocket;
                channel=id;
                InputStreamReader isReader=new InputStreamReader(sock.getInputStream());
                reader=new BufferedReader(isReader);
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        public void run() {
            String message;
            try {
                while( (message=reader.readLine())!=null) {
                    tellEveryone(message,channel);
                }
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        new ChatServer().go();
    }
    public void go() {
        clientOutputStreams[0] = new ArrayList();
        clientOutputStreams[1] = new ArrayList();
        clientOutputStreams[2] = new ArrayList();
        try {
            ServerSocket serverSock=new ServerSocket(5000);
            ServerSocket serverSock2=new ServerSocket(5001);
            while(true) {
                Socket clientSocket = serverSock.accept();
                Socket clientSocket2 = serverSock2.accept();
                DataInputStream isReader=new DataInputStream(clientSocket2.getInputStream());
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                int channel=0;
                channel=isReader.readInt();
                System.out.println(channel);
                clientOutputStreams[channel].add(writer);
                Thread t = new Thread(new ClientHandler(clientSocket,channel));
                t.start();
                System.out.println("Got a connection");
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void tellEveryone(String message,int channel) {
        Iterator it = clientOutputStreams[channel].iterator();
        while(it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}