import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
//Runnable used to handle different instances/threads of clients
public class ClientHandler implements Runnable {

    /* Array to hold all instances of the ClientHandler class
    when a client sends a message it will be iterated through the contents of this array - allowing
    everyone to communicate */
    public static ArrayList<ClientHandler>clientHandler = new ArrayList<>();

    private Socket socket; //this will be passed from the server class. Used to establish a connection
    private BufferedReader bufferedReader; //used to read data i.e messages sent from the client
    private BufferedWriter bufferedWriter; //send data which is broadcast by the array clientHandler
    private String clientUserName;

    //constructor
    public ClientHandler(Socket socket){
        try {
            //this is the object being made from this class. Set the socket for it equal to what is passed
            //into the constructor(in Server class).
            this.socket = socket;

            //setup sockets to send/receive data --> this is wrapped(see below)
            //character stream inside a byte stream as we are sending messages
            //Writer = character. Stream = byte.
            //buffer to make communication efficient
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //As above but for readers
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine(); //read a line/what the types before pressing enter.

            //add client to the array so they can recieve messages
            clientHandler.add(this); //this = array is taking an object of ClientHandler

            broardcastMessage("SERVER: " + clientUserName + " has joined the chat!");
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }



    @Override
    public void run() {
        //use of separate threads so the program does not stay waiting for one client to send a message
        String messageFromClient;

        while(socket.isConnected()) { //while conned to a client...
            try {
                //listen for messages in the buffer reader
                messageFromClient = bufferedReader.readLine(); //program halts here until it receives a message - why multiple threads are used
                broardcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }//END RUN

    //Loop through the array of clients and send a message to all of them
    public void broardcastMessage(String messageToSend){
        //for each item/object in the array...
        for(ClientHandler clientHandler: clientHandler){
            try{
                //send message to the other clients
                if(!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(messageToSend); //each client object has a buffered writer.
                    clientHandler.bufferedWriter.newLine(); //must send back a new line - it says message is done (essentially)
                    clientHandler.bufferedWriter.flush(); //clear buffer (even if messages are not big enough)

                }
            } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    public void removeClientHandler(){
        //allow users to leave the chat
        clientHandler.remove(this); //remove current client object
        broardcastMessage("CLIENT " + clientUserName + " HAS LEFT THE CHAT");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
