package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import GameState.PlayState;
import Main.GamePanel;
import Main.Inputs;
import Screen.Images;

public class Tank {

	private double turret_ang = 0, tank_ang = 0, mx, my, x, y, health = 10.0, maxhealth = health;

	private final String UserName;

	private int fireOffSet = 0, kills, deaths;

	private boolean left, right, up, down, m1down, m2down, alive = true;

	private BufferedImage turret, tank;

	private PlayState state;

	private Font font = new Font("TimesNewRoman", 1, 16);
	private FontMetrics fm;
	
	private static double regen = 0.004,fireSpeed = 10,moveSpeed = 1;

	private long death;

	public Tank(int x, int y, PlayState state) {
		UserName = "";
		this.x = x;
		this.y = y;

		turret = Images.p_turret;
		tank = Images.p_tank;

		this.state = state;

	}

	public Tank(int x, int y, PlayState state, String UserName) {
		this.UserName = UserName;
		this.x = x;
		this.y = y;

		turret = Images.p_turret;
		tank = Images.p_tank;

		this.state = state;
	}

	public void update() {
		if (alive) {
			if (UserName.isEmpty()) {
				updateInputs();
				fireOffSet++;

				move();

				double xoffset = mx - x, yoffset = my - y;
				turret_ang = Math.toDegrees(Math.atan(yoffset / xoffset)) - 90;
				if (mx < x) turret_ang += 180;

				turret_ang = turret_ang % 360;
				if (turret_ang < 0) turret_ang = 360 + turret_ang;

				if (fireOffSet > fireSpeed && m1down) {
					fireOffSet = 0;
					state.addBullet(x + Math.sin(Math.toRadians(-turret_ang)) * turret.getHeight() * 0.89, y + Math.cos(Math.toRadians(-turret_ang)) * turret.getHeight() * 0.89, turret_ang);
				}
				if (fireOffSet > fireSpeed*3 && m2down) {
					fireOffSet = 0;
					state.addBullet(x + Math.sin(Math.toRadians(-turret_ang)) * turret.getHeight() * 0.89, y + Math.cos(Math.toRadians(-turret_ang)) * turret.getHeight() * 0.89, turret_ang, mx, my);
				}
				if (health < maxhealth) health += regen;
				if(health > maxhealth)health = maxhealth;

				if (x < 0) x = 0;
				if (x > state.map.width * 20) x = state.map.width * 20;
				if (y < 0) y = 0;
				if (y > state.map.height * 20) y = state.map.height * 20;
			}
			if (health <= 0.0) {
				alive = false;
				deaths++;
				death = System.currentTimeMillis() + 5000;
			}
		} else {
			if (death - System.currentTimeMillis() <= 0 || health >= 5) {
				alive = true;
				setData(state.startx, state.starty, 0, 0, maxhealth);
			}
		}
	}

	private void move() {

		if (up) {
			int x1 = (int) ((this.x + Math.sin(Math.toRadians(-tank_ang + 38.7)) * tank.getHeight() * 0.66) / 20), x2 = (int) ((this.x + Math.sin(Math.toRadians(-tank_ang - 38.7)) * tank.getHeight() * 0.66) / 20);
			int y1 = (int) ((this.y + Math.cos(Math.toRadians(-tank_ang - 38.7)) * tank.getHeight() * 0.66) / 20), y2 = (int) ((this.y + Math.cos(Math.toRadians(-tank_ang + 38.7)) * tank.getHeight() * 0.66) / 20);
			if (x1 > x2) {
				int tempx = x1;
				x1 = x2;
				x2 = tempx;
			}
			if (y1 > y2) {
				int tempy = y1;
				y1 = y2;
				y2 = tempy;
			}
			for (int x = x1 - 1; x < x2 + 1; x++) {
				for (int y = y1 - 1; y < y2 + 1; y++) {
					if (PlayState.map.isSolid(x, y)) {
						if (getPoly(0.53).intersects(PlayState.map.getRect(x, y))) up = false;
					}
				}
			}
		}

		if (down) {
			int x1 = (int) ((this.x + Math.sin(Math.toRadians(-tank_ang + 38.7 + 180)) * tank.getHeight() * 0.66) / 20), x2 = (int) ((this.x + Math.sin(Math.toRadians(-tank_ang - 38.7 + 180)) * tank.getHeight() * 0.66) / 20);
			int y1 = (int) ((this.y + Math.cos(Math.toRadians(-tank_ang - 38.7 + 180)) * tank.getHeight() * 0.66) / 20), y2 = (int) ((this.y + Math.cos(Math.toRadians(-tank_ang + 38.7 + 180)) * tank.getHeight() * 0.66) / 20);
			if (x1 > x2) {
				int tempx = x1;
				x1 = x2;
				x2 = tempx;
			}
			if (y1 > y2) {
				int tempy = y1;
				y1 = y2;
				y2 = tempy;
			}
			for (int x = x1 - 1; x < x2 + 1; x++) {
				for (int y = y1 - 1; y < y2 + 1; y++) {
					if (PlayState.map.isSolid(x, y)) {
						if (getPoly(0.53).intersects(PlayState.map.getRect(x, y))) down = false;
					}
				}
			}
		}

		if (up) {
			y += Math.cos(Math.toRadians(-tank_ang))*moveSpeed;
			x += Math.sin(Math.toRadians(-tank_ang))*moveSpeed;
		}
		if (down) {
			y -= Math.cos(Math.toRadians(-tank_ang)) * 0.8*moveSpeed;
			x -= Math.sin(Math.toRadians(-tank_ang)) * 0.8*moveSpeed;
		}

		if (left) {

			tank_ang--;

			int[] xp = getPoly().xpoints;
			int[] yp = getPoly().ypoints;

			int x1 = xp[0] / 20, x2 = xp[0] / 20, y1 = yp[0] / 20, y2 = yp[0] / 20;

			for (int i = 1; i < 4; i++) {
				if (xp[i] / 20 < x1)
					x1 = xp[i] / 20;
				else if (xp[i] / 20 > x2) x2 = xp[i] / 20;
				if (yp[i] / 20 < y1)
					y1 = yp[i] / 20;
				else if (yp[i] / 20 > y2) y2 = yp[i] / 20;
			}

			for (int x = x1 - 1; x < x2 + 1; x++) {
				for (int y = y1 - 1; y < y2 + 1; y++) {
					if (PlayState.map.isSolid(x, y)) {
						if (getPoly(0.5).intersects(PlayState.map.getRect(x, y))) left = false;
					}
				}
			}

			tank_ang++;

		}

		if (right) {

			tank_ang++;

			int[] xp = getPoly().xpoints;
			int[] yp = getPoly().ypoints;

			int x1 = xp[0] / 20, x2 = xp[0] / 20, y1 = yp[0] / 20, y2 = yp[0] / 20;

			for (int i = 1; i < 4; i++) {
				if (xp[i] / 20 < x1)
					x1 = xp[i] / 20;
				else if (xp[i] / 20 > x2) x2 = xp[i] / 20;
				if (yp[i] / 20 < y1)
					y1 = yp[i] / 20;
				else if (yp[i] / 20 > y2) y2 = yp[i] / 20;
			}

			for (int x = x1 - 1; x < x2 + 1; x++) {
				for (int y = y1 - 1; y < y2 + 1; y++) {
					if (PlayState.map.isSolid(x, y)) {
						if (getPoly(0.5).intersects(PlayState.map.getRect(x, y))) right = false;
					}
				}
			}

			tank_ang--;

		}

		if (left) tank_ang -= 1+(moveSpeed-1)*.5;
		if (right) tank_ang += 1+(moveSpeed-1)*.5;
	}

	public void draw(Graphics2D g) {
		if (alive) {
			if (fm == null) fm = g.getFontMetrics(font);

			g.setColor(Color.red);
			g.fillRect((int) x - 20 + (int) state.map.x, (int) y + 30 + (int) state.map.y, 40, 5);
			g.setColor(Color.green);
			g.fillRect((int) x - 20 + (int) state.map.x, (int) y + 30 + (int) state.map.y, (int) ((health / maxhealth) * 40), 5);

			AffineTransform trans = new AffineTransform();
			trans.translate(x + (int) state.map.x + Math.sin(Math.toRadians(tank_ang)) * (tank.getHeight() / 2) - Math.cos(Math.toRadians(tank_ang)) * (tank.getWidth() / 2), y + (int) state.map.y - Math.cos(Math.toRadians(tank_ang)) * (tank.getHeight() / 2) - Math.sin(Math.toRadians(tank_ang)) * (tank.getWidth() / 2));
			trans.rotate(Math.toRadians(tank_ang));
			g.drawImage(tank, trans, null);

			trans = new AffineTransform();
			trans.translate(x + (int) state.map.x + Math.sin(Math.toRadians(turret_ang)) * (turret.getWidth() / 2) - Math.cos(Math.toRadians(turret_ang)) * (turret.getWidth() / 2), y + (int) state.map.y - Math.cos(Math.toRadians(turret_ang)) * (turret.getWidth() / 2) - Math.sin(Math.toRadians(turret_ang)) * (turret.getWidth() / 2));
			trans.rotate(Math.toRadians(turret_ang));
			g.drawImage(turret, trans, null);

			g.setFont(font);
			g.setColor(Color.green);
			if (!UserName.equals("")) g.drawString(UserName, (int) x - fm.stringWidth(UserName) / 2 + (int) state.map.x, (int) y - 35 + (int) state.map.y);

		} else {
			g.setFont(font);
			g.setColor(Color.red);
			if (!UserName.equals(""))
				g.drawString(UserName, (int) x - fm.stringWidth(UserName) / 2 + (int) state.map.x, (int) y - 35 + (int) state.map.y);
			else
				g.drawString("respawning in", (int) x - fm.stringWidth("respawning in") / 2 + (int) state.map.x, (int) y - 35 + (int) state.map.y);

			g.setFont(new Font("TimesNewRoman", 0, 24));
			g.setColor(Color.black);
			g.drawString("" + (int) ((death - System.currentTimeMillis()) / 1000 + 1), (int) x - 12 + (int) state.map.x, (int) y + 12 + (int) state.map.y);
		}
	}

	public void setData(double x, double y, double ta, double tu, double h) {
		this.x = x;
		this.y = y;
		turret_ang = tu;
		tank_ang = ta;
		health = h;
	}
	
	public void setPos(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void setKD(int k, int d) {
		kills = k;
		deaths = d;
	}

	public void hurt(double damage) {
		health -= damage;
	}

	public Polygon getPoly(double d) {
		int[] x = { (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7 + 180)) * tank.getHeight() * d), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7 + 180)) * tank.getHeight() * d), (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7)) * tank.getHeight() * d), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7)) * tank.getHeight() * d) };

		int[] y = { (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7 + 180)) * tank.getHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7 + 180)) * tank.getHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7)) * tank.getHeight() * d), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7)) * tank.getHeight() * d) };

		return new Polygon(x, y, 4);
	}

	public Polygon getPoly() {
		int[] x = { (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7 + 180)) * tank.getHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7 + 180)) * tank.getHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang + 38.7)) * tank.getHeight() * 0.66), (int) (this.x + Math.sin(Math.toRadians(-tank_ang - 38.7)) * tank.getHeight() * 0.66) };

		int[] y = { (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7 + 180)) * tank.getHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7 + 180)) * tank.getHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang - 38.7)) * tank.getHeight() * 0.66), (int) (this.y + Math.cos(Math.toRadians(tank_ang + 38.7)) * tank.getHeight() * 0.66) };

		return new Polygon(x, y, 4);
	}

	public void updateInputs() {
		if (!Inputs.focus) {
			left = right = up = down = false;
			return;
		}
		mx = Inputs.mx - PlayState.map.x;
		my = Inputs.my - PlayState.map.y;
		left = Inputs.left;
		right = Inputs.right;
		up = Inputs.up;
		down = Inputs.down;
		m1down = Inputs.mleft;
		m2down = Inputs.mright;
	}
	
	public static void setRegen(double d){
		regen = d;
	}
	
	public static void setFireSpeed(double d){
		fireSpeed = d;
	}
	
	public static void setMoveSpeed(double d){
		moveSpeed = d;
	}
	
	public double getX() {
		return Math.ceil(x * 10) / 10;
	}

	public double getY() {
		return Math.ceil(y * 10) / 10;
	}

	public double getTankAng() {
		return Math.ceil(tank_ang * 10) / 10;
	}

	public double getTurretAng() {
		return Math.ceil(turret_ang * 10) / 10;
	}

	public double getHealth() {
		return Math.ceil(health * 10) / 10;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public boolean isAlive() {
		return alive;
	}

	public String getUserName() {
		return UserName;
	}

}
