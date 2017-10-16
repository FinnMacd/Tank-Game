package Screen;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Tile {
	
	private double health,maxHealth;
	private boolean issolid,isdestructable,isshootable;
	private BufferedImage image;
	private BufferedImage[] frames;
	private int id;
	
	public static int numBlocks = 2;
	public static Tile block;
	public static Tile box;
	public static Tile blank;
	public static Tile invis;
	public static Tile start;
	
	public Tile(BufferedImage i, int id, boolean s){
		image = i;
		this.id = id;
		issolid = isshootable = s;
	}
	
	public Tile(BufferedImage bi, int id, boolean s, int f, int h){
		image = bi;
		this.id = id;
		issolid = isshootable = s;
		frames = new BufferedImage[f];
		for(int i = 0; i < f; i++){
			frames[i] = bi.getSubimage(i*20, 0, 20, 20);
		}
		health = maxHealth = h;
		image = frames[0];
		isdestructable = true;
	}
	
	public boolean issolid(){
		return issolid;
	}
	
	public boolean isshootable(){
		return isshootable;
	}
	
	public boolean isDestructable(){
		return isdestructable;
	}
	
	public void setshootable(boolean shoot){
		isshootable = shoot;
	}
	
	public void hurt(double h){
		if(!isdestructable)return;
		health -= h;
		if(health <= 0){
			issolid = isshootable = false;
			image = frames[frames.length-1];
		}else{
			double hp = (health/maxHealth);
			double fp = ((double)frames.length-1);
			image = frames[(int)(frames.length-1-(hp*fp))];
		}
	}
	
	public Tile setHealth(double h){
		health = h;
		if(health <= 0){
			issolid = isshootable = false;
			image = frames[frames.length-1];
		}else{
			double hp = (health/maxHealth);
			double fp = ((double)frames.length-1);
			image = frames[(int)(frames.length-1-(hp*fp))];
		}
		return this;
	}
	
	public Image getImage(){
		return image;
	}
	
	public int id(){
		return id;
	}
	
	public static void loadTiles(){
		blank = new Tile(null,0,false);
		block = new Tile(Images.block,1,true);
		box = new Tile(Images.box,2,true,5,10);
		invis = new Tile(null,3,true);
		start = new Tile(Images.green,0,false);
		invis.setshootable(false);
	}
	
	public static Tile newBox(){
		return new Tile(Images.box, 2, true, 5, 5);
	}
	
}
