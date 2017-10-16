package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Server {
	
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<String> CurrentUsers = new ArrayList<String>();
	public static ArrayList<Server_Return> returns = new ArrayList<Server_Return>(); 
	public static ArrayList<String> BannedUsers = new ArrayList<String>();
	
	public static JFrame frame = new JFrame();
	public static JTextArea label = new JTextArea();
	public static JScrollPane sp = new JScrollPane();
	public static JList users = new JList();
	public static JScrollPane sp_users = new JScrollPane();
	public static JButton b_remove = new JButton();
	public static JButton b_ban = new JButton();
	
	public static String UserName;
	
	public static Game game;
	
	public static void main(String[] args) throws IOException{
		
		String ip = ""+InetAddress.getLocalHost();
		ip = ip.split("/")[1];
		frame.setTitle("System ip: " + ip);
		frame.setSize(670,480);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		sp.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(label);
		frame.getContentPane().add(sp);
		sp.setBounds(0, 0, 505, 446);
		
		sp_users.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp_users.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp_users.setViewportView(users);
		frame.getContentPane().add(sp_users);
		sp_users.setBounds(505, 0, 160, 180);
		
		frame.setVisible(true);
		
		b_remove.setText("Remove");
		b_remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(users.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(null, "Select a user");
					return;
				}
				String[] options = {"Yes","No"};
				int choise = JOptionPane.showOptionDialog(null, "Are you sure you want to remove " + CurrentUsers.get(users.getSelectedIndex()),
						"Confirm", 0, 0, null, options, options[0]);
				
				if(choise == 0){
					label.setText(label.getText() + "Client " +CurrentUsers.get(users.getSelectedIndex())+ " from: " + ConnectionArray.get(users.getSelectedIndex()).getLocalAddress().getHostName() + " was removed\n");
					try {
						//"RM" = remove
						String user = CurrentUsers.get(users.getSelectedIndex());
						if(user.contains(" (cheated)"))user = user.replace(" (cheated)", "");
						for(int i = 0; i < returns.size(); i++){
							if(returns.get(i).UserName.equals(user)){
								returns.get(i).Remove("RMYou were removed by the server");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						label.setText(label.getText() + "Error removing "+CurrentUsers.get(users.getSelectedIndex())+"\n");
					}
				}
				
			}
		});
		frame.add(b_remove);
		b_remove.setBounds(530, 200, 100, 60);
		
		b_ban.setText("Ban");
		b_ban.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(users.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(null, "Select a user");
					return;
				}
				String[] options = {"Yes","No"};
				int choise = JOptionPane.showOptionDialog(null, "Are you sure you want to ban " + CurrentUsers.get(users.getSelectedIndex()),
						"Confirm", 0, 0, null, options, options[0]);
				if(choise == 0){
					label.setText(label.getText() + "Client " +CurrentUsers.get(users.getSelectedIndex())+ " from: " + ConnectionArray.get(users.getSelectedIndex()).getLocalAddress().getHostName() + " was removed\n");
					try {
						//"RM" = remove
						String user = CurrentUsers.get(users.getSelectedIndex());
						if(user.contains(" (cheated)"))user = user.replace(" (cheated)", "");
						for(int i = 0; i < returns.size(); i++){
							if(returns.get(i).UserName.equals(user)){
								BannedUsers.add(returns.get(i).getAddres());
								returns.get(i).Remove("BNYou were banned by the server");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						label.setText(label.getText() + "Error banning "+CurrentUsers.get(users.getSelectedIndex())+"\n");
					}
				}
			}
		});
		frame.add(b_ban);
		b_ban.setBounds(530, 280, 100, 60);
		
		game = new Game();
		game.init();
		new Thread(game, "Game_Thread").start();
		
		try{
			
			final int PORT = 25526;
			ServerSocket SERVER = new ServerSocket(PORT);
			label.setText(label.getText() + "Internal ip: " + ip + "\n");
			try {
				URL whatismyip = new URL("http://checkip.amazonaws.com");
				BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

				String eip = in.readLine(); // you get the IP as a String
				label.setText(label.getText() + "External ip: " + eip + "\n");
			} catch (Exception e) {
				label.setText(label.getText() + "Could not load external ip \n");
			}
			label.setText(label.getText() + "Waiting for clients..." + "\n");
			
			searching:
			while(true){
				Socket Sock = SERVER.accept();
				
				String address = Sock.getLocalAddress().getHostName();
				
				for(int i = 0; i < BannedUsers.size();i++){
					if(BannedUsers.get(i).equals(address)){
						PrintWriter OUT = new PrintWriter(Sock.getOutputStream());
						OUT.println("BN");
						OUT.flush();
						OUT.close();
						Sock.close();
						System.out.println("Banned");
						continue searching;
					}
				}
				
				Game.map.setMapForSend();
				if(AddUserName(Sock)){
					Sock.close();
					System.out.println("name already used");
					continue;
				}
				ConnectionArray.add(Sock);
				
				PrintWriter OUT = new PrintWriter(Sock.getOutputStream());
				OUT.println(CurrentUsers);
				OUT.flush();
				
				OUT.println(Game.map.getMapForSend());
				OUT.flush();
				
				users.setListData(CurrentUsers.toArray());

				label.setText(label.getText() + "Client " +UserName+ " connected from: " + Sock.getLocalAddress().getHostName() + "\n");
				
				Server_Return CHAT = new Server_Return(Sock,UserName);
				returns.add(CHAT);
				
				Thread X = new Thread(CHAT,UserName);
				X.start();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public static boolean AddUserName(Socket X)throws IOException{
		Scanner Input = new Scanner(X.getInputStream());
		UserName = Input.nextLine();
		
		System.out.println(UserName + "au");
		
		for(int i = 0; i < CurrentUsers.size(); i++){
			if(UserName.equals(CurrentUsers.get(i))){
				PrintWriter OUT = new PrintWriter(X.getOutputStream());
				OUT.println("NO");
				OUT.flush();
				return true;
			}
		}
		
		CurrentUsers.add(UserName);
		
		if(UserName.contains(" (cheated)"))UserName = UserName.replace(" (cheated)", "");
		//"NU" is new user
		
		game.addTank(UserName);
		
		for(int i = 0; i < ConnectionArray.size();i++){
			Socket TEMP_SOCK = (Socket) ConnectionArray.get(i);
			PrintWriter OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
			OUT.println("NU" + UserName);
			OUT.flush();
		}
		return false;
	}

}
