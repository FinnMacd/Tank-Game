package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Entity.Bullet;
import Entity.Tank;
import Entity.Turret;
import Main.Inputs;
import Screen.Background;
import Screen.Button;
import Screen.Images;
import Screen.Map;
import Screen.Tile;

public class EditState extends PlayState{
	
	private static String path;
	private static boolean d;
	
	private Tile[] tiles;
	private int selected = 0;
	
	private int mx,my;
	
	private Button save,menu;
	private Turret drawt;
	
	public EditState(GameStateManager gsm) {
		super(gsm);
		cursor = -1;
	}

	public void init() {
		map = new Map(this);
		
		turrets = new ArrayList<Turret>();
		
		tank = new Tank( startx,starty,this);
		bg = new Background("/Backgrounds/Blank.png");
		map.loadMap(path,false);
		tank.setData(startx, starty, 0, 0, 10);
		
		save = new Button("Save", 420, 50, Color.black, new Color(110,0,20), 40);
		menu = new Button("Menu", 520, 50, Color.black, new Color(110,0,20), 40);
		
		Tile[] ttiles = {Tile.block,Tile.box,Tile.blank,Tile.start};
		tiles = ttiles;
		
		drawt = new Turret(-40,-40,this);
	}
	
	public static void setPath(String path,boolean d){
		EditState.path = path;
		EditState.d = d;
	}

	public void update() {
		
		if(Inputs.left)map.moveScreen(5, 0);
		if(Inputs.right)map.moveScreen(-5, 0);
		if(Inputs.down)map.moveScreen(0, -5);
		if(Inputs.up)map.moveScreen(0, 5);
		
		selected = Inputs.notches%4;
		if(selected < 0)selected = -selected;
		
		if(selected == 2)drawt.setPos((mx+1)*20, (my+1)*20);
		else drawt.setPos(-32, -32);
		
		if(Inputs.up)map.y+=2;
		if(Inputs.down)map.y-=2;
		if(Inputs.left)map.x+=2;
		if(Inputs.right)map.x-=2;
		
		save.update();
		menu.update();
		
		if(save.isClicked()){
			save();
			return;
		}
		if(menu.isClicked()){
			gsm.setState(GameStateManager.SELECTIONSTATE);
		}
		
		mx = (Inputs.mx - (int)map.x) /20;
		my = (Inputs.my - (int)map.y) /20;
		if(mx < 0)mx = 0;
		if(mx > map.width-1)mx = map.width-1;
		if(my < 0)my=0;
		if(my > map.height-1)my = map.height-1;
		
		if(Inputs.mleft){
			if(selected < 2){
				map.setTile(mx, my, Integer.toString(tiles[selected].id()));
			}else if(selected == 2){
				map.setTile(mx, my, "5");
				map.setTile(mx+1, my, "3");
				map.setTile(mx, my+1, "3");
				map.setTile(mx+1, my+1, "3");
				turrets.add(new Turret(mx*20,my*20,this));
			}else if(selected == 3){
				map.setTile(mx, my, "4");
				tank.setPos(mx*20, my*20);
			}
		}
		if(Inputs.mright){
			
			for(int i = 0; i < turrets.size(); i++){
				if((mx*20 == turrets.get(i).getX() || (mx+1)*20 == turrets.get(i).getX())&&(my*20 == turrets.get(i).getY() || (my+1)*20 == turrets.get(i).getY())){
					
					map.setTile((int)turrets.get(i).getX()/20, (int)turrets.get(i).getY()/20, Integer.toString(Tile.blank.id()));
					map.setTile((int)turrets.get(i).getX()/20, (int)turrets.get(i).getY()/20-1, Integer.toString(Tile.blank.id()));
					map.setTile((int)turrets.get(i).getX()/20-1, (int)turrets.get(i).getY()/20, Integer.toString(Tile.blank.id()));
					map.setTile((int)turrets.get(i).getX()/20-1, (int)turrets.get(i).getY()/20-1, Integer.toString(Tile.blank.id()));
					
					turrets.remove(i);
					break;
				}
			}
			
			
			map.setTile(mx, my, Integer.toString(Tile.blank.id()));
		}
	}
	
	private void save() {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
			writer.write("Width=" + map.width + "\n");
			writer.write("Height=" + map.height + "\n");
			for (int y = 0; y < map.height; y++) {
				for (int x = 0; x < map.width; x++) {
					writer.write(map.getMap()[x][y] + " ");
				}
				writer.write("\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}
	
	public void draw(Graphics2D g) {
		bg.draw(g);
		map.draw(g);
		tank.draw(g);
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).draw(g);			
		}
		
		g.drawImage(tiles[selected].getImage(), mx*20+(int)map.x, my*20+(int)map.y, null);
		g.setColor(Color.red);
		g.drawRect(mx*20-1+(int)map.x, my*20-1+(int)map.y, 21, 21);
		
		save.draw(g);
		menu.draw(g);
		
		drawt.draw(g);
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
