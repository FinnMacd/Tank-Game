package GameState;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Entity.Bullet;
import Entity.Tank;
import Entity.Turret;
import Main.GamePanel;
import Screen.Background;
import Screen.Map;

public class SinglePlayerState extends PlayState{
	
	private static String path;
	private static boolean d;
	
	public SinglePlayerState(GameStateManager gsm) {
		super(gsm);
		
	}

	public void init() {
		map = new Map(this);
		
		bullets = new ArrayList<Bullet>();
		
		turrets = new ArrayList<Turret>();
		
		tank = new Tank( startx,starty,this);
		bg = new Background("/Backgrounds/Blank.png");
		map.loadMap(path,d);
		tank.setData(startx, starty, 0, 0, 10);
	}
	
	public static void setPath(String path,boolean d){
		SinglePlayerState.path = path;
		SinglePlayerState.d = d;
	}
	
	public void update() {
		tank.update();
		
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).update();			
			for(int p = 0; p < bullets.size(); p++){
				if(turrets.get(i).getRect().intersects(bullets.get(p).getRect())){
					turrets.get(i).hurt(bullets.get(p).getDamage());
					bullets.remove(p);
					p--;
				}
			}
		}
		
		for(int i = 0; i < bullets.size(); i++){
			if(!bullets.get(i).isAlive()){
				bullets.remove(i);
				i--;
				continue;
			}
			if(bullets.get(i).isExploded()){
				for(int p = 0; p < 6;p++){
					bullets.add(new Bullet(bullets.get(i).getX(),
							bullets.get(i).getY(),
							p*60,bullets.get(i).getShooter()));
				}
				bullets.remove(i);
				i--;
				continue;
			}
			
			if(tank.getPoly().intersects(bullets.get(i).getRect())){
				tank.hurt(bullets.get(i).getDamage());
				bullets.remove(i);
				i--;
				continue;
			}
			
			bullets.get(i).update();
		}
		
		map.slideScreen((GamePanel.WIDTH/2)-tank.getX(), (GamePanel.HEIGHT/2)-tank.getY());
		
	}
	
	public void draw(Graphics2D g) {
		bg.draw(g);
		for(int i = 0; i < bullets.size();i++){
			bullets.get(i).draw(g);
		}
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).draw(g);			
		}
		map.draw(g);
		tank.draw(g);
	}
	
	public void addTurret(int x, int y){
		turrets.add(new Turret(x*20,y*20,this));
		turrets.get(turrets.size()-1).setTank(tank);
	}
	
	public boolean loaded() {
		if(!loaded){
			loaded = true;
			return false;
		}
		return true;
	}
}
