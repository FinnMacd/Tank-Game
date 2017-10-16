package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import GameState.PlayState;
import Main.GamePanel;
import Screen.Images;

public class Bullet {
	
	private BufferedImage bullet,ebullet;
	
	private double x,y,vx,vy,dx,dy,speed = 3;
	private double damage = 0.6, angle;
	private boolean alive = true,explosive = false,exploded = false;
	private String shooter;
	
	
	public Bullet(double x, double y, double ang,String user){
		shooter = user;
		vy = Math.cos(Math.toRadians(-ang))*speed;
		vx = Math.sin(Math.toRadians(-ang))*speed;
		this.x = x;
		this.y = y;
		bullet = Images.bullet;
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
		bullet = Images.bullet;
		ebullet = Images.ebullet;
		angle = ang;
	}
	
	public void update(){
		x += vx;
		y += vy;
		if(x < 0||x > PlayState.map.width*20||y<0||y>PlayState.map.height*20)alive = false;
		
		if(PlayState.map.isShootable((int)x/20, (int)y/20)){
			alive = false;
			PlayState.map.hurtBlock((int)x/20, (int)y/20,damage);
		}
		
		if(explosive && ((vx < 0 && x < dx)||(vx > 0 && x > dx))){
			exploded = true;
		}
		
	}
	
	public void draw(Graphics2D g){
		if(!explosive)
			g.drawImage(bullet, (int)x + (int)PlayState.map.x -bullet.getWidth()/2, (int)y + (int)PlayState.map.y -bullet.getHeight()/2, null);
		else
			g.drawImage(ebullet, (int)x + (int)PlayState.map.x -ebullet.getWidth()/2, (int)y + (int)PlayState.map.y -ebullet.getHeight()/2, null);
	}
	
	public Rectangle getRect(){
		return new Rectangle((int)x,(int)y,bullet.getWidth(),bullet.getHeight());
	}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getDamage(){return damage;}
	public boolean isAlive(){return alive;}
	public boolean isExplosive(){return explosive;}
	public boolean isExploded(){return exploded;}
	public String getShooter(){return shooter;}
	
}
