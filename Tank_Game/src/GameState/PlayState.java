package GameState;

import java.awt.Cursor;
import java.util.ArrayList;

import Entity.Bullet;
import Entity.Tank;
import Entity.Turret;
import Screen.Map;

public abstract class PlayState extends GameState{
	
	protected ArrayList<Bullet> bullets;
	protected ArrayList<Turret> turrets;
	public static Map map;
	
	public int startx = 0,starty = 0;
	
	protected Tank tank;
	
 	public PlayState(GameStateManager gsm) {
		super(gsm);
		cursor = Cursor.CROSSHAIR_CURSOR;
	}

	public void addBullet(double x, double y, double ang){
		if(bullets == null)return;
		bullets.add(new Bullet(x,y,ang,""));
	}

	public void addBullet(double x, double y, double ang, double dx, double dy){
		if(bullets == null)return;
		bullets.add(new Bullet(x,y,ang,dx,dy,""));
	}
 	
	public void addTurret(int x, int y){
		
	}
	
	public void setStart(int x, int y){
		startx = 20*x;
		starty = 20*y;
		tank.setPos(x*20, y*20);
		System.out.println(x + " " + y);
	}
	
}
