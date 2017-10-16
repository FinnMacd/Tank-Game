package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Server_Return implements Runnable {

	// Globals
	Socket SOCK;
	private Scanner INPUT;
	private PrintWriter OUT;
	String MESSAGE = "";
	final String UserName;
	boolean running = true;
	private String address;

	public Server_Return(Socket X,String UserName) {
		SOCK = X;
		this.UserName = UserName;
		address = SOCK.getLocalAddress().getHostName();
	}

	public void Remove(String s) throws IOException {
		running = false;
		
		if(!s.isEmpty()){
			OUT.println(s);
			OUT.flush();
		}
		
		Server.game.removeTank(UserName);
		
		for (int i = 0; i < Server.ConnectionArray.size(); i++) {

			if (Server.ConnectionArray.get(i) == SOCK) {

				for (int p = 0; p < Server.ConnectionArray.size() - 1; p++) {
					if (p == i) continue;
					Socket TEMP_SOCK = (Socket) Server.ConnectionArray.get(p);
					PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
					TEMP_OUT.println("RU" + Server.CurrentUsers.get(i));
					TEMP_OUT.flush();
				}

				Server.ConnectionArray.remove(i);
				Server.CurrentUsers.remove(i);
				
				Server.users.setListData(Server.CurrentUsers.toArray());
			}
		}
		if(s.isEmpty())
		Server.label.setText(Server.label.getText() + "Client " + UserName+ " left from " + address + "\n");
	}

	public void run() {

		try {
			try {
				INPUT = new Scanner(SOCK.getInputStream());
				OUT = new PrintWriter(SOCK.getOutputStream());

				while (running) {
					
					if (!SOCK.isConnected() && running) {
						Remove("");
						System.out.println("conection lost with " + UserName);
						break;
					}

					if (!INPUT.hasNext() && running) {
						Remove("");
						System.out.println("lost connection with "+UserName);
						break;
					}
					
					if(running)
						MESSAGE = INPUT.nextLine();
					
					Server.game.receive(MESSAGE);
					
//					for (int i = 0; i < Server.ConnectionArray.size(); i++) {
//						if (Server.ConnectionArray.get(i) == SOCK) continue;
//						Socket TEMP_SOCK = (Socket) Server.ConnectionArray.get(i);
//						PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
//						TEMP_OUT.println(MESSAGE);
//						TEMP_OUT.flush();
//					}
				}

			} finally {
				if(SOCK.isConnected()){
					SOCK.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < Server.returns.size(); i++){
			if(Server.returns.get(i) == this)Server.returns.remove(i);
		}
		
	}
	
	public String getAddres(){
		return address;
	}

}