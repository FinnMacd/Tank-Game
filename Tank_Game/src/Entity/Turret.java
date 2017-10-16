package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import GameState.PlayState;
import Screen.Images;

public class Turret {

	private double x, y, health = 15, ang, shootTime, xoffset, yoffset, destang;
	private int tx, ty, ran, turnTime, turnTimer, id;
	private boolean alive = true, hastarget = false, onTarget = false;
	private PlayState state;

	private BufferedImage enemyTurret, enemyBase;

	private AffineTransform trans;

	private Tank[] tanks;
	private Tank tank;

	private static Random turn = new Random();

	public Turret(double x, double y, PlayState state) {
		this.x = x + 20;
		this.y = y + 20;
		this.state = state;
		enemyTurret = Images.t_turret;
		enemyBase = Images.t_base;
	}

	public Turret(double x, double y, PlayState state, int id) {
		this.x = x + 20;
		this.y = y + 20;
		this.state = state;
		enemyTurret = Images.t_turret;
		enemyBase = Images.t_base;
		this.id = id;
	}

	public void update() {
		getTarget();
		if (hastarget) {
			shootTime++;
			xoffset = tx - x;
			yoffset = ty - y;
			destang = Math.toDegrees(Math.atan(yoffset / xoffset)) - 90;
			if (xoffset >= 0)
				destang = 360 + destang;
			else
				destang = 180 + destang;
			if (ang - destang > 180)
				destang += 360;
			else if (destang - ang > 180) ang += 360;
			if (destang < ang - 95 || destang > ang + 95) {
				hastarget = false;
			}
		}
		if (hastarget) {
			if (destang < ang - 10 || destang > ang + 10) {
				onTarget = false;
			} else
				onTarget = true;
			if (ang < destang) ang++;
			if (ang > destang) ang--;
			ang = ang % 360;

			if (health <= 0) alive = false;

			if (shootTime >= 40 && onTarget) {
				state.addBullet(x + Math.sin(Math.toRadians(-ang + 10)) * enemyTurret.getHeight() * 0.75, y + Math.cos(Math.toRadians(ang - 10)) * enemyTurret.getHeight() * 0.75, ang);
				state.addBullet(x + Math.sin(Math.toRadians(-ang - 10)) * enemyTurret.getHeight() * 0.75, y + Math.cos(Math.toRadians(ang + 10)) * enemyTurret.getHeight() * 0.75, ang);
				shootTime = 0;
			}
		} else {
			turnTimer++;
			if (turnTimer > turnTime) {
				ran = turn.nextInt(4);
				turnTime = 30 + turn.nextInt(100);
				turnTimer = 0;
			}
			if (ran == 1) ang += 0.5;
			if (ran == 2) ang -= 0.5;
		}

	}
	
	public void update(double d){
		ang = d;
	}

	private void getTarget() {
		if (Math.sqrt(Math.pow(tank.getX() - x, 2) + Math.pow(tank.getY() - y, 2)) < 300 && tank.isAlive()) {
			tx = (int) tank.getX();
			ty = (int) tank.getY();
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
						if (state.map.isShootable(xc, yc)) {
							hastarget = false;
							return;
						}
					}
				}
			}
			hastarget = true;
		} else
			hastarget = false;
	}

	public void setTank(Tank t) {
		tank = t;
	}

	public void setTanks(Tank[] t) {
		tanks = t;
	}

	public void draw(Graphics2D g) {
		// if(health <= 0)return;
		trans = new AffineTransform();
		trans.translate(x + state.map.x - enemyBase.getWidth() / 2, y + state.map.y - enemyBase.getHeight() / 2);
		g.drawImage(enemyBase, trans, null);

		trans = new AffineTransform();
		trans.translate(x + state.map.x + Math.sin(Math.toRadians(ang)) * (enemyTurret.getWidth() / 2) - Math.cos(Math.toRadians(ang)) * (enemyTurret.getWidth() / 2), y + state.map.y - Math.cos(Math.toRadians(ang)) * (enemyTurret.getWidth() / 2) - Math.sin(Math.toRadians(ang)) * (enemyTurret.getWidth() / 2));
		trans.rotate(Math.toRadians(ang));
		g.drawImage(enemyTurret, trans, null);
	}

	public void hurt(double hurt) {
		health -= hurt;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getAng() {
		return ang;
	}

	public Rectangle getRect() {
		return new Rectangle((int) x - enemyBase.getWidth() / 2, (int) y - enemyBase.getHeight() / 2, enemyBase.getWidth(), enemyBase.getHeight());
	}

	public boolean isAlive() {
		return alive;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setInfo(double ang){
		this.ang = ang;
	}

}
