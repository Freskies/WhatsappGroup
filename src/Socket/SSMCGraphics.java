package Socket;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.Callable;

public class SSMCGraphics extends SSMClient {

    /*
    I don't create parametric graphics with static variables because it's boring to do
    It's too big job for the result you get.
    At the moment all the measurements are nailed into the code.
    I use the prefix 'g' for graphics elements
     */
    private final JFrame gWindow = new JFrame ("SSM - Super Secret Messages");
    private JButton gConnectButton, gSendButton;
    private JEditorPane gGroupMessagesArea;
    private JTextField gTextField;
    private final SSMMessages ssmMessages;

    public SSMCGraphics (String name) {
        super (name);
        this.ssmMessages = new SSMMessages (name);
        this.generateGraphics ();
    }

    private void listenMessages () {
        // function to listen one request
        Callable<String> listenRequest = () -> {
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
        };

        new Thread (() -> {
            while (true) {
                // My protocol
                String request = "";
                try { request = listenRequest.call (); } catch (Exception ignored) {}

                switch (request) {
                    // 0 : Normal Message
                    // 1 : Join / Exit the chat (name, join ("0": join, "1"; left))
                    case "0", "1" -> {
                        try {
                            String name = listenRequest.call ();
                            String msg = listenRequest.call ();
                            this.ssmMessages.addMessage (request, name, msg);
                        } catch (Exception ignored) {
                        }
                    }

                    default -> System.out.println ("Unknown Request");
                }
                this.gGroupMessagesArea.setText (this.ssmMessages.getHTML ());
            }
        }).start ();
    }

    public void disconnect () {
        if (this.writer != null) this.sendRequest ("1", this.name, "1");
    }

    // if it is connected it would be able to use the chat
    public void setServicesUp () {
        this.gTextField.setEnabled (true);
        this.gSendButton.setEnabled (false);
        this.gGroupMessagesArea.setEnabled (true);
        this.gConnectButton.setEnabled (false);
        this.listenMessages ();
    }

    // if it is not connected it would not be able to use the chat
    public void setServicesDown () {
        this.gTextField.setEnabled (false);
        this.gSendButton.setEnabled (false);
        this.gGroupMessagesArea.setEnabled (false);
        this.gConnectButton.setEnabled (true);
    }

    public void setSendableMessage (boolean sendableMessage) {
        this.gSendButton.setEnabled (sendableMessage);
    }

    private void generateGraphics () {
        // window
        this.gWindow.setBounds (10, 10, 400, 620);
        this.gWindow.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        this.gWindow.addWindowListener (new GWindowListener (this));
        this.gWindow.setLayout (null);
        this.gWindow.setResizable (false);
        this.gWindow.setIconImage (Main.IMAGE_ICON.getImage ());

        // name
        JLabel gName = new JLabel (this.name);
        gName.setBounds (20, 10, 230, 50);
        this.gWindow.add (gName);

        // connect button (if it isn't a connection the other part will not be able to be accessible)
        this.gConnectButton = new JButton ("Connect");
        this.gConnectButton.setFocusable (false);
        this.gConnectButton.setBounds (270, 10, 100, 50);
        this.gConnectButton.addActionListener (new GConnectButtonListener (this));
        this.gWindow.add (this.gConnectButton);

        // text area for group messages
        this.gGroupMessagesArea = new JEditorPane ();
        this.gGroupMessagesArea.setBounds (20, 80, 350, 400);
        this.gGroupMessagesArea.setEditable (false);
        this.gGroupMessagesArea.setContentType ("text/html");
        //this.gWindow.add (this.gGroupMessagesArea);

        JScrollPane p = new JScrollPane (this.gGroupMessagesArea);
        p.setBounds (20, 80, 350, 400);
        this.gWindow.add (p);

        // send button
        this.gSendButton = new JButton ("Send");
        this.gSendButton.setFocusable (false);
        this.gSendButton.setBounds (270, 500, 100, 48);
        this.gSendButton.addActionListener (new GSendButtonListener (this));
        this.gWindow.add (this.gSendButton);

        // text field to send messages
        this.gTextField = new JTextField ("");
        this.gTextField.setBounds (20, 500, 230, 50);
        GTextFieldListener gTextFieldListener = new GTextFieldListener (this, this.gSendButton);
        this.gTextField.getDocument ().addDocumentListener (gTextFieldListener);
        this.gTextField.addKeyListener (gTextFieldListener);
        this.gWindow.add (this.gTextField);

        this.setServicesDown ();
        this.gWindow.setVisible (true);
    }

    public String getMessageToSend () {
        return this.gTextField.getText ();
    }

    public void resetMessageTextField () {
        this.gTextField.setText ("");
        this.gSendButton.setEnabled (false);
    }
}

record GConnectButtonListener (SSMCGraphics ssmcGraphics) implements ActionListener {

    @Override
    public void actionPerformed (ActionEvent e) {
        if (ssmcGraphics.establishConnection ()) ssmcGraphics.setServicesUp ();
        else ssmcGraphics.setServicesDown ();
    }
}

record GSendButtonListener (SSMCGraphics ssmcGraphics) implements ActionListener {

    @Override
    public void actionPerformed (ActionEvent e) {
        //System.out.println (this.ssmcGraphics.getMessageToSend ());
        this.ssmcGraphics.sendMessage (this.ssmcGraphics.getMessageToSend ());
        this.ssmcGraphics.resetMessageTextField ();
    }
}

record GTextFieldListener (SSMCGraphics ssmcGraphics, JButton gSendButton) implements DocumentListener, KeyListener {

    @Override public void insertUpdate (DocumentEvent e) { this.warn (); }

    @Override public void removeUpdate (DocumentEvent e) { this.warn (); }

    @Override public void changedUpdate (DocumentEvent e) { this.warn (); }

    public void warn () {
        ssmcGraphics.setSendableMessage (!ssmcGraphics.getMessageToSend ().equals (""));
    }

    @Override public void keyTyped (KeyEvent e) {

    }

    @Override public void keyPressed (KeyEvent e) {

    }

    @Override public void keyReleased (KeyEvent e) {
        if (e.getKeyCode () == KeyEvent.VK_ENTER) gSendButton.doClick ();
    }
}

record GWindowListener (SSMCGraphics ssmcGraphics) implements WindowListener {

    @Override public void windowOpened (WindowEvent e) {}

    @Override
    public void windowClosing (WindowEvent e) {
        ssmcGraphics.disconnect ();
    }

    @Override
    public void windowClosed (WindowEvent e) {

    }

    @Override public void windowIconified (WindowEvent e) {}

    @Override public void windowDeiconified (WindowEvent e) {}

    @Override public void windowActivated (WindowEvent e) {}

    @Override public void windowDeactivated (WindowEvent e) {}
}
