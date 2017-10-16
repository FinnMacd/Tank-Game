package server;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Map {

	public int width, height, coloffset, rowoffset;
	private int[][] map;
	private Tile[][] tiles;
	private Game game;
	
	private String sendMap = "";

	public Map(Game g) {
		game = g;
	}

	public void loadMap(String path, boolean d) {
		Scanner scanner = null;
		BufferedReader reader = null;
		try {
			if (d) {
				scanner = new Scanner(getClass().getResourceAsStream(path));
				width = Integer.parseInt(scanner.nextLine().split("=")[1]);
				height = Integer.parseInt(scanner.nextLine().split("=")[1]);
				map = new int[width][height];
				for (int y = 0; y < height; y++) {
					String[] bl = scanner.nextLine().split(" ");
					for (int x = 0; x < width; x++) {
						if (bl[x].equals("5")) {
							game.addTurret(x, y);
						}
						map[x][y] = Integer.parseInt(bl[x]);
					}
				}
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
				width = Integer.parseInt(reader.readLine().split("=")[1]);
				height = Integer.parseInt(reader.readLine().split("=")[1]);
				map = new int[width][height];
				for (int y = 0; y < height; y++) {
					String[] bl = reader.readLine().split(" ");
					for (int x = 0; x < width; x++) {
						if (Integer.parseInt(bl[x]) == 5) {
							game.addTurret(x, y);
						}
						map[x][y] = Integer.parseInt(bl[x]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("could not load map");
			JOptionPane.showMessageDialog(null, "could not load map");
		} finally {
			try {
				scanner.close();
				reader.close();
			} catch (Exception e) {
			}
		}

		tiles = new Tile[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (map[x][y] == Tile.block.id())
					tiles[x][y] = Tile.block;
				else if (map[x][y] == Tile.box.id())
					tiles[x][y] = Tile.newBox();
				else if (map[x][y] == Tile.invis.id())
					tiles[x][y] = Tile.invis;
				else if(map[x][y] == Tile.start.id())
					tiles[x][y] = Tile.start;
				else if(map[x][y] == Tile.turret.id())
					tiles[x][y] = Tile.turret;
				else
					tiles[x][y] = Tile.blank;
			}
		}

	}

	public boolean isSolid(int x, int y) {
		if (x < 0) x = 0;
		if (x > width - 1) x = width - 1;
		if (y < 0) y = 0;
		if (y > height - 1) y = height - 1;
		return tiles[x][y].issolid();
	}

	public boolean isShootable(int x, int y) {
		if (x < 0) x = 0;
		if (x > width - 1) x = width - 1;
		if (y < 0) y = 0;
		if (y > height - 1) y = height - 1;
		return tiles[x][y].isshootable();
	}

	public void hurtBlock(int x, int y, double h) {
		if (x < 0) x = 0;
		if (x > width - 1) x = width - 1;
		if (y < 0) y = 0;
		if (y > height - 1) y = height - 1;
		tiles[x][y].hurt(h);
	}

	public void setTile(int x, int y, String data) {
		map[x][y] = Integer.parseInt(data);

		if (map[x][y] == Tile.block.id())
			tiles[x][y] = Tile.block;
		else if (map[x][y] == Tile.box.id())
			tiles[x][y] = Tile.box;
		else if (map[x][y] == Tile.invis.id())
			tiles[x][y] = Tile.invis;
		else
			tiles[x][y] = Tile.blank;

	}

	public Rectangle getRect(int x, int y) {
		return new Rectangle(x * 20, y * 20, 20, 20);
	}

	public int[][] getMap() {
		return map;
	}

	public void setMapForSend() {
		sendMap = "";
		sendMap += width + " " + height + " ";
		
		String tileData = "";
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tileData += tiles[x][y].getCode();
			}
		}
		
		String[] data = tileData.split("");
		
		for(int i = 0; i < data.length;){
			
			int num = 0;
			String repeat = data[i];
			
			while(i < data.length && data[i].equals(repeat)){
				i++;
				num++;
			}
			
			String[] convert = String.valueOf(num).split("");
			
			for(int p = 0; p < convert.length; p++)
				sendMap += changeNumToChar(convert[p]);
			sendMap += repeat;
		}
	}
	
	public String getMapForSend(){
		return sendMap;
	}
	
	public String changeNumToChar(String change){
		
		switch(change){
			case "0":
				return("a");
			case "1":
				return("b");
			case "2":
				return("c");
			case "3":
				return("d");
			case "4":
				return("e");
			case "5":
				return("f");
			case "6":
				return("g");
			case "7":
				return("h");
			case "8":
				return("i");
			case "9":
				return("j");
		}
		return null;
	}

}
