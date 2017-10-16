package Screen;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JOptionPane;

import GameState.PlayState;
import Main.GamePanel;

public class Map {

	public double x = 0, y = 0;
	public int width, height, coloffset, rowoffset;
	private int[][] map;
	private Tile[][] tiles;
	private PlayState state;
	
	public boolean hasLoaded = false; 

	public Map(PlayState p) {
		state = p;
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
						if (bl[x].equals("3")) {
							state.addTurret(x, y);
						}
						if (bl[x].equals("-2")) {
							state.setStart(x, y);
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
						if (Integer.parseInt(bl[x]) == 3) {
							state.addTurret(x, y);
						}
						if (Integer.parseInt(bl[x]) == -2) {
							state.setStart(x, y);
						}
						map[x][y] = Integer.parseInt(bl[x]);
					}
				}
			}
		} catch (Exception e) {
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
					tiles[x][y] = new Tile(Images.box, 2, true, 5, 5);
				else if (map[x][y] == Tile.invis.id() || map[x][y] == 3)
					tiles[x][y] = Tile.invis;
				else
					tiles[x][y] = Tile.blank;
			}
		}
		hasLoaded = true;
	}

	public void loadMapFromString(String map) {

		String[] seperates = map.split(" ");

		width = Integer.parseInt(seperates[0]);
		height = Integer.parseInt(seperates[1]);

		String[] tokens = seperates[2].split("");

		String data = "";

		for (int i = 0; i < tokens.length - 1;) {
			
			if(!isChar(tokens[i])){
				i++;
				continue;
			}
			
			String count = "";

			while (isChar(tokens[i])) {
				count += tokens[i];
				i++;
			}

			String[] countChars = count.split("");
			int trueCount = 0;

			for (int p = countChars.length - 1; p >= 0; p--) {

				trueCount += Math.pow(10, countChars.length - p - 1) * changeCharToNum(countChars[p]);

			}

			for (int p = 0; p < trueCount; p++) {
				data += tokens[i];
			}
		}
		
		System.out.println("data: " + data);
		
		String[] dataArry = data.split("");

		tiles = new Tile[width][height];

		int counter = 0;
		for (int i = 0; i < dataArry.length; i++) {
			if(counter >= width*height)break;
			switch (Integer.parseInt(dataArry[i])) {
				case 0:
					tiles[counter % width][counter / width] = Tile.blank;
					break;
				case 1:
					tiles[counter % width][counter / width] = Tile.block;
					break;
				case 2:
					tiles[counter % width][counter / width] = Tile.newBox().setHealth(Double.parseDouble(dataArry[i + 1])+(Double.parseDouble(dataArry[i+2])/10)); 
					i+=2;
					break;
				case 3:
					tiles[counter % width][counter / width] = Tile.invis;
					break;
				case 4:
					tiles[counter % width][counter / width] = Tile.blank;
					state.setStart(counter % width, counter % width);
					break;
				case 5:
					tiles[counter % width][counter / width] = Tile.invis;
					state.addTurret(counter % width,counter / width);
					break;
			}

			counter++;
		}
		
		hasLoaded = true;

	}

	public int changeCharToNum(String change) {

		switch (change) {
		case "a":
			return (0);
		case "b":
			return (1);
		case "c":
			return (2);
		case "d":
			return (3);
		case "e":
			return (4);
		case "f":
			return (5);
		case "g":
			return (6);
		case "h":
			return (7);
		case "i":
			return (8);
		case "j":
			return (9);
		}
		return 0;
	}

	public boolean isChar(String s) {
		return !(s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6") || s.equals("7") || s.equals("8") || s.equals("9"));
	}

	public void slideScreen(double x, double y) {
		this.x += (x - this.x) * 0.07;
		this.y += (y - this.y) * 0.07;
		fixbounds();
	}

	public void moveScreen(double x, double y) {
		this.x += x;
		this.y += y;
		fixbounds();
	}

	public void setOffsets(double x, double y) {
		this.x = x;
		this.y = y;
		fixbounds();
	}

	private void fixbounds() {
		if (x > 0) x = 0;
		if (x < width * -20 + GamePanel.WIDTH) x = width * -20 + GamePanel.WIDTH;
		if (y > 0) y = 0;
		if (y < height * -20 + GamePanel.HEIGHT) y = height * -20 + GamePanel.HEIGHT;

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
		if(tiles[x][y].isDestructable())
			tiles[x][y].hurt(h);
	}

	public void setTile(int x, int y, String data) {
		int id = Integer.parseInt(data);

		if (id == Tile.block.id())
			tiles[x][y] = Tile.block;
		else if (id == Tile.box.id())
			tiles[x][y] = Tile.newBox();
		else if (id == Tile.invis.id())
			tiles[x][y] = Tile.invis;
		else
			tiles[x][y] = Tile.blank;

	}

	public void setSpawn(int x, int y) {
		setTile(state.startx / 20, state.starty / 20, "0");

		map[x][y] = Integer.parseInt("-2");
		tiles[x][y] = Tile.blank;

	}

	public Rectangle getRect(int x, int y) {
		return new Rectangle(x * 20, y * 20, 20, 20);
	}

	public void draw(Graphics2D g) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				try {
					if (tiles[x][y].id() >= 1) {
						g.drawImage(tiles[x][y].getImage(), (int) ((x * 20) + this.x), (int) ((y * 20) + this.y), null);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int[][] getMap() {
		return map;
	}
}
