package Socket;

import java.util.LinkedList;

public class SSMMessages {
    private final LinkedList<String[]> listOfMessages = new LinkedList<> ();
    private final String name;

    public SSMMessages (String name) {
        this.name = name;
    }

    public void addMessage (String header, String... parameters) {
        switch (header) {
            // Normal Message (Name, Message)
            case "0" -> this.listOfMessages.add (new String [] {header, parameters [0], parameters [1]});

            // Add & Exit the group (Name, Command ("0": enter, "1": exit))
            case "1" -> this.listOfMessages.add (new String [] {header, parameters [0], parameters [1]});

            default -> System.out.println ("Unknown Request M");
        }
    }

    public String getHTML () {
        StringBuilder s = new StringBuilder ("""
                <html>
                <head>
                \t<meta charset="utf-8">
                \t<meta name="viewport" content="width=device-width, initial-scale=1">
                \t<title>Chat:</title>
                \t<style type="text/css">
                \t\tb {
                \t\t\tcolor: darkgreen;
                \t\t}
                \t\tb.me {
                \t\t\tcolor: green;
                \t\t}
                \t\tb.join {
                \t\t\tcolor: blue;
                \t\t}
                \t\tb.left {
                \t\t\tcolor: red;
                \t\t}
                \t</style>
                </head>
                <body>""");

        for (String [] message : this.listOfMessages) {
            switch (message[0]) {
                // Normal Message (Name, Message)
                case "0" -> {
                    s.append (message[1].equals (this.name) ? "<b class=\"me\">" : "<b>");
                    s.append (message[1]).append ("</b>: ");
                    s.append (message[2]);
                }

                // Add & Exit the group (Name, Command ("0": enter, "1": exit))
                case "1" -> {
                    s.append ("<b class=\"");
                    s.append (message[2].equals ("0") ? "join" : "left").append ("\">");
                    s.append (message[1]).append (" has ");
                    s.append (message[2].equals ("0") ? "joined" : "left");
                    s.append (" the chat</b>");
                }

                default -> System.out.println ("Unknown Type Of Message");
            }
            s.append ("<br>");
        }

        return s + "</p></body></html>";
    }
}
