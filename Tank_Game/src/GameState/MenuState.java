package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import Entity.Tank;
import Main.GamePanel;
import Main.Inputs;
import Screen.Background;
import Screen.Button;

public class MenuState extends GameState{
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
		
	}

	private Button sp, mp;
	
	public void init() {
		sp = new Button("Single Player", 20, 160, Color.black, new Color(110,0,20), 40);
		mp = new Button("Multi Player", 20, 210, Color.black, new Color(110,0,20), 40);
		bg = new Background("/Backgrounds/Blank.png");
	}

	public void update() {
		sp.update();
		mp.update();
		if(sp.isClicked()){
			gsm.setState(GameStateManager.SELECTIONSTATE);
		}
		if(mp.isClicked()){
			gsm.setState(GameStateManager.MULTISTATE);
		}
		
		if(Inputs.cheat[0] && Inputs.cheat[1] && Inputs.cheat[2]){
			Inputs.cheat[0] = Inputs.cheat[1] = Inputs.cheat[2] = false;
			String cheat = JOptionPane.showInputDialog(null, "Enter cheat code", "Cheats!!", JOptionPane.DEFAULT_OPTION);
			if(cheat == null)return;
			if(cheat.length() != 0){
				if(cheat.equals("#Unban")){
					GamePanel.hasCheated = false;
					JOptionPane.showConfirmDialog(null, "Cheat?");
				}
				if(cheat.contains("=")&&cheat.contains("  ")){
					String[] chars = cheat.split("=");
					if(chars.length != 2)return;
					//regen
					if(chars[0].equals("|1|")){
						double d = 0.0;
						try{
							d = Double.parseDouble(chars[1]);
						}catch(Exception e){
							return;
						}
						Tank.setRegen(d);
						JOptionPane.showMessageDialog(null, "Cheat set");
						GamePanel.hasCheated = true;
					}
					//health
					else if(chars[0].equals("|2|")){
						
						JOptionPane.showMessageDialog(null, "Cheat set");
						GamePanel.hasCheated = true;
					}
					//shoot speed
					else if(chars[0].equals("|3|")){
						double d = 0.0;
						try{
							d = Double.parseDouble(chars[1]);
						}catch(Exception e){
							return;
						}
						Tank.setFireSpeed(d);
						JOptionPane.showMessageDialog(null, "Cheat set");
						GamePanel.hasCheated = true;
					}
					//shoot level
					else if(chars[0].equals("|4|")){
						
						JOptionPane.showMessageDialog(null, "Cheat set");
						GamePanel.hasCheated = true;
					}
					//move speed
					else if(chars[0].equals("|5|")){
						double d = 0.0;
						try{
							d = Double.parseDouble(chars[1]);
						}catch(Exception e){
							return;
						}
						Tank.setMoveSpeed(d);
						JOptionPane.showMessageDialog(null, "Cheat set");
						GamePanel.hasCheated = true;
					}
				}
			}
		}
		
	}

	public void draw(Graphics2D g) {
		bg.draw(g);
		sp.draw(g);
		mp.draw(g);
		g.setColor(new Color(110,0,20));
		g.setFont(new Font("TimesNewRoman",0,80));
		g.drawString("Tank Wars!", 40, 90);
	}

	public boolean loaded() {
		if(!loaded){
			loaded = true;
			return false;
		}
		return true;
	}

}
