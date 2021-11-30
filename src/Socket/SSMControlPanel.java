package Socket;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class SSMControlPanel extends JFrame {
    protected JButton cpServerButton, cpClientButton;
    protected JTextField cpClientName;
    protected SSMServer ssmServer;
    protected static LinkedList <SSMClient> CLIENT_CONNECTED = new LinkedList<> ();

    public SSMControlPanel () {
        super ("SSM - Control Panel");
        this.createGraphics ();
    }

    @SuppressWarnings ("InstantiationOfUtilityClass")
    public void enableServer () {
        this.ssmServer = new SSMServer ();
        this.cpServerButton.setEnabled (false);
    }

    public String getNameOfClient () {
        return this.cpClientName.getText ();
    }

    public void resetNameOfClient () {
        this.cpClientName.setText ("");
    }

    public void setCreateClientEnabled (boolean createClientEnabled) {
        this.cpClientButton.setEnabled (createClientEnabled);
    }

    // return true if the name is valid
    public boolean checkNameOfClient (String name) {
        for (SSMClient client : SSMControlPanel.CLIENT_CONNECTED)
            if (name.equals (client.name)) return false;
        return true;
    }

    private void createGraphics () {
        // window
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        this.setBounds (10, 10, 395, 190);
        this.setLayout (null);
        this.setResizable (false);
        this.setIconImage (Main.IMAGE_ICON.getImage ());

        // button start server
        this.cpServerButton = new JButton ("Start Server");
        this.cpServerButton.setBounds (10, 10, 150, 60);
        this.cpServerButton.addActionListener (new CPServerButtonListener (this));
        this.cpServerButton.setFocusable (false);
        this.add (this.cpServerButton);

        // label name
        JLabel name = new JLabel ("Name of the new Client:");
        name.setBounds (205, 50, 200, 30);
        this.add (name);

        // button create client
        this.cpClientButton = new JButton ("Add Client");
        this.cpClientButton.setBounds (10, 80, 150, 60);
        this.cpClientButton.addActionListener (new CPClientButtonListener (this));
        this.cpClientButton.setFocusable (false);
        this.cpClientButton.setEnabled (false);
        this.add (this.cpClientButton);

        // Text Field name of client
        CPClientTextFieldListener cpClientTextFieldListener = new CPClientTextFieldListener (
                this, this.cpClientButton);
        this.cpClientName = new JTextField ("");
        this.cpClientName.setBounds (170, 82, 200, 60);
        this.cpClientName.getDocument ().addDocumentListener (cpClientTextFieldListener);
        this.cpClientName.addKeyListener (cpClientTextFieldListener);
        this.add (this.cpClientName);

        this.setVisible (true);
    }
}

record CPServerButtonListener (SSMControlPanel ssmControlPanel) implements ActionListener {

    @Override
    public void actionPerformed (ActionEvent e) {
        ssmControlPanel.enableServer ();
    }
}

record CPClientButtonListener (SSMControlPanel ssmControlPanel) implements ActionListener {

    @Override
    public void actionPerformed (ActionEvent e) {
        SSMControlPanel.CLIENT_CONNECTED.add (new SSMCGraphics (ssmControlPanel.getNameOfClient ()));
        ssmControlPanel.resetNameOfClient ();
    }
}

record CPClientTextFieldListener (SSMControlPanel ssmControlPanel, JButton cpClientButton) implements DocumentListener, KeyListener {

    @Override public void insertUpdate (DocumentEvent e) { this.warn (); }

    @Override public void removeUpdate (DocumentEvent e) { this.warn (); }

    @Override public void changedUpdate (DocumentEvent e) { this.warn (); }

    public void warn () {
        String name = ssmControlPanel.getNameOfClient ();
        if (name.equals ("")) ssmControlPanel.setCreateClientEnabled (false);
        else ssmControlPanel.setCreateClientEnabled (ssmControlPanel.checkNameOfClient (name));
    }

    @Override public void keyTyped (KeyEvent e) {

    }

    @Override public void keyPressed (KeyEvent e) {

    }

    @Override public void keyReleased (KeyEvent e) {
        if (e.getKeyCode () == KeyEvent.VK_ENTER) cpClientButton.doClick ();
    }
}
