package GameState;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Main.Game;

public class GameStateManager {
	
	public final static int MenuState = 0, SINGLESTATE = 1, MULTISTATE = 2,SELECTIONSTATE = 3,EDITSTATE = 4;
	
	private ArrayList<GameState> gameStates;
	
	private int currentState = 0;
	
	public GameStateManager(){
		
		gameStates = new ArrayList<GameState>();
		gameStates.add(new MenuState(this));
		gameStates.add(new SinglePlayerState(this));
		gameStates.add(new MultiPlayerState(this));
		gameStates.add(new SelectionState(this));
		gameStates.add(new EditState(this));
		
		setState(MenuState);
	}
	
	public void setState(int state){
		currentState = state;
		//if(!gameStates.get(currentState).loaded()){
			gameStates.get(currentState).init();
		//}
		if(gameStates.get(currentState).cursor == -1){
			Game.frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB), new Point(0,0), "blank"));
		}
		else Game.frame.setCursor(gameStates.get(currentState).cursor);
	}
	
	public int getState(){
		return currentState;
	}
	
	public void update(){
		gameStates.get(currentState).update();
	}
	
	public void draw(Graphics2D g){
		gameStates.get(currentState).draw(g);
	}
	
}
