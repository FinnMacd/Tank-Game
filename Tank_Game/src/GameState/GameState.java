package GameState;

import java.awt.Cursor;
import java.awt.Graphics2D;

import Screen.Background;

public abstract class GameState {
	
	protected boolean loaded = false;
	protected Background bg;
	protected GameStateManager gsm;
	protected int cursor = Cursor.DEFAULT_CURSOR;
	
	public GameState(GameStateManager gsm){
		this.gsm = gsm;
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract boolean loaded();
	
}
