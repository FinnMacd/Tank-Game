package Entity;

import java.awt.Rectangle;

import server.Game;

public class Bullet {
	
	private double x,y,vx,vy,dx,dy,speed = 3;
	private double damage = 0.6, angle;
	private boolean alive = true,explosive = false,exploded = false;
	private String shooter;
	
	private int width = 6, height = 6;
	
	
	public Bullet(double x, double y, double ang,String user){
		shooter = user;
		vy = Math.cos(Math.toRadians(-ang))*speed;
		vx = Math.sin(Math.toRadians(-ang))*speed;
		this.x = x;
		this.y = y;
		angle = ang;
	}
	
	public Bullet(double x, double y, double ang, double dx, double dy, String user){
		shooter = user;
		vy = Math.cos(Math.toRadians(-ang))*speed;
		vx = Math.sin(Math.toRadians(-ang))*speed;
		this.x = x;
		this.y = y;
		explosive = true;
		this.dx = dx;
		this.dy = dy;
		damage = 0.8;
		angle = ang;
	}
	
	public void update(){
		x += vx;
		y += vy;
		if(x < 0||x > Game.map.width*20||y<0||y>Game.map.height*20)alive = false;
		
		if(Game.map.isShootable((int)x/20, (int)y/20)){
			alive = false;
			Game.map.hurtBlock((int)x/20, (int)y/20,damage);
		}
		
		if(explosive && ((vx < 0 && x < dx)||(vx > 0 && x > dx))){
			exploded = true;
		}
		
	}
	
	public Rectangle getRect(){
		return new Rectangle((int)x,(int)y,width,height);
	}
	
	public String getString(){
		
		String str = "";
		
		str = (explosive)?"EBU":"BU";
		
		str += (int)x + ",";
		str += (int)y + ",";
		str += (int)angle + ",";
		
		if(explosive){
			str += (int)dx + ",";
			str += (int)dy + ",";
		}
		
		str += shooter;
		
		return str;
		
	}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getDamage(){return damage;}
	public boolean isAlive(){return alive;}
	public boolean isExplosive(){return explosive;}
	public boolean isExploded(){return exploded;}
	public String getShooter(){return shooter;}
	
}
