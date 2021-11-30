package Socket;

import java.io.*;
import java.net.Socket;

public class SSMSThread implements Runnable {

    protected Socket socket; // socket to establish connections
    protected BufferedReader reader = null; // to read
    protected PrintWriter writer = null; // to write

    public SSMSThread (Socket socket) {
        this.socket = socket;

        // add this thread to the connection list
        SSMServer.CONNECTIONS.add (this);

        // create reader and writer
        try {
            assert this.socket != null;
            this.reader = new BufferedReader (new InputStreamReader (this.socket.getInputStream ()));
            this.writer = new PrintWriter (this.socket.getOutputStream (), true);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run () {
        assert writer != null;
        assert reader != null;

        boolean isConnected = true;

        while (isConnected) {
            // My protocol
            String request = this.readRequest ();
            switch (request) {
                // Normal Message that have to be resented to all
                case "0" -> {
                    String name = this.readRequest ();
                    String msg = this.readRequest ();
                    this.resendToAll (request, name, msg);
                }

                // Exit the connection
                case "1" -> {
                    // communication with clients
                    String name = this.readRequest ();
                    String join = this.readRequest ();
                    this.resendToAll (request, name, join);

                    // exit
                    if (join.equals ("1")) {
                        isConnected = false;
                        SSMServer.CONNECTIONS.remove (this);
                        System.out.println ("Connection \"" + name + "\" successfully killed");
                    }
                    // System.out.println (SSMServer.CONNECTIONS);

                }

                default -> System.out.println ("Unknown Request T");
            }
        }
    }

    public String readRequest () {
        String read = null;
        StringBuilder request = new StringBuilder ();

        while (true) {
            try {
                if ((read = reader.readLine ()).equals ("")) break;
            } catch (IOException ignored) {
            }
            //System.out.println (read);
            request.append (read);
        }

        return request.toString ();
    }

    public void sendMessage (String request) {
        writer.println (request + "\n");
    }

    public void resendToAll (String header, String name, String msg) {
        for (SSMSThread connection : SSMServer.CONNECTIONS) {
            connection.sendMessage (header);
            connection.sendMessage (name);
            connection.sendMessage (msg);
        }
    }
}
