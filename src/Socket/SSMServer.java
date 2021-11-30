package Socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public record SSMServer () {
    /*
    since when the client sends a request it must be returned to everyone (including the sender),
    I create a static connection list (shared among all threads) with all the thread.
     */
    public static LinkedList<SSMSThread> CONNECTIONS = new LinkedList<> ();

    /*
    it is managed as a thread because otherwise it would be blocking in main;
    it makes a loop in which it listens for any new clients that want to connect;
    when one connects it creates an SSMSThread which handles the request individually.
     */
    public static int PORT = 8083;

    public SSMServer {
        new Thread (() -> {
            // create the socket of the server
            ServerSocket socket = null;
            try { socket = new ServerSocket (SSMServer.PORT);
            } catch (IOException ignored) {}
            System.out.println ("listening on " + SSMServer.PORT);

            // listen for new clients
            while (true) {
                Socket client = null;
                try {
                    assert socket != null;
                    client = socket.accept ();
                } catch (IOException ignored) {}

                // create connection
                new Thread (new SSMSThread (client)).start ();
            }
        }).start ();
    }
}
