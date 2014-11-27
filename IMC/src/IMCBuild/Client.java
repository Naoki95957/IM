package IMCBuild;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

public class Client extends JFrame{

	private final int port;
	private JTextField text;
	private JTextField userName;
	private JTextArea chatBox;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host,int PORT){
		super("Chatty chat room: Client");
		port=PORT;
		userName = new JTextField("User name",10);
		userName.setEditable(true);
		text = new JTextField();
		text.setEditable(false);
		text.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				sendMessage(event.getActionCommand());
				text.setText("");
			}
		}
		);
		add(userName, BorderLayout.NORTH);
		add(text, BorderLayout.SOUTH);
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		add(new JScrollPane(chatBox));
		setSize(400,250);
		setVisible(true);
	}
	public void starting(){
		showMessage("To close type \"END\" then exit out.");
		boolean connected = false;
		try{
			do{
			try{
			connect();
			connected=true;
			}catch(EOFException eofException){
				showMessage("\nSorry, the server ended connection");
			}catch(IOException ioException){
			}
			}while(!connected);
			setStreams();
			whileChat();
		}catch(EOFException eofException){
			showMessage("\nThe connection ended");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			close();
		}
	}
	public void connect() throws IOException{
		if(chatBox.getText().contains("Attempting connection")){
			if(chatBox.getText().contains(" . . .")){
				chatBox.setText("To close type \"END\" then exit out.\nAttempting connection");
			}else{
				showMessage(" .");
			}
		}else{
			showMessage("\nAttempting connection");
		}
		connection = new Socket(InetAddress.getByName(serverIP), port);
		showMessage("\nConnected to: "+connection.getInetAddress().getHostName());
	}
	public void setStreams() throws IOException{
		out = new ObjectOutputStream(connection.getOutputStream());
		out.flush();
		in = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStream setup is now made! \n");
	}
	public void whileChat() throws IOException{
		type(true);
		String serverName ="SERVER";
		do{
			try{
				message = (String) in.readObject();
				Scanner temp = new Scanner(message);
				temp.useDelimiter(" - ");
				serverName=temp.next();
				showMessage("\n"+message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nError receiving message! :(");
			}
		}while(!message.equals(serverName+" - END"));
	}
	public void close(){
		showMessage("\nClosing connections...");
		type(false);
		try{
			out.close();
			in.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public void sendMessage(String message){
		try{
			out.writeObject(userName.getText()+" - "+message);
			out.flush();
			showMessage("\n"+userName.getText()+" - "+message);
		}catch(IOException ioException){
			chatBox.append("\nError sending message! :(");
		}
	}
	public void showMessage(final String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatBox.append(message);
				}
			}
		);
	}
	public void type(final boolean ToF){
	SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					text.setEditable(ToF);
				}
			}
		);
	}
}
