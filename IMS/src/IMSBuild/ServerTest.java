package IMSBuild;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerTest {

	public static void main(String[] args){
		boolean confirmedPort = false;
		int portNum = 0;
		while(!confirmedPort){
			Object port = JOptionPane.showInputDialog(null, "Enter a vild port number of 4 digits (MUST NOT BE \"0000\"): ", "PORT NUMBER", JOptionPane.QUESTION_MESSAGE);
			try{
				portNum = Integer.parseInt((String)port);
				if(((String)port).length()==4&&!((String)port).equals("0000")){
					confirmedPort=true;
				}else{
					int response = JOptionPane.showConfirmDialog(null, "You must enter a valid port number. Do you wish to retry?", "CANCEL/INVLID PORT",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(response == JOptionPane.NO_OPTION){
						return;
					}
				}
			}catch(Exception e){
				int response = JOptionPane.showConfirmDialog(null, "You must enter a valid port number. Do you wish to retry?", "CANCEL/INVLID PORT",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(response == JOptionPane.NO_OPTION){
					return;
				}
			}
		}
		Server test = new Server(portNum);
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.starting();
	}
	
}
