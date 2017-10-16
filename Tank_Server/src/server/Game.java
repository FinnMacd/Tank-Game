package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import Entity.Bullet;
import Entity.Tank;
import Entity.Turret;

public class Game implements Runnable{

	private ArrayList<Bullet> bullets;
	private ArrayList<Bullet> newBullets;
	
	private ArrayList<Turret> turrets;
	
	private ArrayList<Tank> tanks;
	
	private HashMap<String,Integer> tankID;
	
	private String kills = "";
	
	public static Map map;

	public int startX = 0, startY = 0;

	private static String path;
	private static boolean d;

	public void init() {
		
		Tile.loadTiles();
		
		path = "/maps/MultiPlayer";
		//path = "/maps/EasyMap";
		d = true;
		
		map = new Map(this);

		bullets = new ArrayList<Bullet>();
		newBullets = new ArrayList<Bullet>();

		turrets = new ArrayList<Turret>();

		tanks = new ArrayList<Tank>();
		
		tankID = new HashMap<String, Integer>();

		map.loadMap(path, d);
		map.setMapForSend();
	}
	
	public void run(){
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0/60.0;
		double catchUp = 0;
		
		while(true){
			
			long now = System.nanoTime();
			catchUp += (now-lastTime)/ns;
			lastTime = now;
			
			while(catchUp >= 1){
				update();
				send();
				catchUp--;
			}
			
		}
	}
	
	public void update() {

		for (int i = 0; i < turrets.size(); i++) {
			turrets.get(i).update();
			for (int p = 0; p < bullets.size(); p++) {
				if (turrets.get(i).getRect().intersects(bullets.get(p).getRect())) {
					turrets.get(i).hurt(bullets.get(p).getDamage());
					bullets.remove(p);
					p--;
				}
			}
		}
		
		bullet:
		for (int i = 0; i < bullets.size(); i++) {
			if (!bullets.get(i).isAlive()) {
				bullets.remove(i);
				i--;
				continue;
			}
			if (bullets.get(i).isExploded()) {
				for (int p = 0; p < 6; p++) {
					bullets.add(new Bullet(bullets.get(i).getX(), bullets.get(i).getY(), p * 60, bullets.get(i).getShooter()));
				}
				bullets.remove(i);
				i--;
				continue;
			}
			for (int p = 0; p < tanks.size(); p++) {
				Tank tank = tanks.get(p);
				if (tank.getPoly().intersects(bullets.get(i).getRect())) {
					tank.hurt(bullets.get(i).getDamage());
					bullets.remove(i);
					i--;
					continue bullet;
				}
			}

			bullets.get(i).update();
		}

	}
	
	public void send(){
		
		String MESSAGE = "";
		
		for(int i = 0; i < newBullets.size(); i++){
			MESSAGE += newBullets.get(i).getString();
			MESSAGE += "_";
			bullets.add(newBullets.get(i));
		}
		newBullets.clear();
		
		for(Tank tank:tanks){
			
			MESSAGE += tank.getString();
			MESSAGE += "_";
			
		}
		
		MESSAGE += kills;
		kills = "";
		
		for(Turret t:turrets){
			MESSAGE += t.getInfo() + "_";
		}
		
		if(MESSAGE.endsWith("_"))MESSAGE = MESSAGE.substring(0, MESSAGE.length()-1);
		
		if(!MESSAGE.equals("")){
			for (int i = 0; i < Server.ConnectionArray.size(); i++) {
				try {
					Socket TEMP_SOCK = (Socket) Server.ConnectionArray.get(i);
					PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
					TEMP_OUT.println(MESSAGE);
					TEMP_OUT.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public void addTurret(int x, int y) {
		
		turrets.add(new Turret(x * 20, y * 20, this, turrets.size()));
		setTurrets();
		
	}
	
	public Tank[] getTanks(){
		
		Tank[] tankArry = new Tank[tanks.size()];
		
		for(int i = 0; i < tankArry.length; i++){
			tankArry[i] = tanks.get(i);
		}
		
		return tankArry;
		
	}
	
	public void setTurrets(){
		
		for(Turret t : turrets){
			t.setTanks(getTanks());
		}
		
	}
	
	public void addTank(String userName){
		tanks.add(new Tank(startX, startY, userName));
		tankID.put(userName, tanks.size());
		setTurrets();
	}
	
	public void removeTank(String userName){
		tanks.remove(tankID.get(userName));
		tankID.clear();
		for(int i = 0; i < tanks.size(); i++){
			tankID.put(tanks.get(i).getUserName(), i);
		}
		setTurrets();
	}
	
	public void addBullet(double x, double y, double ang, String userName){
		if(newBullets == null)return;
		newBullets.add(new Bullet(x,y,ang,userName));
	}

	public void addBullet(double x, double y, double ang, double dx, double dy, String userName){
		if(newBullets == null)return;
		newBullets.add(new Bullet(x,y,ang,dx,dy,userName));
	}
	
	public void setStart(int x, int y){
		startX = x;
		startY = y;
	}
	
	public void receive(String message){
		if(message.startsWith("BU")){
			//new bullet
			message = message.substring(2);
			String[] data = message.split(",");
			addBullet(Double.parseDouble(data[0]),Double.parseDouble(data[1]),
					Double.parseDouble(data[2]),data[3]);
		}else if(message.startsWith("EBU")){
			//new explosive bullet
			message = message.substring(3);
			String[] data = message.split(",");
			addBullet(Double.parseDouble(data[0]),Double.parseDouble(data[1]),
					Double.parseDouble(data[2]),Double.parseDouble(data[3]),
					Double.parseDouble(data[4]),data[5]);
		}else if(message.startsWith("PO")){
			//position
			message = message.substring(2);
			String[] data = message.split(",");
			for(int i = 0; i < tanks.size(); i++){
				if(data[0].equals(tanks.get(i).getUserName())){
					tanks.get(i).setData(Double.parseDouble(data[1]), Double.parseDouble(data[2]),
							Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5]));
					break;
				}
			}
		}else if(message.startsWith("KD")){
			//kills and deaths
			message = message.substring(2);
			String[] data = message.split(",");
			for(int i = 0; i < tanks.size(); i++){
				if(data[0].equals(tanks.get(i).getUserName())){
					tanks.get(i).setKD(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
					break;
				}
			}
		}else if(message.startsWith("KL")){
			
			kills += message + "_";
			
		}
	}
}