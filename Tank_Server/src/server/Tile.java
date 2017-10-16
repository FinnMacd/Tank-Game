package server;

public class Tile {
	
	private double health,maxHealth;
	private boolean isSolid,isDestructable,isShootable = false;
	private int id, frames;
	
	public static int numBlocks = 2;
	public static Tile block;
	public static Tile box;
	public static Tile blank;
	public static Tile invis;
	public static Tile start;
	public static Tile turret;
	
	public Tile(int id, boolean s){
		this.id = id;
		isSolid = isShootable = s;
	}
	
	public Tile(int id, boolean s, int f, int h){
		this.id = id;
		isSolid = isShootable = s;
		health = maxHealth = h;
		isDestructable = true;
		frames = f;
	}
	
	public boolean issolid(){
		return isSolid;
	}
	
	public boolean isshootable(){
		return isShootable;
	}
	
	public void setshootable(boolean shoot){
		isShootable = shoot;
	}
	
	public void hurt(double h){
		if(!isDestructable)return;
		health -= h;
	}
	
	public int id(){
		return id;
	}
	
	public static void loadTiles(){
		blank = new Tile(0,false);
		block = new Tile(1,true);
		box = new Tile(2,true,5,5);
		invis = new Tile(3,true);
		start = new Tile(4,false);
		turret = new Tile(5,false);
		invis.setshootable(false);
	}
	
	public String getCode(){
		String code = String.valueOf(id);
		
		if(isDestructable){
			if(health < 0)health = 0;
			if(health < 1)code+="0";
			code += String.valueOf((int)(health*10));
		}
		
		return code;
	}
	
	public static Tile newBox(){
		return new Tile(2, true, 5, 5);
	}
	
}
