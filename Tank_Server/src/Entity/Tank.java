package Entity;

import java.awt.Polygon;

public class Tank {

	private double turret_ang = 0, tank_ang = 0, x, y, health = 10.0;

	private final String UserName;

	private int kills, deaths;
	
	private boolean alive = true;

	public Tank(int x, int y, String UserName) {
		this.UserName = UserName;
		this.x = x;
		this.y = y;
	}

	public void setData(double x, double y, double ta, double tu, double h) {
		this.x = x;
		this.y = y;
		turret_ang = tu;
		tank_ang = ta;
		health = h;
		if(health <= 0)alive = false;
	}
	
	public void setKD(int k, int d) {
		kills = k;
		deaths = d;
	}

	public void hurt(double damage) {
		health -= damage;
	}

	public Polygon getPoly(double d) {
		int[] x = { (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7 + 180)) * getTankHeight() * d), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7 + 180)) * getTankHeight() * d) };

		int[] y = { (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7 + 180)) * getTankHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7 + 180)) * getTankHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7)) * getTankHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7)) * getTankHeight() * d) };

		return new Polygon(x, y, 4);
	}

	public Polygon getPoly() {
		int[] x = { (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7 + 180)) * getTankHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7 + 180)) * getTankHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7)) * getTankHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7)) * getTankHeight() * 0.66) };

		int[] y = { (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7 + 180)) * getTankHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7 + 180)) * getTankHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7)) * getTankHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7)) * getTankHeight() * 0.66) };

		return new Polygon(x, y, 4);
	}
	
	public String getString(){
		
		String message = "PO";
		
		message += UserName + ",";
		message += getX() + ",";
		message += getY() + ",";
		message += getTankAng() + ",";
		message += getTurretAng() + ",";
		message += getHealth();
		
		return message;
		
	}
	
	public double getX() {
		return Math.ceil(x * 10) / 10;
	}

	public double getY() {
		return Math.ceil(y * 10) / 10;
	}

	public double getTankAng() {
		return Math.ceil(tank_ang * 10) / 10;
	}

	public double getTurretAng() {
		return Math.ceil(turret_ang * 10) / 10;
	}

	public double getHealth() {
		return Math.ceil(health * 10) / 10;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public boolean isAlive() {
		return alive;
	}

	public String getUserName() {
		return UserName;
	}
	
	public int getTankHeight(){
		return 50;
	}
	
	public int getTankWidth(){
		return 40;
	}
	
}
