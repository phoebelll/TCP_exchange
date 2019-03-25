import java.io.*;
import java.net.*;

public class client {

    public static void send_to_server(String content, InetAddress ipAddress, 
                                int port, DatagramSocket udpSocket ) throws IOException
    { // using UDPSocket to send server
        byte[] sendData = content.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        udpSocket.send(sendPacket);

    }
    public static String receive_from_server(DatagramSocket udpSocket ) throws IOException
    { // using UDPSocket to receive from server
        byte[] receiveData = new byte[1024]; // allocate bytes
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(receivePacket);
        String rec_info = new String(receivePacket.getData());
        return rec_info.trim();

    }
    public static void main(String[] args) throws Exception {
        // check number of input arguments 
        if (args.length != 4){
            System.err.println("Number of arguments is not correct!");
            return;
        }
        String serverAdd = args[0];
        int nPort= Integer.parseInt(args[1]);
        String regCode= args[2];
        String msg = args[3];

        // Client convert server_address to ip
        InetAddress address = InetAddress.getByName(serverAdd);
 

        // client create packet
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            //send req_code using udpSocket
            send_to_server(regCode, address, nPort, clientSocket);
 
            // recived r_port
            String rPort = receive_from_server(clientSocket);

            //to confirm r_port through UDP socket
            send_to_server(rPort, address, nPort, clientSocket);

            // recevie server's acknowledgment
            String acknowledgment = receive_from_server(clientSocket);
 
            if (acknowledgment.equals("ok")){
                clientSocket.close();
            }
            ///////////////// stage 2 ///////////////////
            // client creates a TCP connection to the server at <r_port>
            int r_port = Integer.parseInt(rPort);
            Socket tcpSocket = new Socket(serverAdd,r_port);
            // sends the <msg> to client
            DataOutputStream outToServer = new DataOutputStream (tcpSocket.getOutputStream());
            outToServer.writeBytes(msg+'\n');

            // recive reversed msg from client and print out
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            String revMsg = inFromServer.readLine();
            System.out.println("CLIENT_RCV_MSG= " + "'"+revMsg+"'");
            tcpSocket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}