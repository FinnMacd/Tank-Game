package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Entity.Bullet;
import Entity.Tank;
import Entity.Turret;
import Main.GamePanel;
import Screen.Background;
import Screen.Map;

public class MultiPlayerState extends PlayState implements Runnable{
	
	private static boolean join = false, cancle = false;
	
	private boolean joined = false;
	
	private String UserName;
	
	private Socket SOCK;
	private Scanner INPUT;
	private PrintWriter OUT;
	
	private ArrayList<Tank> players;
	
	private int infoOffSet = 0,kdoffset;
	
	private volatile Thread login, reciever;

	public MultiPlayerState(GameStateManager gsm) {
		super(gsm);
	}

	public void init() {
		map = new Map(this);
		
		turrets = new ArrayList<Turret>();
		
		players = new ArrayList<Tank>();
		getData("","","");
		bg = new Background("/Backgrounds/Blank.png");
		tank = new Tank(startx,starty,this);
		bullets = new ArrayList<Bullet>();
	}
	
	public void getData(String s,String i, String u) {
		
		login = new Thread(new Runnable(){
			public void run(){
				join = false;
				JFrame frame = new JFrame("Join a server");
				if(s.equals(""))
					frame.setSize(340, 200);
				else
					frame.setSize(340, 220);
				frame.setLayout(null);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setFocusable(true);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				JLabel L_IP = new JLabel();
				L_IP.setText("Enter IP:");
				frame.getContentPane().add(L_IP);
				L_IP.setBounds(10, 10, 80, 30);
				
				JLabel L_USERNAME = new JLabel();
				L_USERNAME.setText("Enter UserName:");
				frame.getContentPane().add(L_USERNAME);
				L_USERNAME.setBounds(10, 60, 100, 30);
				
				if(!s.equals("")){
					JLabel L_INFO = new JLabel();
					L_INFO.setText(s);
					L_INFO.setFont(new Font("TimesNewRoman",1,16));
					L_INFO.setForeground(Color.black);
					frame.getContentPane().add(L_INFO);
					L_INFO.setBounds(10, 150, 800, 30);
				}
				
				JTextField TF_IP = new JTextField();
				TF_IP.setText(i);
				frame.getContentPane().add(TF_IP);
				TF_IP.setBounds(120, 10, 200, 30);
				TF_IP.requestFocus();
				
				JTextField TF_USERNAME = new JTextField();
				TF_USERNAME.setText(u);
				frame.getContentPane().add(TF_USERNAME);
				TF_USERNAME.setBounds(120, 60, 200, 30);

				JButton B_JOIN = new JButton();
				B_JOIN.setText("Join");
				B_JOIN.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						join = true;
					}
				});
				frame.getContentPane().add(B_JOIN);
				B_JOIN.setBounds(180, 100, 120, 40);
				
				JButton B_CANCLE = new JButton();
				B_CANCLE.setText("Cancel");
				B_CANCLE.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancle = true;
					}
				});
				frame.getContentPane().add(B_CANCLE);
				B_CANCLE.setBounds(30, 100, 120, 40);

				frame.setVisible(true);
				
				while (!join) {
					if(cancle == true){
						cancle = false;
						loaded = false;
						frame.setVisible(false);
						frame.dispose();
						gsm.setState(GameStateManager.MenuState);
						return;
					}
					try {
						Thread.currentThread();
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				join = false;
				String user = TF_USERNAME.getText();
				String ip = TF_IP.getText();
				
				joinServer(user,ip);

				frame.setVisible(false);
				frame.dispose();
				
				return;
			}
		},"Login");
		
		login.start();
		
		
	}
	
	private void joinServer(String user, String ip){
		if(ip.equals("")){
			getData("Enter an IP address",ip,user);
			return;
		}
		if(user.length() > 16){
			getData("Username needs to be under 16 digits",ip,user);
			return;
		}
		if(user.isEmpty()){
			getData("Enter a username",ip,user);
			return;
		}
		String temp_user = user;
		if(user.length() != temp_user.replaceAll(",", "").length()){
			getData("Username cannot contain commas",ip,user);
			return;
		}
		try{
			final int PORT = 25526;
			final String HOST = ip;
			SOCK = new Socket(HOST,PORT);
			System.out.println("You connected to: " + HOST);
			
			UserName = user;
			
			INPUT = new Scanner(SOCK.getInputStream());
			OUT = new PrintWriter(SOCK.getOutputStream());
			if(!GamePanel.hasCheated)OUT.println(UserName);
			else OUT.println(UserName + " (cheated)");
			OUT.flush();
			
			String temp1 = INPUT.nextLine();
			System.out.println("reply: " + temp1);
			
			if(temp1.equals("NO")){
				SOCK.close();
				OUT.close();
				INPUT.close();
				INPUT = null;
				OUT = null;
				getData("Username already in use",ip,"");
				return;
			}
			
			if(temp1.equals("BN")){
				SOCK.close();
				OUT.close();
				INPUT.close();
				INPUT = null;
				OUT = null;
				getData("Banned from Server","","");
				return;
			}
			
			temp1 = temp1.replace("[", "");
			temp1 = temp1.replace("]", "");
			temp1.replaceAll(" ", "");
			
			String[] CurrentUsers = temp1.split(",");
			for(int i = 0; i < CurrentUsers.length; i++){
				if(CurrentUsers[i].equals(""))continue;
				if(CurrentUsers[i].startsWith(" "))CurrentUsers[i] = CurrentUsers[i].substring(1);
				if(CurrentUsers[i].equals(UserName))continue;
				players.add(new Tank(startx,starty,this,CurrentUsers[i]));
			}
			
			String data = INPUT.nextLine();
			map.loadMapFromString(data);
			
			joined = true;
			
			reciever = new Thread(this,"receiver");
			reciever.start();
			
		}catch(Exception e){
			e.printStackTrace();
			getData("Error connecting to server",ip,user);
			OUT.close();
			INPUT.close();
			try {
				SOCK.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			INPUT = null;
			OUT = null;
			if(reciever != null)reciever.stop();
		}
	}
	
	public void update(){
		infoOffSet++;
		if(!joined)return;
		tank.update();
		for(int i = 0; i < players.size();i++){
			players.get(i).update();
			for(int p = 0; p < bullets.size(); p++){
				if(players.get(i).getPoly().intersects(bullets.get(p).getRect()) && players.get(i).getHealth() > 0){
					bullets.remove(p);
				}
			}
		}
		for(int p = 0; p < bullets.size(); p++){
			if(!bullets.get(p).isAlive()){
				bullets.remove(p);
				p--;
				continue;
			}
			if(bullets.get(p).isExploded()){
				for(int i = 0; i < 6;i++){
					bullets.add(new Bullet(bullets.get(p).getX(),
							bullets.get(p).getY(),
							i*60,bullets.get(p).getShooter()));
				}
				bullets.remove(p);
				p--;
				continue;
			}
			if(tank.getPoly().intersects(bullets.get(p).getRect()) && tank.isAlive()){
				tank.hurt(bullets.get(p).getDamage());
				if(tank.getHealth() <= 0){
					//"KL" = kill
					OUT.println("KL" + bullets.get(p).getShooter());
					OUT.flush();
					if(UserName.equals(bullets.get(p).getShooter()))
						tank.setKD(tank.getKills()+1, tank.getDeaths());
				}
				bullets.remove(p);
				p--;
				continue;
			}
			bullets.get(p).update();
		}
		
		map.slideScreen((GamePanel.WIDTH/2)-tank.getX(), (GamePanel.HEIGHT/2)-tank.getY());
		
		//"PO" = position
		//"KD" = Kill/Death
		//if(infoOffSet < 3)return;
		kdoffset++;
		infoOffSet = 0;
		OUT.println("PO"+UserName +","+tank.getX()+","+tank.getY()+","+tank.getTankAng()+","+tank.getTurretAng() +","+ tank.getHealth());
		if(kdoffset > 10){
			kdoffset = 0;
			OUT.println("KD"+UserName +","+tank.getKills()+","+tank.getDeaths());
		}
		OUT.flush();
	}
	
	public void run(){
		long start = System.currentTimeMillis(),time;
		while(joined){
			if(!GamePanel.thread.isAlive())return;
			time = System.currentTimeMillis();
			if(time - start >= 1){
				RECEIVE();
				start = time;
			}
		}
	}
	
	public void RECEIVE() {
		if (INPUT.hasNextLine()) {
			String input = INPUT.nextLine();
			String in[] = input.split("_");
			
			for(String message:in){
				//if(message.split("").length < 2)continue;
				switch(message.substring(0, 2)){
					case "PO":
						//position
						message = message.substring(2);
						if(message.startsWith(UserName))break;
						String[] data4 = message.split(",");
						for(int i = 0; i < players.size(); i++){
							if(data4[0].equals(players.get(i).getUserName())){
								players.get(i).setData(Double.parseDouble(data4[1]), Double.parseDouble(data4[2]),
										Double.parseDouble(data4[3]), Double.parseDouble(data4[4]), Double.parseDouble(data4[5]));
								//System.out.println("Player pos updated");
								break;
							}
						}
						break;
					case "TU":
						message = message.substring(2);
						String[] data5 = message.split(",");
						turrets.get(Integer.parseInt(data5[0])).update(Double.parseDouble(data5[1]));
						break;
					case "NU":
						//new user
						message = message.substring(2);
						players.add(new Tank(startx, starty, this, message));
						System.out.println("added new user " + message);
						break;
					case "RU":
						//remove user
						message = message.substring(2);
						for(int i = 0; i < players.size(); i++){
							if(message.equals(players.get(i).getUserName())){
								players.remove(i);
								break;
							}
						}
						break;
					case "BU":
						//new bullet
						message = message.substring(2);
						String[] data = message.split(",");
						addEBullet(Double.parseDouble(data[0]),Double.parseDouble(data[1]),
								Double.parseDouble(data[2]),data[3]);
						break;
					case "EB":
						//new explosive bullet
						message = message.substring(3);
						String[] data3 = message.split(",");
						addEBullet(Double.parseDouble(data3[0]),Double.parseDouble(data3[1]),
								Double.parseDouble(data3[2]),Double.parseDouble(data3[3]),
								Double.parseDouble(data3[4]),data3[5]);
						break;
					case "RM":
						//remove yourself
						message = message.substring(2);
						loaded = false;
						joined = false;
						try {
							SOCK.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						gsm.setState(GameStateManager.MenuState);
						JOptionPane.showMessageDialog(null, message);
						break;
					case "BN":
						//you were banned
						message = message.substring(2);
						loaded = false;
						joined = false;
						try {
							SOCK.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						gsm.setState(GameStateManager.MenuState);
						JOptionPane.showMessageDialog(null, message);
						break;
					case "KD":
						//kill to death ratio
						message = message.substring(2);
						String[] data2 = message.split(",");
						for(int i = 0; i < players.size(); i++){
							if(data2[0].equals(players.get(i).getUserName())){
								players.get(i).setKD(Integer.parseInt(data2[1]), Integer.parseInt(data2[2]));
								break;
							}
						}
						break;
					case "KL":
						//you killed
						message = message.substring(2);
						if(UserName.equals(message)){
							tank.setKD(tank.getKills()+1, tank.getDeaths());
						}
						break;
				}
			}
		}
	}
	
	public void addBullet(double x, double y, double ang){
		if(bullets == null)return;
		x = Math.ceil(x * 10)/10;
		y = Math.ceil(y * 10)/10;
		ang = Math.ceil(ang * 10)/10;
		//"BU" = bullet
		OUT.println("BU"+x + "," + y + "," + ang + "," + UserName);
		OUT.flush();
	}

	public void addBullet(double x, double y, double ang, double dx, double dy){
		if(bullets == null)return;
		x = Math.ceil(x * 10)/10;
		y = Math.ceil(y * 10)/10;
		ang = Math.ceil(ang * 10)/10;
		dx = Math.ceil(dx * 10)/10;
		dy = Math.ceil(dy * 10)/10;
		//"EBU" = explosive bullet
		OUT.println("EBU"+x + "," + y + "," + ang + "," + dx + "," + dy + "," + UserName);
		OUT.flush();
	}
	
	public void addEBullet(double x, double y, double ang, String user){
		if(bullets == null)return;
		x = Math.ceil(x * 10)/10;
		y = Math.ceil(y * 10)/10;
		ang = Math.ceil(ang * 10)/10;
		bullets.add(new Bullet(x,y,ang,user));
	}

	public void addEBullet(double x, double y, double ang, double dx, double dy, String user){
		if(bullets == null)return;
		x = Math.ceil(x * 10)/10;
		y = Math.ceil(y * 10)/10;
		ang = Math.ceil(ang * 10)/10;
		dx = Math.ceil(dx * 10)/10;
		dy = Math.ceil(dy * 10)/10;
		bullets.add(new Bullet(x,y,ang,dx,dy,user));
	}
	
	public void addTurret(int x, int y){
		turrets.add(new Turret(x*20,y*20,this,turrets.size()));
	}
	
	public void draw(Graphics2D g) {
		if(!map.hasLoaded)return;
		bg.draw(g);
		map.draw(g);
		for(int i = 0; i < players.size();i++){
			players.get(i).draw(g);
		}
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(g);
		}
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).draw(g);			
		}
		tank.draw(g);
		g.setFont(new Font("TimesNewRoman",1,16));
		g.setColor(Color.black);
		g.drawString("Kills: " + tank.getKills(), 545, 20);
		g.drawString("Deaths: " + tank.getDeaths(), 545, 40);
		
	}

	public boolean loaded() {
		if(!loaded){
			loaded = true;
			return false;
		}
		return true;
	}

}
