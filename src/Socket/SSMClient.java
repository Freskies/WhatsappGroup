package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SSMClient {
    protected String name;
    protected Socket server;
    protected BufferedReader reader;
    protected PrintWriter writer;

    public SSMClient (String name) {
        this.name = name;
    }

    protected boolean establishConnection () {
        // establish connection with the server and create reader and writer
        try {
            this.server = new Socket (InetAddress.getByName ("localhost"), 8083);
            this.reader = new BufferedReader (new InputStreamReader (server.getInputStream ()));
            this.writer = new PrintWriter (server.getOutputStream (), true);
            System.out.println (name + ": Connected to the server");

            this.sendRequest ("1", this.name, "0");
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    protected void sendMessage (String msg) {
        sendRequest ("0", this.name, msg);
    }

    protected void sendRequest (String header, String... parameters) {
        this.writer.println (header + "\n");
        for (String parameter : parameters)
            this.writer.println (parameter + "\n");
    }
}
