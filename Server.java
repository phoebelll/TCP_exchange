import java.io.*;
import java.net.*;

public class Server {

    private static boolean running = true ;
    private static boolean verified = false ;
    private static String acknowledge = "no";
    public  static int c_port;
    public  static InetAddress IPAddress;

    public static void send_to_client(String content, InetAddress ipAddress, 
                                int port, DatagramSocket udpSocket ) throws IOException
    { // using UDPSocket to send client
        byte[] sendData = content.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        udpSocket.send(sendPacket);

    }
    public static String receive_from_client( DatagramSocket udpSocket) throws IOException
    {  // using UDPSocket to receive from client
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(receivePacket);
        String rec_content = new String(receivePacket.getData()).trim();
        Server.c_port = receivePacket.getPort();
        Server.IPAddress = receivePacket.getAddress();
        return rec_content;

    }
    public static void main (String[] args) throws Exception {

        //check input arguments and set to Req_code
        if (args.length != 1){
            System.err.println("Number of arguments is not correct!");
            return;
        }
        String regCode= args[0];

        // creates a UDP socket with <n_port> 
        ServerSocket Socket = new ServerSocket(0);
        System.out.println("SERVER_PORT=" + Socket.getLocalPort());
        DatagramSocket serverSocket = new DatagramSocket(Socket.getLocalPort());
        
    
        // start listening on the n_port
        while (running) {
            // to receive and verify request code 
            String req_code_c = receive_from_client(serverSocket);
            if (req_code_c.equals(regCode)) { 
                Server.running = false;
                Server.verified = true;
            }

        ServerSocket tcpSocket;
        int r_port;
        if (verified) { // if verified req_code, create TCPsocket with random avaliable port number
            tcpSocket = new ServerSocket(0);
            r_port = tcpSocket.getLocalPort();
            System.out.println("SERVER_TCP_PORT=" + r_port);
        } else { // if NOT verified, server terminate
            return;
        }


        // server uses the UDP socket to reply back with <r_port>.
        String content = Integer.toString(r_port);
        send_to_client(content, IPAddress, c_port, serverSocket);

        // receive  r_port comfirmation from client
        String confirm = receive_from_client(serverSocket);

        // The server acknowledges the received <r_port>
        if (confirm.equals(Integer.toString(r_port))){
            acknowledge = "ok";
        }
        send_to_client(acknowledge, IPAddress, c_port, serverSocket);

        /////////////////// Stage 2 ////////////////////////

        //connection to client using created tcpSocket in Stage1
        Socket clientSocket = tcpSocket.accept();
        BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String msg = in.readLine();
        System.out.println("SERVER_RCV_MSG=" + "'"+msg+"'");

        //reverse message from client
        StringBuilder rev_msg = new StringBuilder();
        rev_msg.append(msg);
        rev_msg = rev_msg.reverse();

        // send reversed string back
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        String toClient = rev_msg.toString() + '\n';
        out.writeBytes(toClient);
    }
}
}