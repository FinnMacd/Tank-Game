package Entity;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Random;

import server.Game;

public class Turret {
	
	private double x, y,health = 15,ang,shootTime, xoffset, yoffset, destang;
	private int ran,turnTime,turnTimer;
	private boolean alive = true,hastarget = false, onTarget = false;
	private Game game;
	
	private AffineTransform trans;
	
	private static Tank[] tanks;
	private Tank target;
	
	private static Random turn = new Random();
	
	private int id;
	
	public Turret(double x, double y,Game game,int id){
		this.x = x+20;
		this.y = y+20;
		this.game = game;
		this.id = id;
	}
	
	public void update(){
		getTarget();
		if (hastarget) {
			shootTime++;
			xoffset = target.getX() - x;
			yoffset = target.getY() - y;
			destang = Math.toDegrees(Math.atan(yoffset / xoffset)) - 90;
			if(xoffset >= 0)destang = 360 + destang;
			else destang = 180 + destang;
			if(ang - destang > 180)destang += 360;
			else if(destang - ang > 180)ang += 360;
			if(destang < ang - 95 || destang > ang + 95){
				hastarget = false;
			}
		}
		if(hastarget){
			if(destang < ang - 10 || destang > ang + 10){
				onTarget = false;
			}else onTarget = true;
			if(ang < destang)ang++;
			if(ang > destang)ang--;
			ang = ang % 360;
			
			if (health <= 0) alive = false;

			if (shootTime >= 40 && onTarget) {
				game.addBullet(x + Math.sin(Math.toRadians(-ang + 10)) * 45 * 0.75, y + Math.cos(Math.toRadians(ang - 10)) * 45 * 0.75, ang, " ");
				game.addBullet(x + Math.sin(Math.toRadians(-ang - 10)) * 45 * 0.75, y + Math.cos(Math.toRadians(ang + 10)) * 45 * 0.75, ang, " ");
				shootTime = 0;
			}
		}else{
			turnTimer++;
			if(turnTimer > turnTime){
				ran = turn.nextInt(4);
				turnTime = 30 + turn.nextInt(100);
				turnTimer = 0;
			}
			if(ran == 1)ang+=0.5;
			if(ran == 2)ang-=0.5;
		}
		
	}
	
	private void getTarget(){
		hastarget = false;
		int maxDist = 300;
		tankLoop:
		for(Tank tank : tanks){
			int dist = (int)Math.sqrt(Math.pow(tank.getX() - x, 2) + Math.pow(tank.getY() - y, 2));
			//System.out.println(dist);
			if (dist < maxDist && tank.isAlive()) {
				int tx = (int) tank.getX();
				int ty = (int) tank.getY();
				int[] xp = { (int) x, (int) x + 1, tx, tx + 1 };
				int[] yp = { (int) y, (int) y + 1, ty, ty + 1 };
				Polygon l = new Polygon(xp, yp, 4);
				for (int yc = (int) (y / 20) - 15; yc < (int) (y / 20) + 15; yc++) {
					if (ty > y && yc < (int) (y / 20)) continue;
					if (ty < y && yc > (int) (y / 20)) break;
					for (int xc = (int) (x / 20) - 15; xc < (int) (x / 20) + 15; xc++) {
						if (tx > x && xc < (int) (x / 20)) continue;
						if (tx < x && xc > (int) (x / 20)) break;
						if (l.intersects(new Rectangle(xc * 20, yc * 20, 20, 20))) {
							if (Game.map.isShootable(xc, yc)) {
								continue tankLoop;
							}
						}
					}
				}
				maxDist = dist;
				target = tank;
				hastarget = true;
			}
		}
		
	}
	
	public Turret setTanks(Tank[] t){
		tanks = t;
		System.out.println("set targets " + t.length);
		getTarget();
		return this;
	}
	
	public void hurt(double hurt){
		health -= hurt;
	}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getAng(){return ang;}
	public Rectangle getRect(){
		return new Rectangle((int)x-20,(int)y-20,40,40);
	}
	public boolean isAlive(){return alive;}
	
	public void setPos(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public String getInfo(){
		
		String info = "TU";
		
		info += id + ",";
		info += Math.ceil(ang*10)/10;
		
		return info;
	}
	
}
