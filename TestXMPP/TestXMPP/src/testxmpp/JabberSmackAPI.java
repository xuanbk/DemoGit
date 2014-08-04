/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testxmpp;

import java.io.*;
import java.util.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class JabberSmackAPI {

    XMPPConnection connection;

    ChatManager chatManager;
    MyMessageListener myMessageListener;
    public void login(String userName, String password) throws XMPPException {
//        ClassLoader[] classLoader = 
//        Enumeration configEnum = getResources("META-INF/smack-config.xml");
//                while (configEnum.hasMoreElements()) 
        ConnectionConfiguration config = new ConnectionConfiguration("172.16.16.54", 5222);
        connection = new XMPPConnection(config);

        connection.connect();
        connection.login(userName, password);
        chatManager = connection.getChatManager();
         myMessageListener = new MyMessageListener();
    }

    public void sendMessage(String message, String to) throws XMPPException {
//    Chat chat = connection.getChatManager().createChat(to, this);
//    chat.sendMessage(message);
        
        Message msg = new Message(to, Message.Type.chat);
//        msg.setBody(message);
        connection.sendPacket(msg);
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, to));

        Chat chat = chatManager.createChat(to, myMessageListener);
        chat.sendMessage(message);

    }

    public void displayBuddyList() {
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for (RosterEntry r : entries) {
            System.out.println(r.getUser());
        }
    }

    public void createEntry(String user, String name) throws Exception {

        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));

        Roster roster = connection.getRoster();

        roster.createEntry(user, name, null);

    }

    public void disconnect() {
        connection.disconnect();
    }

    public void processMessage(Chat chat, Message message) {
        if (message.getType() == Message.Type.chat) {
            System.out.println(chat.getParticipant() + " says: " + message.getBody());
        } else {
            System.out.println(message.getFrom() + " says: " + message.getBody());
        }
    }
    public String CheckName(String name){
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        
        for (RosterEntry r : entries) {
          
            if (r.getUser().split("@")[0].equals(name)){
                return r.getUser();
            }
            
        }
        return null;
    }
    public static void main(String args[]) throws XMPPException, IOException, Exception {
        // declare variables

        JabberSmackAPI c = new JabberSmackAPI();
        //c.createEntry("user20@172.16.8.69", "pass");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;

        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = true;

        // Enter your login information here
        c.login("vu3d", "pass");

        c.displayBuddyList();

        System.out.println("-----");

        System.out.println("Who do you want to talk to? - Type contacts full email address:");
        String talkTo = br.readLine();

        System.out.println("-----");
        System.out.println("All messages will be sent to " + talkTo);
        System.out.println("Enter your message in the console:");
        System.out.println("-----\n");
        talkTo = c.CheckName(talkTo);
        while (!(msg = br.readLine()).equals("bye")) {
            c.sendMessage(msg, talkTo);
        }

        c.disconnect();
        System.exit(0);
    }

    class MyMessageListener implements MessageListener {

        @Override

        public void processMessage(Chat chat, Message message) {

            String from = message.getFrom();

            String body = message.getBody();

            System.out.println(String.format("Received message '%1$s' from %2$s", body, from));

        }

    }

}
