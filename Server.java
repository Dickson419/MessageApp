import java.io.IOException;
import java.net.ContentHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;

public class Server {
    /*
    Listen for new clients and create a new thread to handle them.
    Have to remember to handle input/output exceptions which come from working with sockets
     */

    private ServerSocket serverSocket; //responsible for listening for incoming connections

    //constructor
    //automatically called when an object of the class is created.
    //it is used to initialise the state of the object and perform any other setup that is necessary.

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        /*
        the constructor takes a single argument of type ServerSocket, which is assigned to the
        serverSocket instance variable of the class. This instance variable is then used to listen
        for incoming connections in the rest of the class.
         */
    }

    //start server method. Responsible for keeping the server running
    public void startServer(){
        try{
            while (!serverSocket.isClosed()){ //while socket is open, or not closed...

                //a holding method --> pauses the program until a client connects
                //when a client connects a socket object is returned --> AND this class/variable(Socket socket)
                //is used to communicate with them!
                Socket socket = serverSocket.accept();


                //alert clients someone new has joined the server
                System.out.println("A new dickhead has joined your 'cool' chat");

                //each instance of this class wil be responsible for communicating with a client.
                //and implements the interface runnable (on separate threads)
                ClientHandler clientHandler = new ClientHandler(socket) {
//                    @Override
//                    public Object getContent(URLConnection urlc) throws IOException {
//                        return null;
//                    }
                };

                //threads for each instance
                Thread thread = new Thread(clientHandler);
                thread.start(); //run the method


            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}

