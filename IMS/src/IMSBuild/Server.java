package IMSBuild;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

public class Server extends JFrame{

	private final int port;
	private JTextField text;
	private JTextField userName;
	private JTextArea chatBox;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ServerSocket server;
	private Socket connection;
	
	public Server(int PORT){
		super("Chatty chat room: Host");
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
		try{
			server = new ServerSocket(port, 100);
			String portString=""+port;
			if(port<1000){
				portString="0"+portString;
			}
			if(port<100){
				portString="0"+portString;
			}
			if(port<10){
				portString="0"+portString;
			}
			while(true){
				try{
					showMessage("IP Address: "+InetAddress.getLocalHost().getHostAddress()+"\nPort: "+portString);
					findConnection();
					setStreams();
					whileChat();
				}catch(EOFException eofException){
					showMessage("\nThe connection ended");
				}finally{
					close();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	private void findConnection()throws IOException{
		showMessage("\nWaiting for connection . . .");
		connection = server.accept();
		showMessage("\nConnected to "+connection.getInetAddress().getHostName());
	}
	private void setStreams()throws IOException{
		out = new ObjectOutputStream(connection.getOutputStream());
		out.flush();
		in = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStream setup is now made! \n");
	}
	private void whileChat()throws IOException{
		String message = "Your now connected!";
		sendMessage(message);
		String clientName ="CLIENT";
		type(true);
		do{
			try{
				message = (String) in.readObject();
				Scanner temp = new Scanner(message);
				temp.useDelimiter(" - ");
				clientName=temp.next();
				showMessage("\n"+message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nError receiving message! :(");
			}
		}while(!message.equals(clientName+" - END"));
	}
	private void close(){
		showMessage("\nClosing connections...\n");
		type(false);
		try{
			out.close();
			in.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	private void sendMessage(String message){
		try{
			if(message.equals("Your now connected!")){
				out.writeObject("Your now connected to the host!");
				out.flush();
				showMessage("\nClient connected!");
			}else{
			out.writeObject(userName.getText()+" - "+ message);
			out.flush();
			showMessage("\n"+userName.getText()+" - "+message);
			}
		}catch(IOException ioException){
			chatBox.append("\n Error sending message! :(");
		}
	}
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatBox.append(text);
				}
			}	
		);
	}
	private void type(final boolean ToF){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					text.setEditable(ToF);
				}
			}	
		);
	}
}


